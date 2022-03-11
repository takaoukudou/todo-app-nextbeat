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
      val toDoInfoList = toDos.map(toDo => {
        toDoCategories.find(toDoCategory => toDo.id == toDoCategory.id) match {
          case Some(toDoCategory) =>
            (
              toDo.v.id,
              toDo.v.title,
              toDo.v.body,
              toDo.v.state match {
                case 0 => "TODO(着手前)"
                case 1 => "進行中"
                case 2 => "完了"
                case _ => ""
              },
              toDoCategory.v.name,
              toDoCategory.v.color
            )
        }
      })
      Ok(views.html.todo.List(toDoInfoList))
    }
  }
}
