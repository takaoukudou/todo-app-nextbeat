package controllers

import play.api.i18n.I18nSupport
import play.api.mvc._
import lib.persistence.ToDoRepository
import lib.persistence.ToDoCategoryRepository
import javax.inject._
import scala.concurrent.ExecutionContext
import slick.profile.MyDBProfile

@Singleton
class ListController @Inject() (
    val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BaseController
    with I18nSupport {
  def list() = Action async { implicit request: Request[AnyContent] =>
    for {
      toDos <- ToDoRepository()(MyDBProfile).all()
      toDoCategories <- ToDoCategoryRepository()(MyDBProfile).all()
    } yield {
      Ok(views.html.todo.List(toDos, toDoCategories))
    }
  }
}
