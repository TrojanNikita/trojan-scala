package io.trojan.service1

import cats.effect.Async
import io.trojan.user.UserApiAlg
import org.http4s.implicits._
import org.http4s.{HttpApp, HttpRoutes}

class AppRouter[F[_] : Async](
  userDao: UserDao[F]
)
  extends endpoints4s.http4s.server.Endpoints[F]
    with UserApiAlg
    with endpoints4s.http4s.server.JsonEntitiesFromCodecs {

  private def getUsers: HttpRoutes[F] = HttpRoutes.of[F] {
    getUsersApi.implementedByEffect { _ =>
      userDao.selectUsers()
    }
  }

  def allRoutes: HttpApp[F] = {
    getUsers.orNotFound
  }
}
