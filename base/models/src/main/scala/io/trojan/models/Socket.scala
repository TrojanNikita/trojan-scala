package io.trojan.models

import io.circe.Codec

case class Socket[T: Codec](action: String, data: T)

object Socket
