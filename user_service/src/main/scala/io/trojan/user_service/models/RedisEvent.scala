package io.trojan.user_service.models

import io.circe.Codec
import org.redisson.api.StreamMessageId

case class RedisEvent[T : Codec](id: StreamMessageId, body: T) {
  val idString: String = id.toString
}
