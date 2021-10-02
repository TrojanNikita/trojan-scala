package io.trojan.socket_service

import scala.concurrent.ExecutionContext

import cats.effect.{ExitCode, IO, IOApp}
import io.trojan.socket_service.config.Config
import org.http4s.blaze.server.BlazeServerBuilder
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource

object SocketServiceApplication extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val conf: Config = ConfigSource.default.loadOrThrow[Config]

    implicit def unsafeLogger[F[_]]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

    val socketRouter = new SocketService[IO]

    BlazeServerBuilder[IO](ec)
      .bindHttp(conf.server.port, conf.server.host)
      .withWebSockets(true)
      .withHttpApp(socketRouter.routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
