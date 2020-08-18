package libs.model

import libs.model.User

case class UserPassword(
  userId:   User.Id,
  password: String
) {
  def verify(input: String): Boolean = (password == input)
}
