package io.trojan.service1

import AppModule.ec
import cats.effect.{ConcurrentEffect, ExitCode, IO, Resource, Timer}
import cats.{Applicative, Defer, Monad}
import io.circe.Json
import izumi.distage.model.definition.Lifecycle
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

//class SimpleEndpoint[F[_] : Defer : Applicative : Monad : Timer : ConcurrentEffect]() extends Lifecycle.OfCats[F, Server[F]] {
//  SimpleEndpoint.impl[F]
//}

class SimpleEndpoint[F[_] : Defer : Applicative : Monad : Timer : ConcurrentEffect]() {

  def UserRoutes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "all" => Ok("asd")
    }
  }

  def allRoutes: HttpApp[F] = {
    UserRoutes.orNotFound
  }

  def run: F[Unit] = {
    BlazeServerBuilder[F](ec)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(allRoutes)
      .serve
      .compile
      .drain
  }

}
