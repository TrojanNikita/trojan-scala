package io.trojan.sql

trait WithDB[F[_]] {
  def init(): F[Unit]
}
