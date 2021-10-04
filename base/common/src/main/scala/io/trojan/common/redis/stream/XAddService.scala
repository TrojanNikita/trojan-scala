package io.trojan.common.redis.stream

import io.trojan.common.models.RedisCodec
import io.trojan.common.redis.RedisClient
import org.redisson.api.StreamMessageId

trait XAddService[F[_]] {
  def add[T: RedisCodec](t: T): F[StreamMessageId]
}

object XAddService {
  class Impl[F[_]](client: RedisClient[F], streamName: String) extends XAddService[F] {

    def add[T: RedisCodec](t: T): F[StreamMessageId] = client.xAdd(streamName, RedisCodec[T].encode(t))
  }
}
