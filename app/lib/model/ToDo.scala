package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// ユーザーを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import ToDo._
case class ToDo(
    id: Option[Id],
    categoryId: Long,
    title: String,
    body: Option[String] = None,
    state: Short,
    updatedAt: LocalDateTime = NOW,
    createdAt: LocalDateTime = NOW
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object ToDo {

  val Id = the[Identity[Id]]
  type Id = Long @@ ToDo
  type WithNoId = Entity.WithNoId[Id, ToDo]
  type EmbeddedId = Entity.EmbeddedId[Id, ToDo]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class Status(val code: Short, val name: String)
      extends EnumStatus
  object Status extends EnumStatus.Of[Status] {
    case object IS_INACTIVE extends Status(code = 0, name = "無効")
    case object IS_ACTIVE extends Status(code = 100, name = "有効")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(
      categoryId: Long,
      title: String,
      body: Option[String],
      state: Short
  ): WithNoId = {
    new Entity.WithNoId(
      new ToDo(
        id = None,
        categoryId = categoryId,
        title = title,
        body = body,
        state = state
      )
    )
  }
}
