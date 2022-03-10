package slick.repositories

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{GetResult, JdbcProfile}
import slick.models.ToDo

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ToDoRepository @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val query = new TableQuery(tag => new ToDoTable(tag))

  // ########## [DBIO Methods] ##########
  def all(): Future[Seq[ToDo]] = db.run(query.result)

  // ########## [Table Mapping] ##########
  private class ToDoTable(_tableTag: Tag)
      extends Table[ToDo](_tableTag, Some("to_do"), "to_do") {

    // Tableとのカラムマッピング
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val categoryId: Rep[Long] = column[Long]("category_id")
    val title: Rep[String] =
      column[String]("title", O.Length(255, varying = true))
    val body: Rep[Option[String]] =
      column[Option[String]]("body", O.Default(None))
    val state: Rep[Short] = column[Short]("state")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
    val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")

    // Plain SQLでデータ取得を行う用のマッピング
    implicit def GetResultToDo(implicit
        e0: GetResult[Long],
        e1: GetResult[String],
        e2: GetResult[Short],
        e3: GetResult[LocalDateTime]
    ): GetResult[ToDo] = GetResult { prs =>
      import prs._
      ToDo.tupled(
        (
          Some(<<[Long]),
          <<[Long],
          <<[String],
          Some(<<[String]),
          <<[Short],
          <<[LocalDateTime],
          <<[LocalDateTime]
        )
      )
    }

    // model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
    // O.PrimaryKeyはColumnOptionTypeとなるためid.?でidをOptionとして取り扱い可能
    def * = (
      id.?,
      categoryId,
      title,
      body,
      state,
      createdAt,
      updatedAt
    ) <> (ToDo.tupled, ToDo.unapply)

    def ? = (
      (
        Rep.Some(id),
        Rep.Some(categoryId),
        Rep.Some(title),
        Rep.Some(body),
        Rep.Some(state),
        Rep.Some(createdAt),
        Rep.Some(updatedAt)
      )
    ).shaped.<>(
      { r =>
        import r._;
        _1.map(_ =>
          ToDo.tupled(
            (Option(_1.get), _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)
          )
        )
      },
      (_: Any) =>
        throw new Exception("Inserting into ? projection not supported.")
    )

  }
}
