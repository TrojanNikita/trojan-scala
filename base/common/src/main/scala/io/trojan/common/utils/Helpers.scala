package io.trojan.common.utils

import cats.Applicative
import cats.kernel.Monoid

object Helpers {
  def applyOrEmptyF[F[_] : Applicative, A](cond: Boolean)(f: => F[A])(implicit M: Monoid[A]): F[A] = {
    if (cond) f else Applicative[F].pure(M.empty)
  }
}
