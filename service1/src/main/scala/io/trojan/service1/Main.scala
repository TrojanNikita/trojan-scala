package io.trojan.service1

import cats.effect.{ExitCode, IO, IOApp}
import distage.Injector
import izumi.distage.model.plan.Roots

object Main extends IOApp {
  override def run(args: List[String]):IO[ExitCode] = {

    val plan = Injector().plan(AppModule[IO],Roots.target[SimpleEndpoint[IO]])

    Injector[IO]()
      .produce(plan)
      .use(_.get[SimpleEndpoint[IO]].run)
      .as(ExitCode.Success)
  }
}

//object Main extends App {
//  println("ada")
//}
