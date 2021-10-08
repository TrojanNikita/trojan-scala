package io.trojan.redis.stream

import cats.Applicative
import io.trojan.redis.{RedisClient, RedisCodec}
import org.redisson.api.StreamMessageId

trait XAddService[F[_]] {
  def add[T: RedisCodec](t: T): F[StreamMessageId]
}

object XAddService {
  class Impl[F[_]: Applicative](client: RedisClient[F], streamName: String) extends XAddService[F] {

    def add[T: RedisCodec](t: T): F[StreamMessageId] = client.xAdd(streamName, RedisCodec[T].encode(t))
  }
}
