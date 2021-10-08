package io.trojan.user_daemon.config

import io.trojan.utils.WithConfigCodec
import org.http4s.Uri

case class Config(service: ServicesConfig)

case class ServicesConfig(users: ServiceConfig)

case class ServiceConfig(host: String, port: String) {
  def uri: Uri = Uri.unsafeFromString(s"http://$host:$port")
}

object Config extends WithConfigCodec[Config]
object ServiceConfig extends WithConfigCodec[ServiceConfig]
object ServicesConfig extends WithConfigCodec[ServicesConfig]
