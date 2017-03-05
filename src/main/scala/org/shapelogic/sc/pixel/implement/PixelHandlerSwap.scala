package org.shapelogic.sc.pixel.implement

import scala.reflect.ClassTag
import org.shapelogic.sc.numeric.NumberPromotion
import org.shapelogic.sc.image._
import org.shapelogic.sc.pixel.PixelHandler

class PixelHandlerSwap[@specialized(Byte, Short, Int, Long, Float, Double) I: ClassTag](inputImage: BufferImage[I],
    swap: Seq[Int]) extends PixelHandler[I, I] {

  //  type C = I

  lazy val data: Array[I] = inputImage.data

  /**
   * numBands for input
   */
  lazy val inputNumBands: Int = inputImage.numBands

  /**
   * First output will get alpha if input does
   */
  lazy val inputHasAlpha: Boolean = inputImage.getRGBOffsetsDefaults.hasAlpha

  lazy val rgbOffsets: RGBOffsets = inputImage.getRGBOffsetsDefaults

  lazy val promoter: NumberPromotion.Aux[I, I] = new NumberPromotion[I] {
    type Out = I
    def demote(input: Out): I = input
    def promote(input: I): Out = input
    def parseCalc(text: String): Out = ???
    def maxValue: I = ???
    def minValue: I = ???
  }

  def info(): String = {
    s"PixelHandlerSwap"
  }

  def calc(indexIn: Int, channelOut: Int): I = {
    val inputChannel = swap(channelOut)
    data(indexIn + inputChannel)
  }
}
