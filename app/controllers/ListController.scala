package controllers

import model.ViewValueToDo.ToDoFormData
import lib.model.{ToDo, ToDoCategory}
import play.api.i18n.I18nSupport
import play.api.mvc._
import lib.persistence.onMySQL
import model.{ViewValueToDo, ViewValueToDoCategory}
import play.api.data.Form
import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ListController @Inject() (val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController with I18nSupport {

  def list() = Action async { implicit request: Request[AnyContent] =>
    val toDos = onMySQL.ToDoRepository.all()
    for {
      toDoCategories <- onMySQL.ToDoCategoryRepository.all()
      toDos          <- toDos
    } yield {
      val toDoInfoList = toDos.map(toDo => {
        toDoCategories.find(toDoCategory => toDo.v.categoryId == toDoCategory.id) match {
          case Some(toDoCategory) =>
            ViewValueToDo(
              toDo.v.id,
              toDo.v.title,
              toDo.v.body,
              ToDo.States(toDo.v.state).name,
              toDoCategory.v.name,
              ToDoCategory.Colors(toDoCategory.v.color).code
            )
        }
      })
      Ok(views.html.todo.List(toDoInfoList))
    }
  }

  def register() = Action async { implicit request: Request[AnyContent] =>
    for {
      toDoCategories <- onMySQL.ToDoCategoryRepository.all()
    } yield {
      val viewValueToDoCategories = toDoCategories.map(toDoCategory => ViewValueToDoCategory(toDoCategory.v.id, toDoCategory.v.name))
      Ok(views.html.todo.Store(viewValueToDoCategories, ViewValueToDo.form))
    }
  }

  /** 登録処理実を行う
    */
  def store() = Action async { implicit request: Request[AnyContent] =>
    ViewValueToDo.form
      .bindFromRequest()
      .fold(
        // 処理が失敗した場合に呼び出される関数
        (formWithErrors: Form[ToDoFormData]) => {
          for {
            toDoCategories <- onMySQL.ToDoCategoryRepository.all()
          } yield {
            val viewValueToDoCategories = toDoCategories.map(toDoCategory => ViewValueToDoCategory(toDoCategory.v.id, toDoCategory.v.name))
            BadRequest(views.html.todo.Store(viewValueToDoCategories, formWithErrors))
          }
        },
        // 処理が成功した場合に呼び出される関数
        (toDoFormData: ToDoFormData) => {
          for {
            // データを登録。returnのidは不要なので捨てる
            _ <- onMySQL.ToDoRepository
                   .add(
                     ToDo(
                       toDoFormData.categoryId,
                       toDoFormData.title,
                       Option(toDoFormData.body),
                       ToDo.States.TODO.code
                     )
                   )
          } yield {
            Redirect(routes.ListController.list())
          }
        }
      )
  }
}
