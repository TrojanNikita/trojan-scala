package io.trojan.sql

import cats.effect.kernel.Concurrent
import doobie.{ConnectionIO, Transactor}

trait Sql[F[_]] {
  def execute[A](conn: ConnectionIO[A]): F[A]
}

final class SqlImpl[F[_]: Concurrent](
  transactor: Transactor[F]
) extends Sql[F] {

  override def execute[A](conn: ConnectionIO[A]): F[A] = transactor.trans.apply(conn)
}
