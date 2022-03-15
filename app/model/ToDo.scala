package model

import lib.model.ToDoCategory
import play.api.data.{Form, FormError, Forms}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, text}
import play.api.data.format.{Formats, Formatter}

case class ViewValueToDo(
    id:       Option[Long],
    title:    String,
    body:     Option[String],
    stateStr: String,
    name:     String,
    color:    Short
)

object ViewValueToDo {
  implicit val categoryIdFormatter = new Formatter[ToDoCategory.Id] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ToDoCategory.Id] =
      Formats.longFormat.bind(key, data).right.map(_.asInstanceOf[ToDoCategory.Id])

    def unbind(key: String, value: ToDoCategory.Id): Map[String, String] = Map(key -> value.toString)
  }

  val categoryIdMapping = Forms.of[ToDoCategory.Id]

  case class ToDoFormData(title: String, body: String, categoryId: ToDoCategory.Id)
  val form = Form(
    mapping(
      "title"      -> nonEmptyText(maxLength = 255),
      "body"       -> text,
      "categoryId" -> categoryIdMapping
    )(ToDoFormData.apply)(ToDoFormData.unapply)
  )
}
