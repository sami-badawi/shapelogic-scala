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

object NumberPromotion {

  /**
   * Lemma pattern
   */
  type Aux[I, O] = NumberPromotion[I] { type Out = O }

  val byteMask: Int = 0xff

  object BytePromotion extends NumberPromotion[Byte] {
    type Out = Int
    def promote(input: Byte): Int = {
      byteMask & byteMask
    }
  }

  class NumberPromotionIdentity[@specialized N: ClassTag: Numeric] extends NumberPromotion[N] {
    type Out = N
    def promote(input: N): N = {
      input
    }
  }

  object ByteIdentityPromotion extends NumberPromotionIdentity[Byte] {

  }
}