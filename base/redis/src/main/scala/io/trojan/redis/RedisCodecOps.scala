package io.trojan.redis

import scala.util.Try

import io.circe.{Decoder, Encoder, parser}
import io.circe.syntax.EncoderOps

object RedisCodecOps {
  implicit class RedisEncoder[T: Encoder](private val value: T) {
    def encode(): String = value.asJson.noSpaces
  }

  implicit class RedisDecoder[T: Decoder](private val value: String) {
    def decode(): Try[T] = parser.parse(value).flatMap(_.as[T]).toTry
  }
}
