package controllers.auth

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import play.api.i18n.I18nSupport
import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT
import cats.implicits._

import mvc.auth.AuthMethods
import model.auth.ViewValueAuthLogin
import service.UserPasswordService
import form.auth.LoginFormData
import libs.model.{User, UserPassword}
import libs.dao.UserDAO

@Singleton
class LoginController @Inject()(
  val userDao:              UserDAO,
  val authMethods:          AuthMethods,
  val userPasswordService:  UserPasswordService,
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
        val result: EitherT[Future, Result, Result] =
          for {
            user         <- EitherT(userDao.getByName(login.name).map(_.toRight(NotFound("not found name"))))
            verifiedUser <- EitherT(userPasswordService.verifyPassword(user, login.password)).leftMap {
              case service.UnauthorizedErr => Unauthorized("invalid password") 
            }
            result       <- EitherT(authMethods.loginSuccess(verifiedUser, Redirect(homeUrl)).map(Right(_).withLeft))
          } yield result

        result.merge
      }
    )
  }
}
