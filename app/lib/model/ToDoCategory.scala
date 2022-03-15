package lib.model

import ixias.model._
import ixias.util.EnumStatus
import java.time.LocalDateTime

// ユーザーを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import lib.model.ToDoCategory._

case class ToDoCategory(
    id:        Option[Id],
    name:      String,
    slug:      String,
    color:     Short,
    updatedAt: LocalDateTime = NOW,
    createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object ToDoCategory {

  val Id = the[Identity[Id]]
  type Id         = Long @@ ToDoCategory
  type WithNoId   = Entity.WithNoId[Id, ToDoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, ToDoCategory]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class Status(val code: Short, val name: String) extends EnumStatus

  object Category extends EnumStatus.Of[Status] {
    case object RED   extends Status(code = 1, name = "フロントエンド")
    case object BLUE  extends Status(code = 2, name = "バックエンド")
    case object GREEN extends Status(code = 3, name = "インフラ")
  }
  object Colors   extends EnumStatus.Of[Status] {
    case object RED   extends Status(code = 1, name = "1")
    case object BLUE  extends Status(code = 2, name = "2")
    case object GREEN extends Status(code = 3, name = "3")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(
      name:  String,
      slug:  String,
      color: Short
  ): WithNoId = {
    new Entity.WithNoId(
      new ToDoCategory(
        id    = None,
        name  = name,
        slug  = slug,
        color = color
      )
    )
  }
}
