package model

import lib.model.ToDoCategory
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, shortNumber}

case class ViewValueToDoCategory(
    id:    ToDoCategory.Id,
    name:  String,
    slug:  String,
    color: Short
)

object ViewValueToDoCategory {
  case class ToDoCategoryFormData(name: String, slug: String, color: Short)

  val form = Form {
    mapping(
      "name"  -> nonEmptyText(maxLength = 255),
      "slug"  -> nonEmptyText(maxLength = 64).verifying(
        "英数字で入力してください",
        _.matches("^[a-zA-Z0-9]+$")
      ),
      "color" -> shortNumber
    )(ToDoCategoryFormData.apply)(ToDoCategoryFormData.unapply)
  }
}
