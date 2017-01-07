package org.shapelogic.sc.operation

import spire.math.Numeric
import scala.specialized
import scala.reflect.ClassTag

trait NumberPromotion[I] {
  type Output
  def promote(input: I): Output
}

object NumberPromotion {
  val byteMask: Int = 0xff

  object BytePromotion extends NumberPromotion[Byte] {
    type Output = Int
    def promote(input: Byte): Int = {
      byteMask & byteMask
    }
  }

  class NumberPromotionIdentity[@specialized N: ClassTag: Numeric] extends NumberPromotion[N] {
    type Output = N
    def promote(input: N): N = {
      input
    }
  }

  object ByteIdentityPromotion extends NumberPromotionIdentity[Byte] {

  }
}