package mvc.auth

import javax.inject.Inject
import play.api.cache.AsyncCacheApi
import play.api.mvc._
import play.api.mvc.Results.Status
import play.api.libs.typedmap.TypedKey
import scala.concurrent.{Future, ExecutionContext}

import libs.model.User

class AuthProfile @Inject()(
  dataStore:    AuthDataStore
) (implicit ec: ExecutionContext)
{

  object AttrKeys {
    val Auth: TypedKey[User] = TypedKey("Authentication")
  }

  val sessionToken: AuthSession = AuthSession()

  val Unauthorized: Result = Status(401).apply("Unauthorized")

  def loginSucceed(user: User, result: Result)(implicit rh: RequestHeader): Future[Result] =
    for {
      token: String <- dataStore.set(user)
    } yield sessionToken.put(token)(result)

  def logoutSucceed(result: Result)(implicit rh: RequestHeader): Future[Result] =
    for {
      _ <- sessionToken.get match {
        case Some(token) => dataStore.remove(token)
        case None        => Future.successful(())
      }
    } yield sessionToken.discard(result)

  def authUserOpt(implicit rh: RequestHeader): Option[User] = rh.attrs.get(AttrKeys.Auth)

  def logined(block: User => Future[Result])(implicit rh: RequestHeader): Future[Result] =
    authUserOpt match {
      case Some(user) => block(user)
      case None       => Future.successful(Unauthorized)
    }

  def authenticate(implicit rh: RequestHeader): Future[Either[Result, (User, Result => Result)]] =
    sessionToken.get match {
      case None        => Future.successful(Left(Unauthorized))
      case Some(token) =>
        for {
          userOpt: Option[User] <- dataStore.get(token)
        } yield
          userOpt match {
            case None       => Left(Unauthorized)
            case Some(user) => Right((user, sessionToken.put(token) _))
          }
    }
}
