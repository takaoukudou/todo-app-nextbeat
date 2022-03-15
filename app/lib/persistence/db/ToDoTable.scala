package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.Table
import lib.model.{ToDo, ToDoCategory}

// ToDoTable: ToDoテーブルへのマッピングを行う
//~~~~~~~~~~~~~~
case class ToDoTable[P <: JdbcProfile]()(implicit val driver: P) extends Table[ToDo, P] {

  import api._

  // Definition of DataSourceName
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  lazy val dsn = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/to_do"),
    "slave"  -> DataSourceName("ixias.db.mysql://slave/to_do")
  )

  // Definition of Query
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  // Definition of Table
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  class Table(tag: Tag) extends BasicTable(tag, "to_do") {

    import ToDo._

    // Columns
    /* @1 */
    val id                            = column[Id]("id", O.AutoInc, O.PrimaryKey)
    val categoryId: Rep[Long]         = column[Long]("category_id")
    val title: Rep[String]            = column[String]("title", O.Length(255, varying = true))
    val body: Rep[Option[String]]     = column[Option[String]]("body", O.Default(None))
    val state: Rep[Short]             = column[Short]("state")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
    val createdAt                     = column[LocalDateTime]("created_at")

    type TableElementTuple = (
        Option[Id],
        Long,
        String,
        Option[String],
        Short,
        LocalDateTime,
        LocalDateTime
    )

    // DB <=> Scala の相互のmapping定義
    def * = (id.?, categoryId, title, body, state, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      (t: TableElementTuple) =>
        ToDo(
          t._1,
          t._2.asInstanceOf[ToDoCategory.Id],
          t._3,
          t._4,
          t._5,
          t._6,
          t._7
        ),
      // Model => Tuple(table)
      (v: TableElementType) =>
        ToDo.unapply(v).map { t =>
          (
            t._1,
            t._2,
            t._3,
            t._4,
            t._5,
            LocalDateTime.now(),
            t._7
          )
        }
    )
  }
}
