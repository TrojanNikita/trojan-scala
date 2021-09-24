package io.trojan.user_daemon

import scala.concurrent.ExecutionContext

import cats.effect.{ExitCode, IO, IOApp}
import io.trojan.user_daemon.worker.SimpleWorker
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object UserDaemonApplication extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
    implicit def unsafeLogger[F[_]]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    val worker = new SimpleWorker[IO]

    worker
      .run
      .as(ExitCode.Success)
  }
}
