package controllers

import model.ViewValueToDo.ToDoFormData
import lib.model.{ToDo, ToDoCategory}
import play.api.i18n.I18nSupport
import play.api.mvc._
import lib.persistence.onMySQL
import model.{ViewValueToDo, ViewValueToDoCategory}
import play.api.data.Form

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ToDoController @Inject() (val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController with I18nSupport {

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
          case _                  =>
            ViewValueToDo(
              toDo.v.id,
              toDo.v.title,
              toDo.v.body,
              ToDo.States(toDo.v.state).name,
              "なし",
              -1
            )
        }
      })
      Ok(views.html.todo.List(toDoInfoList))
    }
  }

  private def getViewValueToDoCategories(toDoCategories: Seq[ToDoCategory.EmbeddedId]) = {
    toDoCategories.map(toDoCategory => ViewValueToDoCategory(toDoCategory.v.id, toDoCategory.v.name, toDoCategory.v.slug, toDoCategory.v.color))
  }

  def register() = Action async { implicit request: Request[AnyContent] =>
    for {
      toDoCategories <- onMySQL.ToDoCategoryRepository.all()
    } yield {
      Ok(views.html.todo.Store(getViewValueToDoCategories(toDoCategories), ViewValueToDo.form))
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
            BadRequest(views.html.todo.Store(getViewValueToDoCategories(toDoCategories), formWithErrors))
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
            Redirect(routes.ToDoController.list())
          }
        }
      )
  }

  def edit(id: Long) = Action async { implicit request: Request[AnyContent] =>
    val toDoCategories = onMySQL.ToDoCategoryRepository.all()
    for {
      toDo           <- onMySQL.ToDoRepository.get(id.asInstanceOf[ToDo.Id])
      toDoCategories <- toDoCategories
    } yield {
      toDo match {
        case Some(toDo) =>
          Ok(
            views.html.todo.Edit(
              toDo.v.id.getOrElse(0),
              getViewValueToDoCategories(toDoCategories),
              ViewValueToDo.form
            )
          )
        case None       => NotFound(views.html.error.page404())
      }
    }
  }

  def update(id: Long) = Action async { implicit request: Request[AnyContent] =>
    ViewValueToDo.form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[ToDoFormData]) => {
          for {
            toDoCategories <- onMySQL.ToDoCategoryRepository.all()
          } yield {
            BadRequest(views.html.todo.Edit(id, getViewValueToDoCategories(toDoCategories), formWithErrors))
          }
        },
        (data: ToDoFormData) => {
          for {
            oToDo  <- onMySQL.ToDoRepository.get(id.asInstanceOf[ToDo.Id])
            result <- {
              oToDo match {
                case Some(toDo) =>
                  onMySQL.ToDoRepository.update(
                    toDo.map(
                      _.copy(
                        title      = data.title,
                        categoryId = data.categoryId,
                        body       = Some(data.body),
                        state      = data.state.get.toShort
                      )
                    )
                  )
                case None       =>
                  for {
                    toDoCategories <- onMySQL.ToDoCategoryRepository.all()
                  } yield {
                    BadRequest(views.html.todo.Edit(id, getViewValueToDoCategories(toDoCategories), ViewValueToDo.form))
                  }
              }
            }
          } yield {
            result match {
              case Some(_) => Redirect(routes.ToDoController.list())
              case _       => NotFound(views.html.error.page404())
            }
          }
        }
      )
  }

  def delete() = Action async { implicit request: Request[AnyContent] =>
    val idOpt = request.body.asFormUrlEncoded.get("id").headOption
    idOpt match {
      case None     => Future.successful(NotFound(views.html.error.page404()))
      case Some(id) =>
        for {
          result <- onMySQL.ToDoRepository.remove(id.toLong.asInstanceOf[ToDo.Id])
        } yield {
          result match {
            case None => NotFound(views.html.error.page404())
            case _    => Redirect(routes.ToDoController.list())
          }
        }
    }
  }
}
