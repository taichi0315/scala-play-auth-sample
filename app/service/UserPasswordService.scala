package service

import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT
import cats.implicits._

import mvc.auth.AuthMethods
import libs.model.User
import libs.dao.UserPasswordDAO

sealed trait UnauthorizedErr
object UnauthorizedErr extends UnauthorizedErr

class UserPasswordService @Inject()(
  userPasswordDao: UserPasswordDAO,
  authMethods: AuthMethods
) (implicit val ec: ExecutionContext) {
  def verifyPassword(user: User, password: String, result: Result): Future[Either[UnauthorizedErr, Result]] = {
    val e =
      for {
        userPassword <- EitherT(userPasswordDao.get(user.withId).map(_.toRight(UnauthorizedErr)))
        resultEither <- EitherT(
          userPassword.verify(password) match {
            case false => Future.successful(Left(UnauthorizedErr))
            case true  => authMethods.loginSuccess(user, result).map(Right(_))
          }
        )
      } yield resultEither
    e.value
  }
}
