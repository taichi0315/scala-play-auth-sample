package controllers

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}

import mvc.auth.AuthenticationAction

@Singleton
class HomeController @Inject()(
  val authenticateAction: AuthenticationAction,
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) 
extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
     Ok(views.html.index())
  }

  def home() = (Action andThen authenticateAction).async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.home("user")))
  }
}
