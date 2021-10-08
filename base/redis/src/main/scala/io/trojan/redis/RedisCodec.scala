package io.trojan.redis

import scala.util.Try

trait RedisCodec[T] {
  def decode: Map[String, String] => Try[T]
  def encode: T => Map[String, String]
}

object RedisCodec {
  def apply[T](implicit ev: RedisCodec[T]): RedisCodec[T] = ev
}