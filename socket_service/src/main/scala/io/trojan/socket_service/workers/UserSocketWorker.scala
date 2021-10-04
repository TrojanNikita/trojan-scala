package io.trojan.socket_service.workers

import cats.implicits._
import cats.effect.Temporal
import fs2.{Pipe, Stream}
import io.trojan.common.models.{RedisEvent, User}
import io.trojan.socket_service.config.Config
import io.trojan.socket_service.models.CustomError
import io.trojan.socket_service.service.Helpers.SocketEncoder
import io.trojan.socket_service.service.RedisService
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text
import org.typelevel.log4cats.Logger

class UserSocketWorker[F[_] : Temporal](
  redisService: RedisService[F]
)(implicit L: Logger[F], config: Config) {

  import config.{user => userConfig}
  private val name = getClass.getSimpleName

  val init: F[Unit] = redisService.initGroup.handleError(error => L.warn(s"${error.getLocalizedMessage}"))

  private val readTask =
    Stream
      .awakeDelay[F](userConfig.readInterval)
      .evalMap(_ => redisService.readStreamEvents[User](name))
      .filter(_.nonEmpty)
      .evalTap(entries => L.info(s"Got ${entries.size} new events"))

  private val recover =
    Stream
      .awakeDelay[F](userConfig.readRecoverInterval)
      .evalMap(_ => redisService.readFailedEvents[User](name))
      .filter(_.nonEmpty)
      .evalTap(entries => L.info(s"Got ${entries.size} recovered events"))

  val toClient: Stream[F, WebSocketFrame] =
    readTask
      .merge(recover)
      .flatMap {
        Stream
          .emits(_)
          .covary[F]
          .parEvalMapUnordered(userConfig.concurrent)(_.body.toSocketText().pure[F])
      }

  //  TODO прикрутить эту штуку
  private def handleError[T](error: Throwable, event: RedisEvent[T]): F[Unit] = {
    error match {
      case error: CustomError if error.isRecoverable =>
        L.error(s"End Tasks ${event.idString} with custom ${error.getLocalizedMessage}")
      case error: CustomError =>
        L.warn(s"End Task ${event.idString} with custom ${error.getLocalizedMessage}, but task ack")
          .>>(redisService.ackTask(event.id))
      case error => L.error(s"End Tasks ${event.idString} with error ${error.getLocalizedMessage}")
    }
  }

  val fromClient: Pipe[F, WebSocketFrame, Unit] = _.evalMap {
    case Text(t, _) => L.info(t)
    case f => L.info(s"Unknown type: $f")
  }
}