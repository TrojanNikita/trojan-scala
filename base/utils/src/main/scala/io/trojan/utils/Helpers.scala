package io.trojan.utils

import cats.Applicative
import cats.kernel.Monoid

object Helpers {
  def applyOrEmptyF[F[_] : Applicative, A](cond: Boolean)(f: => F[A])(implicit M: Monoid[A]): F[A] = {
    if (cond) f else Applicative[F].pure(M.empty)
  }

  //  def performance[F[_] : Sync : Clock, A](fa: F[A]): F[(A, Long)] = {
  //    for {
  //      start <- Clock[F].realTime
  //      result <- fa
  //      finish <- Clock[F].monotonic
  //    } yield (result, (finish - start))
  //  }
}
