package model

import lib.model.ToDoCategory
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, text}

case class ViewValueToDo(
    id:       Option[Long],
    title:    String,
    body:     Option[String],
    stateStr: String,
    name:     String,
    color:    Short
)

object ViewValueToDo {
  case class ToDoFormData(title: String, body: String, categoryId: ToDoCategory.Id)
  val form = Form(
    mapping(
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text,
      "categoryId" -> longNumber.transform[ToDoCategory.Id](_.asInstanceOf[ToDoCategory.Id], _.toLong)
    )(ToDoFormData.apply)(ToDoFormData.unapply)
  )
}
