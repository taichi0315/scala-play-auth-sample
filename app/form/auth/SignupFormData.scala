package form.auth

import libs.model.{User, UserPassword}

case class SignupFormData (
  name:     String,
  password: String
) {

  def toUser(): User =
    User(name)

  def toUserPassword(userId: User.Id): UserPassword =
    UserPassword.fromRawPassword(userId, password)
}

object SignupFormData {
  import play.api.data.Forms._
  import play.api.data.Form

  val form = Form(
    mapping(
      "name"     -> nonEmptyText,
      "password" -> nonEmptyText
    )(SignupFormData.apply)(SignupFormData.unapply)
  )
}
