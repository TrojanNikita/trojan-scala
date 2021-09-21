package io.trojan.service1

import scala.concurrent.ExecutionContext

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.ConfigSource

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

    val conf = ConfigSource.default.loadOrThrow[Config].server
    val router = new AppRouter[IO]

    BlazeServerBuilder[IO](ec)
      .bindHttp(conf.port, conf.host)
      .withHttpApp(router.allRoutes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
