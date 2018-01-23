package tshy0931.github.grafree

import cats.Show
import tshy0931.github.grafree.adt.{Argument, GrafreeObject}

object TypeClass {

  object Show {

    implicit val showGrafreeObj: Show[GrafreeObject] = (t: GrafreeObject) => {
      val alias = t.alias.fold(""){ali => ali+": "}
      val args = if(t.arguments.isEmpty) "" else t.arguments.mkString("(",", ",")")
      val fields = if(t.fields.isEmpty) "" else s"{${t.fields.mkString(" ")}}"
      s"$alias${t.name}$args$fields"
    }

    implicit val showArgumentBoolean: Show[Argument[Boolean]] = (t: Argument[Boolean]) => t.value.toString
    implicit val showArgumentInt: Show[Argument[Int]] = (t: Argument[Int]) => t.value.toString
    implicit val showArgumentLong: Show[Argument[Long]] = (t: Argument[Long]) => t.value.toString
    implicit val showArgumentString: Show[Argument[String]] = (t: Argument[String]) => t.value.toString
  }
}
