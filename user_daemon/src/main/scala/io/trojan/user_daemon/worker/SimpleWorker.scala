package io.trojan.user_daemon.worker

import scala.concurrent.duration.DurationInt

import cats.effect.kernel.Temporal
import fs2.Stream
import org.joda.time.DateTime
import org.typelevel.log4cats.Logger

class SimpleWorker[F[_] : Temporal](implicit L: Logger[F]) {
  def run: F[Unit] = Stream
    .retry(stream, 1.seconds, identity, maxAttempts = 100)
    .compile
    .drain

  val stream: F[Unit] = Stream
    .awakeEvery[F](2.seconds)
    .evalMap(_ => L.info(DateTime.now.toString("HH.mm.ss")))
    .compile
    .drain
}
