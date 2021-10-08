package io.trojan.user_service.service

import cats.effect.LiftIO
import cats.effect.kernel.Sync
import io.trojan.redis.{RedisClient, RedisCodec}
import org.redisson.api.StreamMessageId
import org.typelevel.log4cats.Logger

class RedisService[F[_] : Sync : LiftIO](
  client: RedisClient[F]
)(implicit L: Logger[F]) {

  def addToStream[T: RedisCodec](t: T, streamName: String): F[StreamMessageId] = {
    client.xAdd(streamName, RedisCodec[T].encode(t))
  }
}

