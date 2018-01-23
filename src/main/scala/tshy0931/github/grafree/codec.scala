package tshy0931.github.grafree

import shapeless._
import shapeless.labelled.FieldType
import cats.syntax.show._
import tshy0931.github.grafree.adt.{Alias, Argument, Directive, Field, Fragment, GrafreeObject}
import tshy0931.github.grafree.optics.nameL

object codec {

  trait GrafreeCodec[A] {

    def encode: A => String => GrafreeObject
  }

  object GrafreeCodec {

    def instance[A](enc: A => String => GrafreeObject) = new GrafreeCodec[A] {
      def encode: A => String => GrafreeObject = enc
    }

    // -------- primitives --------

    import TypeClass.Show._

    implicit val unitEncoder: GrafreeCodec[Unit] = instance[Unit](_ => name => GrafreeObject(fields = List(name)))
    implicit val aliasEncoder: GrafreeCodec[Alias] = instance[Alias](_ => name => GrafreeObject(alias = Some(s"$name: ")))
    implicit val fragmentEncoder: GrafreeCodec[Fragment] = instance[Fragment](frg => name => GrafreeObject(fragments = List(s"fragment $name on ${frg.on}")))
    implicit val directiveEncoder: GrafreeCodec[Directive] = instance[Directive](_ => name => GrafreeObject(directives = List(s"@$name")))
    implicit def argumentEncoder[A]: GrafreeCodec[Argument[A]] =
      instance[Argument[A]](arg => name => {

//        val argStr = encodeArg[A](arg.value) // TODO: WHY IT CANNOT FIND the Show[Argument[Int/String]] ???
        val argStr = if(arg.value.isInstanceOf[String]) "\"" + arg.value + "\"" else arg.value.toString // TODO: fix this workaround and use more generic solution
        GrafreeObject(arguments = List(s"$name: $argStr"))
      })

    implicit def fieldEncoder[A, R](implicit genA: LabelledGeneric.Aux[A, R],
                                    encoder: Lazy[GrafreeCodec[R]]): GrafreeCodec[Field[A]] =
      instance[Field[A]](fld => name => {

        val field: String = encoder.value.encode(genA.to(fld.value))(name).show
        GrafreeObject(fields = List(field))
      })

    // -------- product type --------

    implicit val hnilEncoder: GrafreeCodec[HNil] = instance[HNil](_ => name => GrafreeObject(name = name))

    implicit def hlistEncoder[K <: Symbol, H, T <: HList](implicit witness: Witness.Aux[K],
                                                          hEncoder: Lazy[GrafreeCodec[H]],
                                                          tEncoder: Lazy[GrafreeCodec[T]]): GrafreeCodec[::[FieldType[K, H], T]] =
      instance[FieldType[K, H] :: T] {
        case h :: t => name =>
          val hName = witness.value.name
          val objT: GrafreeObject = tEncoder.value.encode(t)(hName)
          val objH: GrafreeObject = hEncoder.value.encode(h)(hName)
          nameL.set(name)(objH.merge(objT))
      }

    // -------- coproduct type --------

    implicit val cnilEncoder: GrafreeCodec[CNil] = instance[CNil](_ => name => GrafreeObject(name = name))

    implicit def coproductEncoder[K <: Symbol, L, R <: Coproduct](implicit witness: Witness.Aux[K],
                                                                  lEncoder: Lazy[GrafreeCodec[L]],
                                                                  rEncoder: Lazy[GrafreeCodec[R]]): GrafreeCodec[:+:[FieldType[K, L], R]] =
      instance[FieldType[K, L] :+: R] {
        case Inl(l) => lEncoder.value.encode(l)
        case Inr(r) => rEncoder.value.encode(r)
      }

    // -------- generic --------

    implicit def genericEncoder[A, R](implicit genA: LabelledGeneric.Aux[A, R],
                                      encoder: Lazy[GrafreeCodec[R]]): GrafreeCodec[A] = {
      instance(a => encoder.value.encode(genA.to(a)))
    }
  }
}
