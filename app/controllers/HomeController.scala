package controllers

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}

import mvc.auth.AuthAction

@Singleton
class HomeController @Inject()(
  val authAction: AuthAction,
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) 
extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
     Ok(views.html.index())
  }

  def home() = (Action andThen authAction).async { implicit request =>
    Future.successful(Ok(views.html.home(request.user.name)))
  }
}
