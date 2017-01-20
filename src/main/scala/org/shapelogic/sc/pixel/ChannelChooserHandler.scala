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
 * Chose one color channel 
 */
object ChannelChoserHandler {
  // This was not enough to implicitly create
  // import PrimitiveNumberPromoters._

  class ChannelChoserHandlerG[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric: Ordering](
      bufferImage: BufferImage[T], channelNumber: Int)(
          val promoter: NumberPromotionMax.Aux[T, O]) extends PixelHandler1[T] {
    type C = O
    //    def promoter: NumberPromotionMax.Aux[T, O] = PrimitiveNumberPromoters.BytePromotion

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
        data(index + channelNumber)
      } catch {
        case ex: Throwable => {
          print(".")
          promoter.minValueBuffer
        }
      }
    }
  }

  class ChannelChoserHandlerByte(bufferImage: BufferImage[Byte], channelNumber: Int) extends ChannelChoserHandlerG[Byte, Int](
    bufferImage, channelNumber)(PrimitiveNumberPromoters.BytePromotion)

  class ChannelChoserHandlerShort(bufferImage: BufferImage[Short], channelNumber: Int) extends ChannelChoserHandlerG[Short, Int](
    bufferImage, channelNumber)(PrimitiveNumberPromoters.ShortPromotion)

  class ChannelChoserHandlerInt(bufferImage: BufferImage[Int], channelNumber: Int) extends ChannelChoserHandlerG[Int, Int](
    bufferImage, channelNumber)(PrimitiveNumberPromoters.IntPromotion)

  class ChannelChoserHandlerFloat(bufferImage: BufferImage[Float], channelNumber: Int) extends ChannelChoserHandlerG[Float, Float](
    bufferImage, channelNumber)(PrimitiveNumberPromoters.FloatPromotion)

  class ChannelChoserHandlerDouble(bufferImage: BufferImage[Double], channelNumber: Int) extends ChannelChoserHandlerG[Double, Double](
    bufferImage, channelNumber)(PrimitiveNumberPromoters.DoublePromotion)

}