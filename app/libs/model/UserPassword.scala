package libs.model

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.password.BCryptPasswordHasher

import libs.model.User

case class UserPassword(
  userId: User.Id,
  hash:   PasswordInfo
) {
  import UserPassword.passwordHasher

  def verify(password: String): Boolean =
    passwordHasher.matches(hash, password)
}

object UserPassword {

  lazy val passwordHasher = new BCryptPasswordHasher()

  def build(userId: User.Id, password: String) =
    new UserPassword(
      userId = userId,
      hash   = passwordHasher.hash(password)
    )
}
