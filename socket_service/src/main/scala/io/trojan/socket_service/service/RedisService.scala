package io.trojan.socket_service.service

import scala.util.Success

import cats.effect.kernel.Sync
import cats.implicits._
import io.circe.Codec
import io.trojan.common.models.{RedisCodec, RedisEvent}
import io.trojan.common.redis.RedisClient
import io.trojan.common.utils.Helpers.applyOrEmptyF
import io.trojan.socket_service.config.Config
import org.redisson.api.{PendingEntry, StreamMessageId}
import org.typelevel.log4cats.Logger

//TODO вынести эти ф-и в отдельные сущности
class RedisService[F[_] : Sync](client: RedisClient[F])(implicit config: Config, L: Logger[F]) {
  type TaskId = StreamMessageId
  import config.{redis => redisConfig}

  def initGroup: F[Unit] = client.xGroupCreate(redisConfig.dataBus, redisConfig.group)

  def readStreamEvents[T: RedisCodec : Codec](consumer: String): F[List[RedisEvent[T]]] = {
    client
      .xReadGroup(redisConfig.dataBus, redisConfig.group, consumer, redisConfig.batchSize)
      .flatMap(parseTasks[T])
  }

  def removeOutdatedTasks(outdatedTasks: List[PendingEntry]): F[Unit] = {
    val outdatedTaskIds = outdatedTasks.map(_.getId).toSet

    ackTasks(outdatedTaskIds) >>
    L.error(s"Tasks ${outdatedTaskIds.mkString(",")} will not be processed, max retry attempts was reached")
  }

  def readFailedEvents[T: RedisCodec : Codec](consumer: String): F[List[RedisEvent[T]]] = {
    client
      .xPending(redisConfig.dataBus, redisConfig.group, StreamMessageId.MIN, StreamMessageId.MAX, redisConfig.batchSize)
      .flatMap { entries =>
        val (recoveringTasks, outdatedTasks) = entries.partition(_.getLastTimeDelivered <= redisConfig.recoverCount)

        for {
          _ <- applyOrEmptyF(outdatedTasks.nonEmpty)(removeOutdatedTasks(outdatedTasks))
          events <- claimFailedEvents(consumer, recoveringTasks.map(_.getId).toSet)
        } yield events
      }
      .flatMap(parseTasks[T])
  }

  private def claimFailedEvents(
    consumer: String,
    ids: Set[StreamMessageId]
  ): F[Map[StreamMessageId, Map[String, String]]] = applyOrEmptyF(ids.nonEmpty) {
    client.xClaim(
      redisConfig.dataBus,
      redisConfig.group,
      consumer,
      redisConfig.claimIdleTime.toSeconds,
      ids
    )
  }

  def ackTask(id: StreamMessageId): F[Unit] = client.xAck(redisConfig.dataBus, redisConfig.group, Set(id)).void

  def ackTasks(ids: Set[StreamMessageId]): F[Unit] = client.xAck(redisConfig.dataBus, redisConfig.group, ids).void

  private def parseTasks[R: RedisCodec : Codec](taskMessages: Map[StreamMessageId, Map[String, String]]): F[List[RedisEvent[R]]] = {
    taskMessages
      .toList
      .traverseFilter { case (id, event) =>
        RedisCodec[R].decode(event) match {
          case Success(value) => RedisEvent(id, value).some.pure[F]
          case _ => ackTask(id) >> L.error(s"Parse error, task removed") >> none[RedisEvent[R]].pure[F]
        }
      }
  }
}

