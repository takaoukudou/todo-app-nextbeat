package lib.persistence.db

import ixias.persistence.model.Table
import lib.model.ToDoCategory
import slick.jdbc.JdbcProfile

import java.time.LocalDateTime

// ToDoTable: ToDoテーブルへのマッピングを行う
//~~~~~~~~~~~~~~
case class ToDoCategoryTable[P <: JdbcProfile]()(implicit val driver: P)
    extends Table[ToDoCategory, P] {
  import api._

  // Definition of DataSourceName
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave" -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  // Definition of Query
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  // Definition of Table
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  class Table(tag: Tag) extends BasicTable(tag, "to_do_category") {
    import ToDoCategory._
    // Columns
    val id = column[Id]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] =
      column[String]("name", O.Length(255, varying = true))
    val slug: Rep[String] = column[String]("slug", O.Length(64, varying = true))
    val color: Rep[Short] = column[Short]("color")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
    val createdAt = column[LocalDateTime]("created_at")

    type TableElementTuple = (
        Option[Id],
        String,
        String,
        Short,
        LocalDateTime,
        LocalDateTime
    )

    // DB <=> Scala の相互のmapping定義
    // Tuple(table) => Model
    def * = (id.?, name, slug, color, updatedAt, createdAt) <> (
      (t: TableElementTuple) =>
        ToDoCategory(
          t._1,
          t._2,
          t._3,
          t._4,
          t._5,
          t._6
        ),
      // Model => Tuple(table)
      (v: TableElementType) =>
        ToDoCategory.unapply(v).map { t =>
          (
            t._1,
            t._2,
            t._3,
            t._4,
            LocalDateTime.now(),
            t._6
          )
        }
    )
  }
}
