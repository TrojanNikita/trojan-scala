package io.trojan.socket_service

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import cats.effect._
import cats.effect.std.Queue
import cats.syntax.all._
import fs2._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket._
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame._
import org.typelevel.log4cats.Logger

class SocketService[F[_] : Async](
  implicit ec: ExecutionContext,
  L: Logger[F]
) extends Http4sDsl[F] {

  def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "ws" =>

        val toClient: Stream[F, WebSocketFrame] =
          Stream
            .awakeEvery[F](1.seconds)
            .map(d => Text(s"Ping! ${d.toSeconds}"))

        val fromClient: Pipe[F, WebSocketFrame, Unit] = _.evalMap {
          case Text(t, _) => L.info(t)
          case f => L.info(s"Unknown type: $f")
        }

        WebSocketBuilder[F].build(toClient, fromClient)

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
