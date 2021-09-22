package io.trojan.service1

import cats.effect.Sync
import doobie.implicits.toSqlInterpolator
import io.getquill.{idiom => _}
import io.trojan.user.User

trait UserDao[F[_]] {
  def selectUsers(): F[List[User]]
}

object UserDao {
  final class MySql[F[_] : Sync](sql: Sql[F]) extends UserDao[F] {
    override def selectUsers(): F[List[User]] = {
      sql.execute {
        sql"""SELECT * FROM users""".query[User].to[List]
      }
    }
  }
}