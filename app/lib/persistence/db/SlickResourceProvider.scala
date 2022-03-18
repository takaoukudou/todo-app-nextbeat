package lib.persistence.db

import slick.jdbc.JdbcProfile

// Tableを扱うResourceのProvider
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
trait SlickResourceProvider[P <: JdbcProfile] {

  implicit val driver: P
  object ToDoTable         extends ToDoTable
  object ToDoCategoryTable extends ToDoCategoryTable
  // --[ テーブル定義 ] --------------------------------------
  lazy val AllTables = Seq(
    ToDoTable,
    ToDoCategoryTable
  )
}
