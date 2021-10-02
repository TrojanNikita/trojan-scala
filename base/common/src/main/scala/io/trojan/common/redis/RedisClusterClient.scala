package io.trojan.common.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config

import scala.jdk.CollectionConverters.SeqHasAsJava

import cats.Applicative

trait RedisClient[F[_]] extends RedisOperations.Client[F]  {
  protected val client: RedissonClient
}

class RedisClusterClient[F[_]: Applicative](hosts: Seq[String]) extends RedisClient[F] {

  private val config: Config = {
    val config = new Config()

    config
      .useClusterServers()
      .setCheckSlotsCoverage(false)
      .setNodeAddresses(hosts.map(host => s"redis://$host").asJava)

    config
  }

  override protected val client: RedissonClient = Redisson.create(config)
}