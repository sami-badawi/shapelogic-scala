package org.shapelogic.sc.pixel.implement

import org.shapelogic.sc.image._
import org.shapelogic.sc.numeric.NumberPromotionMax
import org.shapelogic.sc.numeric.PrimitiveNumberPromoters
import scala.reflect.ClassTag
import spire.math.Numeric
import spire.math._
import spire.implicits._
import scala.reflect.runtime.universe._
import org.shapelogic.sc.pixel.PixelHandlerSame
import scala.Range

/**
 * Pixel Operation to be used with SobelOperation
 */
object SobelPixel {

  class SobelPixelG[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric](
      bufferImage: BufferImage[T])(
          val promoter: NumberPromotionMax.Aux[T, O]) extends PixelHandlerSame[T] {
    type C = O
    lazy val data = bufferImage.data
    lazy val inputNumBands = bufferImage.numBands
    lazy val inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha
    lazy val rgbOffsets = bufferImage.getRGBOffsetsDefaults

    lazy val alphaChannel = if (rgbOffsets.hasAlpha) rgbOffsets.alpha else -1
    lazy val inputNumBandsNoAlpha = if (inputHasAlpha) inputNumBands - 1 else inputNumBands
    /**
     * Naive version of a color to gray converter
     * Giving each color equal weight
     * And not excluding alpha channel
     */
    def calc(index: Int): T = {
      try {
        var accumulate: C = promoter.minValue
        for (i <- Range(0, inputNumBands))
          if (i != alphaChannel) {
            val left = promoter.promote(data(index + i - inputNumBands))
            val right = promoter.promote(data(index + i + inputNumBands))
            val differenct = abs(left - right)
            accumulate = max(accumulate, differenct)
          }
        val res: O = accumulate
        promoter.demote(res)
      } catch {
        case ex: Throwable => {
          print(".")
          promoter.minValueBuffer
        }
      }
    }
  }
}