package io.trojan.user_service.config

import io.trojan.utils.WithConfigCodec

case class Config(server: ServerConfig, postgres: PostgresConfig, redis: RedisConfig)
case class ServerConfig(port: Int, host: String)
case class PostgresConfig(
  numThreads: Int,
  jdbcDriver: String,
  url: String,
  user: String,
  password: String
)


object ServerConfig extends WithConfigCodec[ServerConfig]
object PostgresConfig extends WithConfigCodec[PostgresConfig]
object Config extends WithConfigCodec[Config]
