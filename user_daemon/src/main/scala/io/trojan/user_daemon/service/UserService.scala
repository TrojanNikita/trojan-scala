package io.trojan.user_daemon.service

import scala.concurrent.ExecutionContext

import cats.implicits._
import cats.effect.Async
import cats.effect.kernel.Concurrent
import io.trojan.common.models.User
import io.trojan.rpc.UserApiAlg
import io.trojan.user_daemon.config.Config
import org.http4s.client.Client
import org.typelevel.log4cats.Logger

trait UserService[F[_]] {
  def createUser(u: User): F[Long]
  def deleteUsers: F[Unit]
}

object UserService {
  class Rpc[F[_] : Async : Concurrent](
    client: Client[F]
  )(implicit config: Config, env: ExecutionContext, L: Logger[F])
    extends endpoints4s.http4s.client.Endpoints[F](config.service.users.uri, client)
      with UserApiAlg
      with endpoints4s.http4s.client.JsonEntitiesFromCodecs
      with UserService[F] {

    override def createUser(u: User): F[Long] = {
      postUserApi.run(u)
    }

    override def deleteUsers: F[Unit] = {
      deleteUserApi.run()
    }
  }
}