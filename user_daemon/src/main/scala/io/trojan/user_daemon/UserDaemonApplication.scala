package io.trojan.user_daemon

import scala.concurrent.ExecutionContext

import cats.effect.{ExitCode, IO, IOApp}
import io.trojan.user_daemon.config.Config
import io.trojan.user_daemon.service.UserService
import io.trojan.user_daemon.worker.SimpleWorker
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource

object UserDaemonApplication extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

    implicit val conf: Config = ConfigSource.default.loadOrThrow[Config]
    implicit def unsafeLogger[F[_]]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    BlazeClientBuilder[IO](ec)
      .resource
      .use { client =>
        val userService: UserService[IO] = new UserService.Rpc[IO](client)
        val worker: SimpleWorker[IO] = new SimpleWorker[IO](userService)

        Logger[IO].info("User Daemon start ..") >> worker.run
      }
      .as(ExitCode.Success)
  }
}
