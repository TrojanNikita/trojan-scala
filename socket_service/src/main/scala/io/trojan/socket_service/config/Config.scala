package io.trojan.socket_service.config

import io.trojan.utils.WithConfigCodec

case class ServerConfig(port: Int, host: String)

case class Config(server: ServerConfig, redis: RedisConfig, user: UserSocketConfig)

object ServerConfig extends WithConfigCodec[ServerConfig]

object Config extends WithConfigCodec[Config]
