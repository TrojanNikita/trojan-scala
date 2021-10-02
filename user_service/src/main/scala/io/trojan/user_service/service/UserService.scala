package io.trojan.user_service.service


import cats.{Applicative, Monad}
import cats.implicits._
import io.trojan.common.models.User
import io.trojan.user_service.dao.UserDao

class UserService[F[_]: Applicative : Monad](
  userDao: UserDao[F],
  redisService: RedisService[F]
) {

  def getUsers(): F[List[User]] = {
    redisService
      .getUsers()
      .flatMap {
        case Some(users) => users.pure[F]
        case _ => userDao.selectUsers().flatTap(redisService.setUsers)
      }
  }

  def createUser(u: User): F[Long] = {
    userDao.insertUser(u)
  }

  def deleteUser(): F[Unit] = {
    userDao.deleteUsers()
  }
}
