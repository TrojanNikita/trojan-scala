package io.trojan.user

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

final case class User(
  id: Long,
  name: String
)

object User {
  private[user] implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames
  private[user] implicit val cinemaCodec: Codec[User] = deriveConfiguredCodec[User]
}