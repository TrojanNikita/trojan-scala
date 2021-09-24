package io.trojan.user_service.routes

import cats.effect.Async
import cats.implicits.toSemigroupKOps
import io.trojan.rpc.UserApiAlg
import io.trojan.user_service.dao.UserDao
import org.http4s.implicits._
import org.http4s.server.middleware._
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

  private def deleteUser: HttpRoutes[F] = HttpRoutes.of[F] {
    deleteUserApi.implementedByEffect { _ =>
      userDao.deleteUsers()
    }
  }

  def allRoutes: HttpApp[F] = {
    val routes = (getUsers <+> createUser <+> deleteUser).orNotFound
    CORS.policy
      .withAllowOriginAll
      .withAllowCredentials(false)
      .apply(routes)
  }
}
