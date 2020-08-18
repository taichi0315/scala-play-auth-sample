package mvc.auth

import javax.inject.Inject
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

import mvc.auth.AuthMethods
import libs.model.User

case class UserRequest[A](
  user:    User,
  request: Request[A]
) extends WrappedRequest[A](request)

class AuthenticationAction @Inject()(val authMethods: AuthMethods)(implicit val executionContext: ExecutionContext)
extends ActionRefiner[Request, UserRequest] {

  protected def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] = {
    val tokenOpt = request.cookies.get("user")
    val next     = tokenOpt match {
      case None        => Future.successful(Left(Unauthorized("not found cookie")))
      case Some(token) =>
        for {
          userOpt <- authMethods.get(token.value) 
        } yield {
          userOpt match {
            case None            => Left(Unauthorized("not found user"))
            case Some(user) => Right(UserRequest(user, request))
          }
        }
    }
    next
  }
}
