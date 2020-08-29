package libs.model

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.password.BCryptPasswordHasher

import libs.model.User

case class UserPassword(
  userId:         User.Id,
  hashedPassword: PasswordInfo
) {
  import UserPassword.passwordHasher

  def verify(rawPassword: String): Boolean =
    passwordHasher.matches(hashedPassword, rawPassword)
}

object UserPassword {

  lazy val passwordHasher = new BCryptPasswordHasher()

  def fromRawPassword(userId: User.Id, rawPassword: String) =
    new UserPassword(
      userId         = userId,
      hashedPassword = passwordHasher.hash(rawPassword)
    )
}
