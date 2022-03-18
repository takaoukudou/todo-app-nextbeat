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
  sealed abstract class Colors(val code: Short, val name: String) extends EnumStatus
  object Colors extends EnumStatus.Of[Colors] {
    case object RED    extends Colors(code = 1, name = "レッド")
    case object BLUE   extends Colors(code = 2, name = "ブルー")
    case object GREEN  extends Colors(code = 3, name = "グリーン")
    case object YELLOW extends Colors(code = 4, name = "イエロー")
    case object PINK   extends Colors(code = 5, name = "ピンク")
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
