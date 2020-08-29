package service

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT
import cats.implicits._

import libs.model.User
import libs.dao.UserPasswordDAO

sealed trait UnauthorizedErr
object UnauthorizedErr extends UnauthorizedErr

class UserPasswordService @Inject()(
  userPasswordDao: UserPasswordDAO
) (implicit val ec: ExecutionContext) {
  def verifyPassword(user: User, password: String): Future[Either[UnauthorizedErr, User]] = {
    val e =
      for {
        userPassword <- EitherT(userPasswordDao.get(user.withId).map(_.toRight(UnauthorizedErr)))
        resultEither <- EitherT(
          userPassword.verify(password) match {
            case false => Future.successful(Left(UnauthorizedErr))
            case true  => Future.successful(Right(user))
          }
        )
      } yield resultEither
    e.value
  }
}
