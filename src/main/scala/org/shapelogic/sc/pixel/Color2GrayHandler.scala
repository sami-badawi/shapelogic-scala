package org.shapelogic.sc.pixel

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.image.RGBOffsets
import org.shapelogic.sc.numeric.NumberPromotionMax
import org.shapelogic.sc.numeric.PrimitiveNumberPromoters
import scala.reflect.ClassTag
import spire.math.Numeric
import spire.math._
import spire.implicits._

import scala.reflect.runtime.universe._

/**
 *
 */
object Color2GrayHandler {
  // This was not enough to implicitly create
  // import PrimitiveNumberPromoters._

  class Color2GrayHandlerG[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric: Ordering](
      val data: Array[T],
      val inputNumBands: Int,
      val inputHasAlpha: Boolean,
      val rgbOffsets: RGBOffsets)(
          val promoter: NumberPromotionMax.Aux[T, O]) extends PixelHandlerSame[T] {
    type C = O
    //    def promoter: NumberPromotionMax.Aux[T, O] = PrimitiveNumberPromoters.BytePromotion

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
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.BytePromotion)

  implicit class Color2GrayHandlerShort(bufferImage: BufferImage[Short]) extends Color2GrayHandlerG[Short, Int](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.ShortPromotion)

  implicit class Color2GrayHandlerInt(bufferImage: BufferImage[Int]) extends Color2GrayHandlerG[Int, Int](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.IntPromotion)

  implicit class Color2GrayHandlerFloat(bufferImage: BufferImage[Float]) extends Color2GrayHandlerG[Float, Float](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.FloatPromotion)

  implicit class Color2GrayHandlerDouble(bufferImage: BufferImage[Double]) extends Color2GrayHandlerG[Double, Double](
    data = bufferImage.data,
    inputNumBands = bufferImage.numBands,
    inputHasAlpha = bufferImage.getRGBOffsetsDefaults.hasAlpha,
    rgbOffsets = bufferImage.getRGBOffsetsDefaults)(PrimitiveNumberPromoters.DoublePromotion)

  // =========================== Many argument less generic ===========================

  class Color2GrayHandlerByteM(
      val data: Array[Byte],
      val inputNumBands: Int,
      val inputHasAlpha: Boolean,
      val rgbOffsets: RGBOffsets) extends PixelHandlerSame[Byte] {
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

  class Color2GrayHandlerFloatM(
      val data: Array[Float],
      val inputNumBands: Int,
      val inputHasAlpha: Boolean,
      val rgbOffsets: RGBOffsets) extends PixelHandlerSame[Float] {
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