package controllers

import lib.model.ToDo
import play.api.i18n.I18nSupport
import play.api.mvc._
import lib.persistence.ToDoRepository
import lib.persistence.ToDoCategoryRepository
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, shortNumber, text}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import slick.profile.MyDBProfile

case class ToDoFormData(title: String, body: String, category: Short)

@Singleton
class ListController @Inject() (
    val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BaseController
    with I18nSupport {

  val stateMap = Map((0, "TODO(着手前)"), (1, "進行中"), (2, "完了"))

  def list() = Action async { implicit request: Request[AnyContent] =>
    for {
      toDos <- ToDoRepository()(MyDBProfile).all()
      toDoCategories <- ToDoCategoryRepository()(MyDBProfile).all()
    } yield {
      val toDoInfoList = toDos.map(toDo => {
        toDoCategories.find(toDoCategory =>
          toDo.v.categoryId == toDoCategory.id
        ) match {
          case Some(toDoCategory) =>
            (
              toDo.v.id,
              toDo.v.title,
              toDo.v.body,
              stateMap(toDo.v.state),
              toDoCategory.v.name,
              toDoCategory.v.color
            )
        }
      })
      Ok(views.html.todo.List(toDoInfoList))
    }
  }

  val form = Form(
    mapping(
      "title" -> nonEmptyText(maxLength = 255),
      "body" -> text,
      "category" -> shortNumber
    )(ToDoFormData.apply)(ToDoFormData.unapply)
  )

  /** 登録処理実を行う
    */
  def store() = Action async { implicit request: Request[AnyContent] =>
    form
      .bindFromRequest()
      .fold(
        // 処理が失敗した場合に呼び出される関数
        (formWithErrors: Form[ToDoFormData]) => {
          Future.successful(BadRequest(views.html.todo.Store(formWithErrors)))
        },
        // 処理が成功した場合に呼び出される関数
        (toDoFormData: ToDoFormData) => {
          for {
            // データを登録。returnのidは不要なので捨てる
            _ <- ToDoRepository()(MyDBProfile)
              .add(
                ToDo(
                  toDoFormData.category,
                  toDoFormData.title,
                  Option(toDoFormData.body),
                  0
                )
              )
          } yield {
            Redirect(routes.ListController.list())
          }
        }
      )
  }
}
