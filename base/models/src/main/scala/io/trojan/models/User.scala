package io.trojan.models

import scala.util.{Failure, Success, Try}

import io.circe.Codec
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.trojan.redis.{RedisCodec, WithRedisKey}

final case class User(
  id: Long,
  name: String
)

trait CirceUser {
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val userCodec: Codec[User] = deriveConfiguredCodec[User]
}

trait WithRedisKeyUser {
  implicit val withKey: WithRedisKey[User] = new WithRedisKey[User] {
    override def key(value: User): String = value.id.toString
  }
}

trait RedisUser {
  implicit val codec: RedisCodec[User] = {
    new RedisCodec[User] {
      override def decode: Map[String, String] => Try[User] = { keys =>
        (for {
          name <- keys.get("name")
          idString <- keys.get("id")
          id <- idString.toLongOption
        } yield User(id, name))
          .map(Success(_))
          .getOrElse(Failure(new NoSuchElementException(s"Can`t parse $keys to User, event ack")))
      }

      override def encode: User => Map[String, String] = (u: User) => {
        Map(
          "id" -> u.id.toString,
          "name" -> u.name
        )
      }
    }
  }
}

object User extends CirceUser with RedisUser with WithRedisKeyUser
