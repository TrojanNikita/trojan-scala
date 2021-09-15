package io.trojan.service1

import scala.concurrent.ExecutionContext

import cats.{Applicative, Defer, Functor, Monad, Parallel}
import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, IO, LiftIO, Sync, Timer}
import distage.{ModuleDef, TagK}

object AppModule{
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  def apply[F[_]: TagK : Parallel : Concurrent : Timer : ConcurrentEffect](implicit cs : ContextShift[IO]): ModuleDef = {
    new ModuleDef {

      make[ExecutionContext].fromValue(ec)


      make[SimpleEndpoint[F]]

      addImplicit[Sync[F]]
      addImplicit[Timer[F]]

      addImplicit[Sync[F]]
      addImplicit[ContextShift[IO]]
      addImplicit[ConcurrentEffect[F]]
      addImplicit[Timer[F]]
      addImplicit[LiftIO[F]]
      addImplicit[Parallel[F]]
      addImplicit[Functor[F]]
      addImplicit[Applicative[F]]
      addImplicit[Monad[F]]
      addImplicit[Defer[F]]
    }
  }
}
