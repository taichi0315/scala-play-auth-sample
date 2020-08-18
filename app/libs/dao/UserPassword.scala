package libs.dao

import javax.inject.Inject
import scala.concurrent.Future

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider

import libs.model.{User, UserPassword}

class UserPasswordDAO @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  lazy val slick = TableQuery[UserPasswordTable]

  def get(userId: User.Id): Future[Option[UserPassword]] =
    db.run {
      slick
        .filter(_.userId === userId)
        .result
        .headOption
    }

  def add(entity: UserPassword): Future[Int] = {
    db.run {
      slick += entity
    }
  }

  class UserPasswordTable(tag: Tag) extends Table[UserPassword](tag, "user_passwords") {

    def userId    = column[User.Id] ("user_id")
    def password  = column[String]  ("password")

    type TableElementTuple = (
      User.Id, String
    )

    def * = (userId, password) <> (
      (t: TableElementTuple) => UserPassword(
        t._1, t._2
      ),
      (v: TableElementType) => UserPassword.unapply(v)
    )
  }
}
