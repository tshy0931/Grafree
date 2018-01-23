package tshy0931.github.grafree

import monocle.Lens
import monocle.macros.GenLens
import tshy0931.github.grafree.adt.GrafreeObject

object optics {

  val aliasO: Lens[GrafreeObject, Option[String]] = GenLens[GrafreeObject](_.alias)
  val attributesL: Lens[GrafreeObject, List[String]] = GenLens[GrafreeObject](_.arguments)
  val fieldsL: Lens[GrafreeObject, List[String]] = GenLens[GrafreeObject](_.fields)
  val nameL: Lens[GrafreeObject, String] = GenLens[GrafreeObject](_.name)
}