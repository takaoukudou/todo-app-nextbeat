package slick.models

import java.time.LocalDateTime

case class ToDo(
    id: Option[Long],
    categoryId: Long,
    title: String,
    body: Option[String],
    state: Short,
    createdAt: LocalDateTime = LocalDateTime.now,
    updatedAt: LocalDateTime = LocalDateTime.now
)
