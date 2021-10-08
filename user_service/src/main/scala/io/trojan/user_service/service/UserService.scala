package io.trojan.user_service.service


import cats.Monad
import cats.implicits._
import io.trojan.models.User
import io.trojan.sql.WithDB
import io.trojan.user_service.config.Config
import io.trojan.user_service.dao.UserDao

class UserService[F[_] : Monad](
  userDao: UserDao[F],
  redisService: RedisService[F]
)(implicit config: Config) extends WithDB[F] {

  override def init(): F[Unit] = userDao.init()

  def getUsers(): F[List[User]] = {
    userDao.selectUsers()
  }

  def createUser(u: User): F[Unit] = {
    userDao.insertUser(u) >> redisService.addToStream[User](u, config.redis.dataBus).void
  }

  def deleteUser(): F[Unit] = {
    userDao.deleteUsers()
  }
}
