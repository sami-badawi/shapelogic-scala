//package org.shapelogic.sc.pixel
//
//import scala.reflect.ClassTag
//import org.shapelogic.sc.image.RGBOffsets
//import org.shapelogic.sc.numeric.NumberPromotion
//import org.shapelogic.sc.image.BufferImage
//
//class PixelHandlerSwap[@specialized(Byte, Short, Int, Long, Float, Double) I: ClassTag :Ordering](inputImage: BufferImage[I],
//    swap: Seq[Int]) extends PixelHandler[I] {
//
//  type C = I
//
//  lazy val data: Array[I] = inputImage.data
//
//  /**
//   * numBands for input
//   */
//  lazy val inputNumBands: Int = inputImage.numBands
//
//  /**
//   * First output will get alpha if input does
//   */
//  lazy val inputHasAlpha: Boolean = inputImage.getRGBOffsetsDefaults.hasAlpha
//
//  lazy val rgbOffsets: RGBOffsets = inputImage.rgbOffsets
//
//  lazy val promoter: NumberPromotion[I, I] = new NumberPromotion[I, I]() {
//    def demote(input: I): I = input
//    def promote(input: I): I = input
//    def calc(indexIn: Int, channelOut: Int): I = {
//      val inputChannel = swap(channelOut)
//      data(indexIn + inputChannel)
//    }
//  }
//}
