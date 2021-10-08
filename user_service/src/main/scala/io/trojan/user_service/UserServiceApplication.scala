package io.trojan.user_service

import scala.concurrent.ExecutionContext

import cats.effect.kernel.Resource
import cats.effect.{Async, ExitCode, IO, IOApp}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import io.trojan.redis.RedisClusterClient
import io.trojan.sql.{Sql, SqlImpl}
import io.trojan.user_service.config.{Config, PostgresConfig, ServerConfig}
import io.trojan.user_service.dao.UserDao
import io.trojan.user_service.routes.AppRouter
import io.trojan.user_service.service.{RedisService, UserService}
import io.trojan.utils.ConfigHelpers.createConfig
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object UserServiceApplication extends IOApp {

  private def createTransactor[F[_]: Async](cfg: PostgresConfig): Resource[F, HikariTransactor[F]] = {
    ExecutionContexts.fixedThreadPool[F](cfg.numThreads)
      .flatMap { connEc =>
        HikariTransactor.newHikariTransactor(
          driverClassName = cfg.jdbcDriver,
          url = cfg.url,
          user = cfg.user,
          pass = cfg.password,
          connectEC = connEc
        )
      }
  }

  private def startServer[F[_] : Async](cfg: ServerConfig, api: HttpApp[F])(implicit ec: ExecutionContext): F[Unit] = {
    BlazeServerBuilder[F](ec)
      .bindHttp(cfg.port, cfg.host)
      .withHttpApp(api)
      .serve
      .compile
      .drain
  }

  override def run(args: List[String]): IO[ExitCode] = {

    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
    implicit def unsafeLogger[F[_]]: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    implicit val conf: Config = createConfig[Config]()

    createTransactor[IO](conf.postgres).use { transactor =>
      val sql: Sql[IO] = new SqlImpl[IO](transactor)
      val userDao: UserDao[IO] = new UserDao.MySql(sql)
      val redisClusterClient = new RedisClusterClient[IO](conf.redis.hosts)
      val redisService: RedisService[IO] = new RedisService[IO](redisClusterClient)
      val userService: UserService[IO] = new UserService[IO](userDao, redisService)
      val router: AppRouter[IO] = new AppRouter[IO](userService)

      userService.init() >> startServer[IO](conf.server, router.allRoutes).as(ExitCode.Success)
    }
  }
}
