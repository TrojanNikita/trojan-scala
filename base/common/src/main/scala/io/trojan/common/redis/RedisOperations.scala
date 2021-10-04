package io.trojan.common.redis

import java.util.concurrent.TimeUnit

import scala.jdk.CollectionConverters.{IterableHasAsScala, ListHasAsScala, MapHasAsJava, MapHasAsScala}

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
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

  def sAdd(key: String, value: String): F[Boolean]

  def sMembers(key: String): F[Set[String]]

  def setEx(key: String, seconds: Long, value: String): F[Unit]

  def get(key: String): F[Option[String]]
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

    def xAdd(key: String, kv: Map[String, String]): F[StreamMessageId] = {
      stream(key).add(StreamAddArgs.entries(kv.asJava)).pure[F]
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

    override def sAdd(key: String, value: String): F[Boolean] = {
      set(key).add(value).pure[F]
    }

    override def sMembers(key: String): F[Set[String]] = {
      set(key).readAll().asScala.toSet.pure[F]
    }

    override def setEx(key: String, seconds: Long, value: String): F[Unit] = {
      bucket(key).set(value, seconds, TimeUnit.SECONDS).pure[F]
    }

    override def get(key: String): F[Option[String]] = {
      val nullableValue = bucket(key).get()

      Option(nullableValue).pure[F]
    }
  }
}