package io.trojan.service1

import cats.effect.{ConcurrentEffect, Sync, Timer}
import cats.implicits.catsSyntaxApplicativeId
import cats.{Applicative, Defer, Monad}
import io.trojan.user.{User, UserApiAlg}
import org.http4s.implicits._
import org.http4s.{HttpApp, HttpRoutes}

class AppRouter[F[_] : Sync : Defer : Applicative : Monad : Timer : ConcurrentEffect]()
  extends endpoints4s.http4s.server.Endpoints[F]
    with UserApiAlg
    with endpoints4s.http4s.server.JsonEntitiesFromCodecs {

  private def getUsers: HttpRoutes[F] = HttpRoutes.of[F] {
    getUsersApi.implementedByEffect { _ =>
      List(User(1, "1"), User(2, "2")).pure[F]
    }
  }

  def allRoutes: HttpApp[F] = {
    getUsers.orNotFound
  }
}
