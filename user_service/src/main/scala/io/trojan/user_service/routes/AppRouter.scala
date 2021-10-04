package io.trojan.user_service.routes

import cats.effect.Async
import cats.implicits.toSemigroupKOps
import io.trojan.rpc.UserApiAlg
import io.trojan.user_service.service.UserService
import org.http4s.implicits._
import org.http4s.server.middleware._
import org.http4s.{HttpApp, HttpRoutes}

class AppRouter[F[_] : Async](
  userService: UserService[F]
)
  extends endpoints4s.http4s.server.Endpoints[F]
    with UserApiAlg
    with endpoints4s.http4s.server.JsonEntitiesFromCodecs {

  private def getUsers: HttpRoutes[F] = HttpRoutes.of[F] {
    getUsersApi.implementedByEffect { _ =>
      userService.getUsers()
    }
  }

  private def createUser: HttpRoutes[F] = HttpRoutes.of[F] {
    postUserApi.implementedByEffect { u =>
      userService.createUser(u)
    }
  }

  private def deleteUser: HttpRoutes[F] = HttpRoutes.of[F] {
    deleteUserApi.implementedByEffect { _ =>
      userService.deleteUser()
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
