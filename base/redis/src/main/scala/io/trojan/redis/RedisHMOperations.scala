package io.trojan.redis

import scala.jdk.CollectionConverters.MapHasAsScala

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import org.redisson.api.{RMap, RedissonClient}
import org.redisson.client.codec.StringCodec

trait RedisHMOperations[F[_]] {

  protected val client: RedissonClient

  private lazy val map: String => RMap[String, String] = client.getMap[String, String](_, StringCodec.INSTANCE)

  def hSet(key: String, value: (String, String))(implicit a: Applicative[F]): F[Boolean] = {
    map(key).fastPut(value._1, value._2).pure[F]
  }

  def hGet(key: String, field: String)(implicit a: Applicative[F]): F[Option[String]] = {
    Option(map(key).get(field)).pure[F]
  }

  def hGetAll(key: String)(implicit a: Applicative[F]): F[List[(String, String)]] = {
    map(key).readAllMap().asScala.toList.pure[F]
  }
}
