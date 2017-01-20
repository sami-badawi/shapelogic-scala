package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.numeric.NumberPromotionMax
import org.shapelogic.sc.numeric.PrimitiveNumberPromoters
import scala.reflect.ClassTag
import spire.math.Numeric
import spire.math.Integral
import spire.implicits._

import scala.reflect.runtime.universe._

object Color2GrayHandler {

  class Color2GrayHandlerG[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric: Ordering](
      val data: Array[T],
      val inputNumBands: Int,
      val inputHasAlpha: Boolean,
      val rgbOffsets: RGBOffsets)(
          implicit val promoter: NumberPromotionMax.Aux[T, O] ) extends PixelHandler1[T] {
    type C = O
//    def promoter: NumberPromotionMax.Aux[T, O] = PrimitiveNumberPromoters.BytePromotion

    /**
     * Naive version of a color to gray converter
     * Giving each color equal weight
     * And not excluding alpha channel
     */
    def calc(index: Int): T = {
      var accumulate: C = promoter.minValue
      for (i <- Range(0, inputNumBands))
        accumulate += promoter.promote(data(index + i))
      val res: O = accumulate / inputNumBands
      promoter.demote(res)
    }
  }

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