package lib

package object persistence {

  val default = onMySQL

  object onMySQL {
    implicit lazy val driver = slick.jdbc.MySQLProfile
    object ToDoRepository extends ToDoRepository
    object ToDoCategoryRepository extends ToDoCategoryRepository
  }
}
