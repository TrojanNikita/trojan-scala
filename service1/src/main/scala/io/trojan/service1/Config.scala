package io.trojan.service1

import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

case class ServerConfig(port: Int, host: String)

case class Config(server: ServerConfig)

object ServerConfig {
  implicit val codec: ConfigReader[ServerConfig] = deriveReader[ServerConfig]
}

object Config {
  implicit val codec: ConfigReader[Config] = deriveReader[Config]
}
