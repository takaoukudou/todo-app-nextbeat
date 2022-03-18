package model

import lib.model.ToDoCategory

case class ViewValueToDoCategory(
    id:   ToDoCategory.Id,
    name: String
)
