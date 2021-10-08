package io.trojan.redis

trait WithRedisKey[A] {
  def key(value: A): String
}

object WithRedisKey {
  implicit class Impl[A](value: A) {
    def getKey(implicit w: WithRedisKey[A]): String = w.key(value)
  }
}
