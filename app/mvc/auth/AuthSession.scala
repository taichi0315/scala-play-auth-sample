package mvc.auth

import play.api.mvc._
import scala.concurrent.duration.Duration

case class AuthSession() {

  val key:    String      = "user"
  val maxAge: Option[Int] = Some(Duration(30, "minutes").toSeconds.toInt)
  
  def put(value: String)(result: Result)(implicit rh: RequestHeader): Result =
    result.withCookies(
      Cookie(key, value, maxAge)
    )

  def get(implicit rh: RequestHeader): Option[String] =
    rh.cookies.get(key).map(_.value)

  def discard(result: Result)(implicit rh: RequestHeader): Result =
    result.discardingCookies(
      DiscardingCookie(key)
    )
}
