package io.trojan.redis

import java.util.concurrent.TimeUnit

import scala.jdk.CollectionConverters.{CollectionHasAsScala, IterableHasAsScala, MapHasAsJava, MapHasAsScala}

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import org.redisson.api.stream.{StreamAddArgs, StreamReadGroupArgs}
import org.redisson.api.{PendingEntry, RBucket, RSet, RStream, RedissonClient, StreamMessageId}
import org.redisson.client.codec.StringCodec

trait RedisStreamOperations[F[_]] {

  protected val client: RedissonClient

  private lazy val keys = client.getKeys
  private lazy val set: String => RSet[String] = client.getSet[String](_, StringCodec.INSTANCE)
  private lazy val bucket: String => RBucket[String] = client.getBucket[String](_, StringCodec.INSTANCE)
  private lazy val stream: String => RStream[String, String] = client.getStream[String, String](_, StringCodec.INSTANCE)

  def xGroupCreate(key: String, group: String)(implicit a: Applicative[F]): F[Unit] = {
    stream(key).createGroup(group).pure[F]
  }

  def xAdd(key: String, kv: Map[String, String])(implicit a: Applicative[F]): F[StreamMessageId] = {
    stream(key).add(StreamAddArgs.entries(kv.asJava)).pure[F]
  }

  def xDel(key: String, id: StreamMessageId)(implicit a: Applicative[F]): F[Long] = {
    stream(key).remove(id).pure[F]
  }

  def xAck(key: String, group: String, ids: Set[StreamMessageId])(implicit a: Applicative[F]): F[Long] = {
    stream(key).ack(group, ids.toSeq: _*).pure[F]
  }

  def xPending(
    key: String,
    group: String,
    startId: StreamMessageId,
    endId: StreamMessageId,
    count: Int
  )(implicit a: Applicative[F]): F[List[PendingEntry]] = {
    stream(key).listPending(group, startId, endId, count).asScala.toList.pure[F]
  }

  def xClaim(
    key: String,
    group: String,
    consumer: String,
    idleSeconds: Long,
    ids: Set[StreamMessageId]
  )(implicit a: Applicative[F]): F[Map[StreamMessageId, Map[String, String]]] = {
    stream(key)
      .claim(group, consumer, idleSeconds, TimeUnit.SECONDS, ids.toSeq: _*)
      .asScala
      .map { case (key, values) => (key, values.asScala.toMap) }
      .toMap
      .pure[F]
  }

  def xReadGroup(
    key: String,
    group: String,
    consumer: String,
    count: Int
  )(implicit a: Applicative[F]): F[Map[StreamMessageId, Map[String, String]]] = {
    val args = StreamReadGroupArgs.neverDelivered().count(count)

    stream(key)
      .readGroup(group, consumer, args)
      .asScala
      .toMap
      .map { case (key, values) => (key, values.asScala.toMap) }
      .pure[F]
  }

  def scan(pattern: String)(implicit a: Applicative[F]): F[Iterable[String]] = {
    keys.getKeysByPattern(pattern).asScala.pure[F]
  }

  def del(keySet: Set[String])(implicit a: Applicative[F]): F[Long] = {
    keys.delete(keySet.toList: _*).pure[F]
  }

  def sAdd(key: String, value: String)(implicit a: Applicative[F]): F[Boolean] = {
    set(key).add(value).pure[F]
  }

  def sMembers(key: String)(implicit a: Applicative[F]): F[Set[String]] = {
    set(key).readAll().asScala.toSet.pure[F]
  }

  def setEx(key: String, seconds: Long, value: String)(implicit a: Applicative[F]): F[Unit] = {
    bucket(key).set(value, seconds, TimeUnit.SECONDS).pure[F]
  }

  def get(key: String)(implicit a: Applicative[F]): F[Option[String]] = {
    val nullableValue = bucket(key).get()
    Option(nullableValue).pure[F]
  }
}
