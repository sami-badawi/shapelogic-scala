package org.shapelogic.sc.pixel.implement

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.numeric.NumberPromotion
import org.shapelogic.sc.numeric.PrimitiveNumberPromoters
import scala.reflect.ClassTag
import spire.math.Numeric
import spire.math._
import spire.implicits._
import scala.reflect.runtime.universe._
import org.shapelogic.sc.pixel.PixelHandlerSame
import scala.Range
import org.shapelogic.sc.image._

/**
 *
 */
object Color2GrayHandler {
  // This was not enough to implicitly create
  // import PrimitiveNumberPromoters._

  class Color2GrayHandlerG[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric](
      inputImage: BufferImage[T])(
          val promoter: NumberPromotion.Aux[T, O]) extends PixelHandlerSame[T] {
    type C = O
    //    def promoter: NumberPromotion.Aux[T, O] = PrimitiveNumberPromoters.BytePromotion

    lazy val data: Array[T] = inputImage.data

    /**
     * numBands for input
     */
    lazy val inputNumBands: Int = inputImage.numBands

    /**
     * First output will get alpha if input does
     */
    lazy val inputHasAlpha: Boolean = inputImage.getRGBOffsetsDefaults.hasAlpha

    lazy val rgbOffsets: RGBOffsets = inputImage.getRGBOffsetsDefaults

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
          if (i != alphaChannel)
            accumulate += promoter.promote(data(index + i))
        val res: O = accumulate / inputNumBandsNoAlpha
        promoter.demote(res)
      } catch {
        case ex: Throwable => {
          print(".")
          promoter.minValueBuffer
        }
      }
    }
  }

  class Color2GrayHandlerByte(bufferImage: BufferImage[Byte]) extends Color2GrayHandlerG[Byte, Int](
      bufferImage
    )(PrimitiveNumberPromoters.BytePromotion)

}