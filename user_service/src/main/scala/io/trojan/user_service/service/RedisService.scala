package io.trojan.user_service.service

import cats.effect.LiftIO
import cats.effect.kernel.Sync
import io.trojan.common.redis.RedisClient
import io.trojan.common.redis.stream.XAddService
import io.trojan.user_service.config.Config
import org.typelevel.log4cats.Logger

class RedisService[F[_] : Sync : LiftIO](
  client: RedisClient[F]
)(implicit config: Config, L: Logger[F])
  extends XAddService.Impl[F](client, config.redis.dataBus)

