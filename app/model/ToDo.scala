package model

import lib.model.ToDoCategory
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, optional, text}

case class ViewValueToDo(
    id:          Option[Long],
    title:       String,
    body:        Option[String],
    stateStr:    String,
    categoryStr: String,
    color:       Short
)

object ViewValueToDo {
  case class ToDoFormData(title: String, body: String, categoryId: ToDoCategory.Id, state: Option[String])
  val form = Form(
    mapping(
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text,
      "categoryId" -> longNumber.transform[ToDoCategory.Id](_.asInstanceOf[ToDoCategory.Id], _.toLong),
      "state"      -> optional(text)
    )(ToDoFormData.apply)(ToDoFormData.unapply)
  )
}
