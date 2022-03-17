package model

import lib.model.ToDoCategory.Id
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, shortNumber}

case class ViewValueToDoCategory(
    id:    Option[Id],
    name:  String,
    slug:  String,
    color: Short
)

object ViewValueToDoCategory {
  case class ToDoCategoryFormData(name: String, slug: String, color: Short)

  val form = Form {
    mapping(
      "name"  -> nonEmptyText(maxLength = 255),
      "slug"  -> nonEmptyText(maxLength = 64),
      "color" -> shortNumber
      //      "color" -> shortNumber.verifying(
//        "不正な番号です",
//        Constraints.pattern("[a-zA-Z_0-9]".r)
//      )
    )(ToDoCategoryFormData.apply)(ToDoCategoryFormData.unapply)
  }
}
