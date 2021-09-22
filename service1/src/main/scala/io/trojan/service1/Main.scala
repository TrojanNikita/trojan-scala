package io.trojan.service1

import scala.concurrent.ExecutionContext

import cats.effect.{Async, ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import org.http4s.blaze.server.BlazeServerBuilder
import pureconfig.ConfigSource

object Main extends IOApp {
  private def createTransactor[F[_] : Async](conf: PostgresConfig): Transactor[F] =
    Transactor.fromDriverManager[F](
      conf.jdbcDriver,
      conf.url,
      conf.user,
      conf.password
    )

  override def run(args: List[String]): IO[ExitCode] = {

    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

    val conf: Config = ConfigSource.default.loadOrThrow[Config]

    val transactor = createTransactor[IO](conf.postgres)
    val sql: Sql[IO] = new SqlImpl[IO](transactor)
    val userDao: UserDao[IO] = new UserDao.MySql(sql)
    val router: AppRouter[IO] = new AppRouter[IO](userDao)

    BlazeServerBuilder[IO](ec)
      .bindHttp(conf.server.port, conf.server.host)
      .withHttpApp(router.allRoutes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
