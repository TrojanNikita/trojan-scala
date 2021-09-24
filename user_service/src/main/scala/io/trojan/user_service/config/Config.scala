package io.trojan.user_service.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class ServerConfig(port: Int, host: String)
case class PostgresConfig(
  numThreads: Int,
  jdbcDriver: String,
  url: String,
  user: String,
  password: String
)

case class Config(server: ServerConfig, postgres: PostgresConfig)

object ServerConfig {
  implicit val codec: ConfigReader[ServerConfig] = deriveReader[ServerConfig]
}

object PostgresConfig {
  implicit val codec: ConfigReader[PostgresConfig] = deriveReader[PostgresConfig]
}

object Config {
  implicit val codec: ConfigReader[Config] = deriveReader[Config]
}
