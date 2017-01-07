package org.shapelogic.sc.operation

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
}