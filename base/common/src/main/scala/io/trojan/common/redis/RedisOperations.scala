package io.trojan.common.redis

import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.jdk.CollectionConverters.{IterableHasAsScala, ListHasAsScala, MapHasAsJava, MapHasAsScala}
import scala.util.Success

import cats.Applicative
import cats.effect.kernel.Sync
import cats.implicits.catsSyntaxApplicativeId
import io.circe.{Decoder, Encoder}
import io.trojan.common.redis.RedisCodecOps.{RedisDecoder, RedisEncoder}
import org.redisson.api._
import org.redisson.api.stream.{StreamAddArgs, StreamReadGroupArgs}
import org.redisson.client.codec.StringCodec

trait RedisOperations[F[_]] {
  def xGroupCreate(key: String, group: String): F[Unit]

  def xAdd(key: String, kv: Map[String, String]): F[StreamMessageId]

  def xDel(key: String, id: StreamMessageId): F[Long]

  def xAck(key: String, group: String, ids: Set[StreamMessageId]): F[Long]

  def xPending(
    key: String,
    group: String,
    startId: StreamMessageId,
    endId: StreamMessageId,
    count: Int
  ): F[List[PendingEntry]]

  def xClaim(
    key: String,
    group: String,
    consumer: String,
    idleSeconds: Long,
    ids: Set[StreamMessageId]
  ): F[Map[StreamMessageId, Map[String, String]]]

  def xReadGroup(
    key: String,
    group: String,
    consumer: String,
    count: Int
  ): F[Map[StreamMessageId, Map[String, String]]]

  def scan(pattern: String): F[Iterable[String]]

  def del(keySet: Set[String]): F[Long]

  def sAdd[T: Encoder](key: String, value: T): F[Boolean]

  def sMembers(key: String): F[Set[String]]

  def setEx[T: Encoder](key: String, seconds: Long, value: T): F[Unit]

  def get[T: Decoder](key: String): F[Option[T]]
}

object RedisOperations {
  abstract class Client[F[_] : Applicative] extends RedisOperations[F] {

    protected val client: RedissonClient

    private lazy val keys = client.getKeys
    private lazy val set: String => RSet[String] = client.getSet[String](_, StringCodec.INSTANCE)
    private lazy val bucket: String => RBucket[String] = client.getBucket[String](_, StringCodec.INSTANCE)
    private lazy val stream: String => RStream[String, String] = client.getStream[String, String](_, StringCodec.INSTANCE)

    override def xGroupCreate(key: String, group: String): F[Unit] = {
      stream(key).createGroup(group).pure[F]
    }

    override def xAdd[T: Encoder](key: String, kv: (String, T)): F[StreamMessageId] = {
      import kv.{_1 => objKey, _2 => objValue}
      stream(key).add(StreamAddArgs.entry(objKey, objValue.encode())).pure[F]
    }

    override def xDel(key: String, id: StreamMessageId): F[Long] = {
      stream(key).remove(id).pure[F]
    }

    override def xAck(key: String, group: String, ids: Set[StreamMessageId]): F[Long] = {
      stream(key).ack(group, ids.toSeq: _*).pure[F]
    }

    override def xPending(
      key: String,
      group: String,
      startId: StreamMessageId,
      endId: StreamMessageId,
      count: Int
    ): F[List[PendingEntry]] = {
      stream(key).listPending(group, startId, endId, count).asScala.toList.pure[F]
    }

    override def xClaim(
      key: String,
      group: String,
      consumer: String,
      idleSeconds: Long,
      ids: Set[StreamMessageId]
    ): F[Map[StreamMessageId, Map[String, String]]] = {
      stream(key)
        .claim(group, consumer, idleSeconds, TimeUnit.SECONDS, ids.toSeq: _*)
        .asScala
        .map { case (key, values) => (key, values.asScala.toMap) }
        .toMap
        .pure[F]
    }

    override def xReadGroup(
      key: String,
      group: String,
      consumer: String,
      count: Int
    ): F[Map[StreamMessageId, Map[String, String]]] = {
      val args = StreamReadGroupArgs.neverDelivered().count(count)

      stream(key)
        .readGroup(group, consumer, args)
        .asScala
        .toMap
        .map { case (key, values) => (key, values.asScala.toMap) }
        .pure[F]
    }

    override def scan(pattern: String): F[Iterable[String]] = {
      keys.getKeysByPattern(pattern).asScala.pure[F]
    }

    override def del(keySet: Set[String]): F[Long] = {
      keys.delete(keySet.toList: _*).pure[F]
    }

    override def sAdd[T: Encoder](key: String, value: T): F[Boolean] = {
      set(key).add(value.encode()).pure[F]
    }

    override def sMembers(key: String): F[Set[String]] = {
      set(key).readAll().asScala.toSet.pure[F]
    }

    override def setEx[T: Encoder](key: String, seconds: Long, value: T): F[Unit] = {
      bucket(key).set(value.encode(), seconds, TimeUnit.SECONDS).pure[F]
    }

    override def get[T: Decoder](key: String): F[Option[T]] = {
      val nullableValue = bucket(key).get()

      Option(nullableValue)
        .map(_.decode())
        .collect {
          case Success(v) => v
        }
        .pure[F]
    }
  }
}