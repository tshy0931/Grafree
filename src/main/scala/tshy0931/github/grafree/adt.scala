package tshy0931.github.grafree

import tshy0931.github.grafree.optics._

object adt {

  sealed trait GraphQLValue
  case class Argument[A](value: A) extends GraphQLValue
  case class Variable(prefix: String = "$") extends GraphQLValue
  case class Alias() extends GraphQLValue
  case class Field[A](value: A) extends GraphQLValue
  case class Fragment(on: Option[String] = None) extends GraphQLValue
  case class Directive(prefix: String = "@") extends GraphQLValue

  def query(name: => String):String = s"query $name"
  def mutation(name: => String):String = s"mutation $name"

  sealed trait GrafreeADT
  case class GrafreeObject(name: String = "???",
                           alias: Option[String] = None,
                           arguments: List[String] = Nil,
                           fields: List[String] = Nil,
                           fragments: List[String] = Nil,
                           directives: List[String] = Nil
                          ) extends GrafreeADT {

    //TODO: handle alias, directive, etc.
    def merge(that: GrafreeObject) =
      (attributesL.modify(that.arguments ::: _) andThen fieldsL.modify(that.fields ::: _))(this)
  }
}
