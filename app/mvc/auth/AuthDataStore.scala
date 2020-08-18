package mvc.auth

import java.util.UUID
import javax.inject.Inject
import play.api.mvc._
import play.api.cache._
import scala.concurrent.{Future, ExecutionContext}

import libs.model.User

case class AuthDataStore @Inject()(
  cache: AsyncCacheApi
) (implicit ec: ExecutionContext)
{
  def loginSuccess(user: User, result: Result): Future[Result] = {
    val token: String = UUID.randomUUID.toString
    for {
      _ <- cache.set(token, user)
    } yield
      result.withCookies(
        Cookie("user", token)
      )
  }

  def get(token: String): Future[Option[User]] = cache.get(token)

  def remove(token: String): Future[Unit] =
    for {
      _ <- cache.remove(token)
    } yield ()
}
