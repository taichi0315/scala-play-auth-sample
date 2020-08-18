package mvc.auth

import java.util.UUID
import javax.inject.Inject
import play.api.mvc._
import play.api.cache._
import scala.concurrent.{Future, ExecutionContext}

import libs.model.User

case class AuthMethods @Inject()(
  cache: AsyncCacheApi
) (implicit ec: ExecutionContext)
{
  def loginSuccess(user: User, result: Result): Future[Result] = {
    val token: String = UUID.randomUUID.toString
    for {
      _ <- set(token, user)
    } yield
      result.withCookies(
        Cookie("user", token)
      )
  }

  def logoutSuccess(result: Result)(implicit rh: RequestHeader): Future[Result] =
    for {
      _ <- rh.cookies.get("user") match {
        case Some(token) => remove(token.value)
        case None        => Future.successful(())
      }
    } yield
      result.discardingCookies(
        DiscardingCookie("user")
      )

  def set(token: String, user: User): Future[akka.Done] = cache.set(token, user)

  def get(token: String): Future[Option[User]] = cache.get(token)

  def remove(token: String): Future[Unit] =
    for {
      _ <- cache.remove(token)
    } yield ()
}
