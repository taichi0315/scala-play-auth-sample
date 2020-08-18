package controllers.auth

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import play.api.i18n.I18nSupport
import scala.concurrent.{ExecutionContext, Future}

import mvc.auth.AuthDataStore
import model.auth.ViewValueAuthLogin
import form.auth.LoginFormData
import libs.model.{User, UserPassword}
import libs.dao.{UserDAO, UserPasswordDAO}

@Singleton
class LoginController @Inject()(
  val userDao:              UserDAO,
  val userPasswordDao:      UserPasswordDAO,
  val authDataStore:        AuthDataStore,
  val controllerComponents: ControllerComponents
) (implicit val ec: ExecutionContext)
extends BaseController with I18nSupport {

  import play.api.data.Form  

  private val postUrl: Call = controllers.auth.routes.LoginController.post()
  private val homeUrl: Call = controllers.routes.HomeController.home()

  def get() = Action { implicit request: Request[AnyContent] =>
    val vv: ViewValueAuthLogin =
      ViewValueAuthLogin(
        form    = LoginFormData.form,
        postUrl = postUrl
      )
    Ok(views.html.auth.Login(vv))
  }

  def post() = Action.async { implicit request: Request[AnyContent] =>
    LoginFormData.form.bindFromRequest().fold(
      (formWithErrors: Form[LoginFormData]) => {
        val vv: ViewValueAuthLogin =
          ViewValueAuthLogin(
            form    = LoginFormData.form,
            postUrl = postUrl
          )
        Future.successful(BadRequest(views.html.auth.Login(vv)))
      },
      (login: LoginFormData) => {
        for {
          userOpt: Option[User] <- userDao.getByName(login.name)
          result:  Result       <- userOpt match {
            case None       => Future.successful(NotFound("not found name"))
            case Some(user) =>
              for {
                Some(userPassword) <- userPasswordDao.get(user.withId)
                result             <- userPassword.verify(login.password) match {
                  case false => Future.successful(Unauthorized("invalid password"))
                  case true  => authDataStore.loginSuccess(user, Redirect(homeUrl))
                }
              } yield result
          }
        } yield result
     })
    }
}
