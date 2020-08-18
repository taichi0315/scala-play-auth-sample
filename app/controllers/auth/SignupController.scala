package controllers.auth

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import play.api.i18n.I18nSupport
import scala.concurrent.{ExecutionContext, Future}

import model.auth.ViewValueAuthSignup
import form.auth.SignupFormData
import libs.model.{User, UserPassword}
import libs.dao.{UserDAO, UserPasswordDAO}

@Singleton
class SignupController @Inject()(
  val userDao:              UserDAO,
  val userPasswordDao:      UserPasswordDAO,
  val controllerComponents: ControllerComponents
) (implicit val ec: ExecutionContext)
extends BaseController with I18nSupport {

  import play.api.data.Form  

  private val postUrl:  Call  = controllers.auth.routes.SignupController.post()
  private val indexUrl: Call = controllers.routes.HomeController.index()

  def get() = Action { implicit request: Request[AnyContent] =>
    val vv: ViewValueAuthSignup =
      ViewValueAuthSignup(
        form    = SignupFormData.form,
        postUrl = postUrl
      )
    Ok(views.html.auth.Signup(vv))
  }

  def post() = Action.async { implicit request: Request[AnyContent] =>
    SignupFormData.form.bindFromRequest().fold(
      (formWithErrors: Form[SignupFormData]) => {
        val vv: ViewValueAuthSignup =
          ViewValueAuthSignup(
            form    = SignupFormData.form,
            postUrl = postUrl
          )
        Future.successful(BadRequest(views.html.auth.Signup(vv)))
      },
      (signup: SignupFormData) => {
        for {
          userOpt: Option[User] <- userDao.getByName(signup.name)
          result:  Result       <- userOpt match {
            case Some(_) => Future.successful(BadRequest("name is already used"))
            case None    =>
              for {
                userId: User.Id       <- userDao.add(signup.toUser)
                _                     <- userPasswordDao.add(signup.toUserPassword(userId))
              } yield Redirect(indexUrl)
          }
        } yield result
      })
    }
}
