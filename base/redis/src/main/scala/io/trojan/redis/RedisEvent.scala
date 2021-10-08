package io.trojan.redis

import org.redisson.api.StreamMessageId

case class RedisEvent[T: RedisCodec](id: StreamMessageId, body: T) {
  val idString: String = id.toString
}
