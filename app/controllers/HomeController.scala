package controllers

import javax.inject.{Singleton, Inject}
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext) 
extends BaseController {

  def index() = Action { implicit request: Request[AnyContent] =>
     Ok(views.html.index())
  }

  def home() = Action.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.home("user")))
  }
}
