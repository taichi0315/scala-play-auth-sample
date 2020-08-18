package mvc.auth

import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}

trait AuthExtension {self: BaseControllerHelpers =>
  
  import ExecutionContext.Implicits.global

  def AuthAction(authProfile: AuthProfile): ActionBuilder[Request, AnyContent] =
    AuthActionBuilder(authProfile, parse.default)
}

case class AuthActionBuilder(
  auth: AuthProfile,
  parse: BodyParser[AnyContent]
) (implicit ec: ExecutionContext)
extends ActionBuilderImpl(parse) {
  
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) =
    auth.authenticate(request).flatMap {
      case Left(l)     => Future.successful(l)
      case Right((user, put)) => block {
        request.addAttr(auth.AttrKeys.Auth, user)
      }.map(put)
    }
}


