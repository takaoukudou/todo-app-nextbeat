package model

case class ViewValueToDo(
  id: Option[Long],
  title: String,
  body: Option[String],
  stateStr: String,
  name: String,
  color: Short,
)