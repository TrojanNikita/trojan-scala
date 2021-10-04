package io.trojan.common.models

import io.circe.Codec

case class Socket[T: Codec](action: String, data: T)

object Socket
