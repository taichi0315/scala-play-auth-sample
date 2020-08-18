package libs.model

import User.Id
case class User(
  id:    Option[Id],
  name:  String,
) {
  lazy val withId = id.get
}

object User {
  type Id = Long

  def apply(name: String) =
    new User(
      id    = None,
      name  = name,
    )
}
