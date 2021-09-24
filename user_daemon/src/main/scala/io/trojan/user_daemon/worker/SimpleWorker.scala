package io.trojan.user_daemon.worker

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

import cats.effect.Temporal
import cats.implicits._
import fs2.Stream
import io.trojan.common.models.User
import io.trojan.user_daemon.config.Config
import io.trojan.user_daemon.service.UserService
import org.joda.time.DateTime
import org.typelevel.log4cats.Logger

class SimpleWorker[F[_] : Temporal](userService: UserService[F])(
  implicit config: Config, env: ExecutionContext, L: Logger[F]
) {

  def run: F[Unit] = Stream
    .retry(stream, 1.seconds, identity, maxAttempts = 100)
    .compile
    .drain

  def stream: F[Unit] = Stream(
    createUserStream,
    deleteUsersStream
  ).parJoin(2).compile.drain

  def createUserStream: Stream[F, Unit] = Stream
    .awakeDelay[F](4.seconds)
    .evalMap(_ => L.info("createUserProcess") >> createUserProcess)

  def deleteUsersStream: Stream[F, Unit] = Stream
    .awakeDelay[F](40.seconds)
    .evalMap(_ => L.info("deleteUsers") >> userService.deleteUsers)

  def createUserProcess: F[Unit] = {
    val randomId = DateTime.now.getMillis / 1000
    val randomUser = User(
      id = randomId,
      name = "A_" + randomId
    )
    userService.createUser(randomUser).void
  }
}
