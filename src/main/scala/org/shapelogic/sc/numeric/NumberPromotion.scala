package org.shapelogic.sc.numeric

import spire.math.Numeric
import spire.implicits._
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

  def demote(out: Out): I
}

trait HasNumberPromotion[I] {
  def promotor: NumberPromotion[I]
}

object NumberPromotion {
  import PrimitiveNumberPromoters._

  /**
   * Lemma pattern
   */
  type Aux[I, O] = NumberPromotion[I] { type Out = O }

  type Same[I] = NumberPromotion[I] { type Out = I }

  class NumberIdPromotion[@specialized(Byte, Short, Int, Long, Float, Double) I: ClassTag]() extends NumberPromotion[I] {
    type Out = I
    val typeOfInput = implicitly[ClassTag[I]]
    if (verboseLogging)
      println(s"============= NumberIdPromotion typeOfInput: $typeOfInput")

    def promote(input: I): I = {
      input
    }

    def demote(input: I): I = {
      input
    }
  }

  trait NumberIdPromotionTrait[I] extends NumberPromotion[I] {
    type Out = I

    def promote(input: I): I = {
      if (verboseLogging)
        println(s"Default input: $input")
      input
    }
  }

  object ByteIdentityPromotion extends NumberIdPromotion[Byte] {
    implicit val floatPromotion = new NumberIdPromotion[Float]()

  }

  /**
   * When this was specialized I got warning
   *
   *  Old error message when it was specialized
   *  class NumberPromotion must be a trait. Specialized version of
   *  class NumberIdPromotion will inherit generic
   *  org.shapelogic.sc.operation.NumberPromotion[Boolean]
   *
   *  I am afraid that this will cause boxing of numbers
   */
  class LowPriorityImplicits[I: ClassTag] {
    //    implicit val floatIdPromotionFloat = new NumberIdPromotion[Float]()
    implicit val promotorL = new NumberIdPromotion[I]
  }

  abstract class LowPriorityImplicitsTrait[@specialized I: ClassTag] {
    implicit val promotorL = new NumberIdPromotion[I]
  }

  trait LowPriorityImplicitsByte {
    implicit val numberIdPromotionByte = new NumberIdPromotion[Byte]()
  }

  trait HighWithLowPriorityImplicitsByte extends LowPriorityImplicitsByte {
    implicit val piorityNumberIdPromotionByte = BytePromotion
  }

  class HighWithLowPriorityImplicits[@specialized I: ClassTag] extends LowPriorityImplicits[I] {
    val typeOfInput = implicitly[ClassTag[I]]
    if (verboseLogging)
      println(s"HighWithLowPriorityImplicits typeOfInput: $typeOfInput")
    implicit lazy val piorityNumberIdPromotionByte = BytePromotion
  }

  class HighPriorityImplicits[@specialized I: ClassTag] // extends LowPriorityImplicits 
  {
    //    val low = new LowPriorityImplicits[I]()
    //    import low._
    implicit def byteToIntPromotion: NumberPromotion[Byte] = BytePromotion
  }
}