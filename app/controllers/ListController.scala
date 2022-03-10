package controllers

import play.api.i18n.I18nSupport
import play.api.mvc._
import slick.repositories.ToDoRepository

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ListController @Inject() (
    val controllerComponents: ControllerComponents,
    toDoRepository: ToDoRepository
)(implicit ec: ExecutionContext)
    extends BaseController
    with I18nSupport {
  def list() = Action async { implicit request: Request[AnyContent] =>
    for {
      results <- toDoRepository.all()
    } yield {
      println(results)
      Ok(views.html.todo.List(results))
    }
  }
}
