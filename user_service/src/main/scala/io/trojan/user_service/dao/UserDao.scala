package io.trojan.user_service.dao

import cats.effect.Sync
import doobie.implicits.toSqlInterpolator
import io.getquill.{idiom => _}
import io.trojan.user_service.utils.Sql
import io.trojan.user.User

trait UserDao[F[_]] {
  def selectUsers(): F[List[User]]
  def insertUser(u: User): F[Long]
}

object UserDao {
  final class MySql[F[_] : Sync](sql: Sql[F]) extends UserDao[F] {
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
  }
}