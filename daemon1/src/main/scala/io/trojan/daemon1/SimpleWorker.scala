package io.trojan.daemon1

import scala.concurrent.duration.DurationInt

import cats.{Applicative, Functor}
import cats.effect.{Sync, Timer}
import cats.implicits.catsSyntaxApplicativeId
import fs2.Stream
import org.joda.time.DateTime

class SimpleWorker[F[_] : Sync : Timer : Functor : Applicative] {
  def run: F[Unit] = Stream
    .retry(stream, 1.seconds, identity, maxAttempts = 100)
    .compile
    .drain

  val stream: F[Unit] = Stream
    .awakeEvery[F](2.seconds)
    .evalMap(_ => println(DateTime.now.toString("HH.mm.ss")).pure[F])
    .compile
    .drain
}
