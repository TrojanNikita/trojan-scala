package io.trojan.user_service.service


import cats.{Applicative, Monad}
import cats.implicits._
import io.trojan.common.models.User
import io.trojan.user_service.dao.UserDao

class UserService[F[_] : Monad](
  userDao: UserDao[F],
  redisService: RedisService[F]
) {

  def init(): F[Unit] = userDao.init()

  def getUsers(): F[List[User]] = {
    userDao.selectUsers()
  }

  def createUser(u: User): F[Long] = {
    userDao
      .insertUser(u)
      .flatTap(_ => redisService.add[User](u))
  }

  def deleteUser(): F[Unit] = {
    userDao.deleteUsers()
  }
}
