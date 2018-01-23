package tshy0931.github.grafree

import shapeless.Poly1

object poly {

  object encodeArg extends Poly1 {

    implicit def caseString = at[String]("\""+_+"\"")
    implicit def caseInt = at[Int](_.toString)
    implicit def caseBoolean = at[Boolean](_.toString)
  }
}
