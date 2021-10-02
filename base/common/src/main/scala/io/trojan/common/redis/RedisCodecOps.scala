package io.trojan.common.redis
import scala.util.Try

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, parser}

object RedisCodecOps {
  implicit class RedisEncoder[T: Encoder](private val value: T) {
    def encode(): String = value.asJson.noSpaces
  }

  implicit class RedisDecoder[T: Decoder](private val value: String) {
    def decode(): Try[T] = parser.parse(value).flatMap(_.as[T]).toTry
  }
}

