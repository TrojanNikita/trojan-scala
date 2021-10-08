package io.trojan.rpc

import endpoints4s.algebra.circe.JsonEntitiesFromCodecs
import io.trojan.models.User

trait UserApiAlg extends JsonEntitiesFromCodecs {
  type RequestId = String

  private val urlGetUsers          = path / "api" / "users"
  private val urlPostUser          = path / "api" / "users"

  protected val getUsersApi: Endpoint[Unit, List[User]] = endpoint(
    get(urlGetUsers),
    ok(jsonResponse[List[User]])
  )

  protected val postUserApi: Endpoint[User, Unit] = endpoint(
    post(urlPostUser, entity = jsonRequest[User]),
    ok(emptyResponse)
  )

  protected val deleteUserApi: Endpoint[Unit, Unit] = endpoint(
    delete(urlPostUser),
    ok(emptyResponse)
  )
}

