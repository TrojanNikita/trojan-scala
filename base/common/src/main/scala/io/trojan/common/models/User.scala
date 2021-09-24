package io.trojan.common.models

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

final case class User(
  id: Long,
  name: String
)

object User {
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val cinemaCodec: Codec[User] = deriveConfiguredCodec[User]
}