package io.trojan.socket_service.service

import cats.effect._
import cats.effect.std.Queue
import cats.syntax.all._
import io.trojan.socket_service.workers.UserSocketWorker
import fs2._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket._
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame._


class SocketService[F[_] : Async](
  userSocketWorker: UserSocketWorker[F]
) extends Http4sDsl[F] {

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "ws" =>
        userSocketWorker.init >> WebSocketBuilder[F].build(userSocketWorker.toClient, userSocketWorker.fromClient)

      case GET -> Root / "wsecho" =>
        val echoReply: Pipe[F, WebSocketFrame, WebSocketFrame] =
          _.collect {
            case Text(msg, _) => Text("You sent the server: " + msg)
            case _ => Text("Something new")
          }

        Queue
          .unbounded[F, Option[WebSocketFrame]]
          .flatMap { q =>
            val d: Stream[F, WebSocketFrame] = Stream.fromQueueNoneTerminated(q).through(echoReply)
            val e: Pipe[F, WebSocketFrame, Unit] = _.enqueueNoneTerminated(q)

            WebSocketBuilder[F].build(d, e)
          }
    }
}
