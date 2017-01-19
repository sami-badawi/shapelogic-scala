package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.numeric.NumberPromotionMax
import org.shapelogic.sc.numeric.PrimitiveNumberPromoters

object Color2GrayHandler {
  class Color2GrayHandlerByte(
      val data: Array[Byte],
      val inputNumBands: Int,
      val inputHasAlpha: Boolean,
      val rgbOffsets: RGBOffsets) extends PixelHandler1[Byte] {
    type C = Int
    def promoter: NumberPromotionMax.Aux[Byte, Int] = PrimitiveNumberPromoters.BytePromotion

    /**
     * Naive version of a color to gray converter
     * Giving each color equal weight
     * And not excluding alpha channel
     */
    def calc(index: Int): Byte = {
      var accumulate: Int = 0
      for (i <- Range(0, inputNumBands))
        accumulate += data(index + i)
      val res: Int = accumulate / inputNumBands
      promoter.demote(res)
    }
  }

  class Color2GrayHandlerFloat(
      val data: Array[Float],
      val inputNumBands: Int,
      val inputHasAlpha: Boolean,
      val rgbOffsets: RGBOffsets) extends PixelHandler1[Float] {
    type C = Float
    def promoter: NumberPromotionMax.Aux[Float, Float] = PrimitiveNumberPromoters.FloatPromotion

    /**
     * Naive version of a color to gray converter
     * Giving each color equal weight
     * And not excluding alpha channel
     */
    def calc(index: Int): Float = {
      var accumulate: Float = 0
      for (i <- Range(0, inputNumBands))
        accumulate += data(index + i)
      accumulate / inputNumBands
    }
  }
}