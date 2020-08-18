package form.auth

case class LoginFormData (
  name:     String,
  password: String
)

object LoginFormData {
  import play.api.data.Forms._
  import play.api.data.Form

  val form = Form(
    mapping(
      "name"     -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginFormData.apply)(LoginFormData.unapply)
  )
}
