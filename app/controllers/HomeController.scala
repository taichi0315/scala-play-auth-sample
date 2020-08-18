package controllers

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}

import mvc.auth.{AuthExtension, AuthProfile}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  val authProfile:          AuthProfile,
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) 
extends BaseController with AuthExtension {

  def index() = Action { implicit request: Request[AnyContent] =>
     Ok(views.html.index())
  }

  def home() = AuthAction(authProfile).async { implicit request: Request[AnyContent] =>
    authProfile.logined { user =>
      Future.successful(Ok(views.html.home(user.name)))
    }
  }
}
