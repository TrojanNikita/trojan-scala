package io.trojan.user_daemon.config

import org.http4s.Uri
import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class Config(service: ServicesConfig)

case class ServicesConfig(users: ServiceConfig)

case class ServiceConfig(host: String, port: String) {
  def uri: Uri = Uri.unsafeFromString(s"http://$host:$port")
}

object Config {
  implicit val codec: ConfigReader[Config] = deriveReader[Config]
}

object ServiceConfig {
  implicit val codec: ConfigReader[ServiceConfig] = deriveReader[ServiceConfig]
}

object ServicesConfig {
  implicit val codec: ConfigReader[ServicesConfig] = deriveReader[ServicesConfig]
}
