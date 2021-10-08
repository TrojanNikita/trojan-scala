package io.trojan.redis

trait RedisOperations[F[_]]
  extends RedisStreamOperations[F]
    with RedisHMOperations[F]
