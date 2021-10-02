package io.trojan.user_service.service

import scala.util.Try

import cats.effect.LiftIO
import cats.effect.kernel.Sync
import cats.implicits._
import cats.syntax._
import io.circe.Codec
import io.trojan.common.models.User
import io.trojan.common.redis.RedisClient
import io.trojan.user_service.config.Config
import io.trojan.user_service.models.RedisEvent
import org.redisson.api.StreamMessageId
import org.typelevel.log4cats.Logger

class RedisService[F[_] : Sync : LiftIO](client: RedisClient[F])(implicit config: Config, L: Logger[F]) {

  def setUsers(users: List[User]): F[Unit] = {
    client.setEx("users", 10000, users)
  }

  def getUsers(): F[Option[List[User]]] = {
    client.get("users")
  }

  def initGroup: F[Unit] = {
    client.xGroupCreate(config.redis.dataBus, config.redis.group)
  }

  def readStreamEvents(consumer: String): F[List[User]] = {
    client
      .xReadGroup(config.redis.dataBus, config.redis.group, consumer, config.redis.batchSize)
      .map(_.map {case (_, keys) => keys.values })
  }

  def addUser(u: User): F[StreamMessageId] = {
    client.xAdd(config.redis.group, "user" -> u)
  }



  def ackTask(id: StreamMessageId): F[Unit] =
    client.xAck(config.redis.dataBus, config.redis.group, Set(id)).void

  private def parseEvents[R: Codec](taskMessages: Try[R]): F[List[RedisEvent[R]]] = {
    taskMessages.toList.traverseFilter { case (id, eventTry) =>
      Sync[F]
        .fromTry(eventTry).map(event => RedisEvent(id, event).some)
        .handleErrorWith(error => ackTask(id) *> L.error(s"${error.getLocalizedMessage}").map(_ => Option.empty))
    }
  }
}
