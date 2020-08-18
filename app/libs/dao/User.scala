package libs.dao

import javax.inject.Inject
import scala.concurrent.Future

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider

import libs.model.User

class UserDAO @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  lazy val slick = TableQuery[UserTable]

  def getByName(name: String): Future[Option[User]] =
    db.run {
      slick
        .filter(_.name === name)
        .result
        .headOption
    }

  def add(user: User): Future[User.Id] =
    db.run {
      (slick returning slick.map(_.id)) += user
    }

  class UserTable(tag: Tag) extends Table[User](tag, "users") {

    def id    = column[User.Id] ("id", O.AutoInc)
    def name  = column[String]  ("name")

    type TableElementTuple = (
      Option[User.Id], String
    )

    def * = (id.?, name) <> (
      (t: TableElementTuple) => User(t._1, t._2),
      (v: TableElementType) => User.unapply(v)
    )
  }
}
