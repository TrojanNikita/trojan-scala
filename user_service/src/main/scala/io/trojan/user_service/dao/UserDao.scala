package io.trojan.user_service.dao

import cats.implicits.toFunctorOps
import doobie.implicits.toSqlInterpolator
import io.trojan.models.User
import io.trojan.sql.{Sql, WithDB}

trait UserDao[F[_]] extends WithDB[F] {
  def selectUsers(): F[List[User]]
  def insertUser(u: User): F[Long]
  def deleteUsers(): F[Unit]
}

object UserDao {
  final class MySql[F[_]](sql: Sql[F]) extends UserDao[F] {

    // TODO сделать с помощью эволюций
    override def init(): F[Unit] = {
      val schema = scala.io.Source.fromResource("schema.sql").mkString
      sql.execute {
        doobie.Update(schema).run().void
      }
    }

    override def selectUsers(): F[List[User]] = {
      sql.execute {
        sql"""select * from users""".query[User].to[List]
      }
    }

    override def insertUser(user: User): F[Long] = {
      sql.execute {
        sql"""insert into users (id, name) values(${user.id}, ${user.name})"""
          .update
          .withUniqueGeneratedKeys[Long]("id")
      }
    }

    override def deleteUsers(): F[Unit] = {
      sql.execute {
        sql"""delete from users where id in (select id from users order by id limit 10)""".update.run.void
      }
    }
  }
}