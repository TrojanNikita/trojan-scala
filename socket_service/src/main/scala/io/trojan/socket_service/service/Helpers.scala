package io.trojan.socket_service.service

import io.circe.Encoder
import io.circe.syntax.EncoderOps
import org.http4s.websocket.WebSocketFrame.Text

object Helpers {
  implicit class SocketEncoder[T: Encoder](val value: T) {
    def toSocketText(): Text = Text(value.asJson.noSpaces)
  }
}