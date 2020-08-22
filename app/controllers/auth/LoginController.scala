package controllers.auth

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import play.api.i18n.I18nSupport
import scala.concurrent.{ExecutionContext, Future}

import mvc.auth.AuthMethods
import model.auth.ViewValueAuthLogin
import form.auth.LoginFormData
import libs.model.{User, UserPassword}
import libs.dao.{UserDAO, UserPasswordDAO}

@Singleton
class LoginController @Inject()(
  val userDao:              UserDAO,
  val userPasswordDao:      UserPasswordDAO,
  val authMethods:          AuthMethods,
  val controllerComponents: ControllerComponents
) (implicit val ec: ExecutionContext)
extends BaseController with I18nSupport {

  import play.api.data.Form  

  private val postUrl: Call = controllers.auth.routes.LoginController.post()
  private val homeUrl: Call = controllers.routes.HomeController.home()

  def get() = Action { implicit request =>
    val vv: ViewValueAuthLogin =
      ViewValueAuthLogin(
        form    = LoginFormData.form,
        postUrl = postUrl
      )
    Ok(views.html.auth.Login(vv))
  }

  def post() = Action.async { implicit request =>
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
        userDao.getByName(login.name).flatMap(userOpt =>
          userOpt match {
            case None       => Future.successful(NotFound("not found name"))
            case Some(user) =>
              userPasswordDao.get(user.withId).flatMap(userPasswordOpt =>
                userPasswordOpt.get.verify(login.password) match {
                  case false => Future.successful(Unauthorized("invalid password"))
                  case true  => authMethods.loginSuccess(user, Redirect(homeUrl))
                }
              )
          }
        )
     })
    }
}
