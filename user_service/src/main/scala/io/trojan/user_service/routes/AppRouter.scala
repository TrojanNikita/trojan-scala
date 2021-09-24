package io.trojan.user_service.routes

import cats.effect.Async
import cats.implicits.toSemigroupKOps
import io.trojan.user_service.dao.UserDao
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

  private def createUser: HttpRoutes[F] = HttpRoutes.of[F] {
    postUserApi.implementedByEffect { u =>
      userDao.insertUser(u)
    }
  }

  def allRoutes: HttpApp[F] = {
    (getUsers <+> createUser).orNotFound
  }
}
