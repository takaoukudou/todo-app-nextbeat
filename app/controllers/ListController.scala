package controllers

import lib.model.ToDo.{ToDoFormData, form}
import lib.model.{ToDo, ToDoCategory}
import play.api.i18n.I18nSupport
import play.api.mvc._
import lib.persistence.onMySQL
import model.ViewValueToDo
import lib.model
import play.api.data.Form

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListController @Inject() (val controllerComponents: ControllerComponents)(implicit
    ec:                                                   ExecutionContext
) extends BaseController
    with I18nSupport {

  def list() = Action async { implicit request: Request[AnyContent] =>
    val toDos = onMySQL.ToDoRepository.all()
    for {
      toDos          <- toDos
      toDoCategories <- onMySQL.ToDoCategoryRepository.all()
    } yield {
      val toDoInfoList = toDos.map(toDo => {
        toDoCategories.find(toDoCategory => toDo.v.categoryId == toDoCategory.id) match {
          case Some(toDoCategory) =>
            ViewValueToDo(
              toDo.v.id,
              toDo.v.title,
              toDo.v.body,
              ToDo.States(toDo.v.state).name,
              ToDoCategory.Category(toDo.v.categoryId.toShort).name,
              ToDoCategory.Colors(toDoCategory.v.color).code
            )
        }
      })
      Ok(views.html.todo.List(toDoInfoList))
    }
  }

  /** 登録処理実を行う
    */
  def store() = Action async { implicit request: Request[AnyContent] =>
    ToDo.form
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
            _ <- onMySQL.ToDoRepository
                   .add(
                     ToDo(
                       toDoFormData.categoryId.asInstanceOf[ToDoCategory.Id],
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

  def edit(id: Long) = Action async { implicit request: Request[AnyContent] =>
    for {
      toDo <- onMySQL.ToDoRepository.get(id.asInstanceOf[ToDo.Id])
    } yield {
      toDo match {
        case Some(toDo) =>
          Ok(
            views.html.todo.Edit(
              toDo.v.id.getOrElse(0),
              form
            )
          )
        case None       => NotFound(views.html.error.page404())
      }
    }
  }

  def update(id: Long) = Action async { implicit request: Request[AnyContent] =>
    ToDo.form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[ToDoFormData]) => {
          Future
            .successful(BadRequest(views.html.todo.Edit(id, formWithErrors)))
        },
        (data: ToDoFormData) => {
          for {
            oToDo  <- onMySQL.ToDoRepository.get(id.asInstanceOf[ToDo.Id])
            result <- onMySQL.ToDoRepository.update(
                        oToDo match {
                          case Some(toDo) =>
                            toDo.map(
                              _.copy(
                                title      = data.title,
                                categoryId = data.categoryId
                                  .asInstanceOf[model.ToDoCategory.Id],
                                body       = Some(data.body),
                                state      = data.state.get.toShort
                              )
                            )
                        }
                      )
          } yield {
            result match {
              case Some(_) => Redirect(routes.ListController.list())
              case _       => NotFound(views.html.error.page404())
            }
          }
        }
      )
  }

  def delete() = Action async { implicit request: Request[AnyContent] =>
    val idOpt = request.body.asFormUrlEncoded.get("id").headOption
    for {
      result <- onMySQL.ToDoRepository.remove(
                  idOpt.map(_.toLong.asInstanceOf[ToDo.Id]).get
                )
    } yield {
      result match {
        case None => NotFound(views.html.error.page404())
        case _    => Redirect(routes.ListController.list())
      }
    }
  }
}
