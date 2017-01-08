package org.shapelogic.sc.operation

import spire.math.Numeric
import scala.specialized
import scala.reflect.ClassTag

/**
 * You frequently have to change from
 */
trait NumberPromotion[I] {
  /*
   * Do not know this type in advance
   */
  type Out

  /**
   * Promote to a place that is better for computation
   * One use is to fix signed Byte to unsigned Byte
   */
  def promote(input: I): Out
}

trait HasNumberPromotion[I] {
  def promotor: NumberPromotion[I]
}

object NumberPromotion {
  val verboseLogging = false

  /**
   * Lemma pattern
   */
  type Aux[I, O] = NumberPromotion[I] { type Out = O }

  type AuxId[I] = NumberPromotion[I] { type Out = I }

  class NumberIdPromotion[@specialized I: ClassTag: Numeric: Ordering]() extends NumberPromotion[I] {
    type Out = I

    def promote(input: I): I = {
      if (verboseLogging)
        println(s"Default input: $input")
      input
    }
  }

  val byteMask: Int = 0xff

  object BytePromotion extends NumberPromotion[Byte] {
    println("Hello World, BytePromotion")
    type Out = Int
    def promote(input: Byte): Int = {
      val res = input & byteMask
      println(s"Promote: $input to $res")
      res
    }
  }

  object ByteIdentityPromotion extends NumberIdPromotion[Byte] {
    implicit val floatPromotion = new NumberIdPromotion[Float]()

  }

  class LowPriorityImplicits[@specialized I: ClassTag: Numeric: Ordering] {
    //    implicit val floatIdPromotionFloat = new NumberIdPromotion[Float]()
    implicit val promotorL = new NumberIdPromotion[I]
  }

  trait LowPriorityImplicitsByte {
    implicit val numberIdPromotionByte = new NumberIdPromotion[Byte]()
  }

  trait HighWithLowPriorityImplicitsByte extends LowPriorityImplicitsByte {
    implicit val piorityNumberIdPromotionByte = BytePromotion
  }

  class HighPriorityImplicits[@specialized I: ClassTag: Numeric: Ordering] // extends LowPriorityImplicits 
  {
    //    val low = new LowPriorityImplicits[I]()
    //    import low._
    implicit def byteToIntPromotion: NumberPromotion[Byte] = BytePromotion
  }
}