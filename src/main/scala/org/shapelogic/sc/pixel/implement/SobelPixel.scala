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
 *
 */
object SobelPixel {
  // This was not enough to implicitly create
  // import PrimitiveNumberPromoters._

  class SobelPixelG[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric](
      val data: Array[T],
      val inputNumBands: Int,
      val inputHasAlpha: Boolean,
      val rgbOffsets: RGBOffsets)(
          val promoter: NumberPromotionMax.Aux[T, O]) extends PixelHandlerSame[T] {
    type C = O

    def this(bufferImage: BufferImage[T])(
      promoterIn: NumberPromotionMax.Aux[T, O]) = {
      this(
        data = bufferImage.data,
        inputNumBands = bufferImage.numBands,
        inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
        rgbOffsets = bufferImage.getRGBOffsetsDefaults)(promoterIn)
    }

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

  class SobelPixelByte(bufferImage: BufferImage[Byte]) extends SobelPixelG[Byte, Int](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.BytePromotion)

  implicit class SobelPixelShort(bufferImage: BufferImage[Short]) extends SobelPixelG[Short, Int](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.ShortPromotion)

  implicit class SobelPixelInt(bufferImage: BufferImage[Int]) extends SobelPixelG[Int, Int](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.IntPromotion)

  implicit class SobelPixelFloat(bufferImage: BufferImage[Float]) extends SobelPixelG[Float, Float](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.FloatPromotion)

  implicit class SobelPixelDouble(bufferImage: BufferImage[Double]) extends SobelPixelG[Double, Double](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.DoublePromotion)

}