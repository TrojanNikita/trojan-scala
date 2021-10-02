package io.trojan.socket_service.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class ServerConfig(port: Int, host: String)

case class Config(server: ServerConfig)

object ServerConfig {
  implicit val codec: ConfigReader[ServerConfig] = deriveReader[ServerConfig]
}

object Config {
  implicit val codec: ConfigReader[Config] = deriveReader[Config]
}
