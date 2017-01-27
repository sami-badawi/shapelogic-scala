package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.ChannelChoserHandler._
import spire.math.Numeric
import spire.math.Numeric._
import spire.math.Integral
import spire.algebra._
import spire.math._
import spire.implicits._

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.util.Try

object ChannelChoserOperation {
  class ChannelChoserOperationByte(inputImage: BufferImage[Byte], channelNumber: Int) extends BaseOperation[Byte, Int](inputImage)(new ChannelChoserHandlerByte(inputImage, channelNumber)) {
  }

  class ChannelChoserOperationShort(inputImage: BufferImage[Short], channelNumber: Int) extends BaseOperation[Short, Int](inputImage)(new ChannelChoserHandlerShort(inputImage, channelNumber)) {
  }

  class ChannelChoserOperationInt(inputImage: BufferImage[Int], channelNumber: Int) extends BaseOperation[Int, Int](inputImage)(new ChannelChoserHandlerInt(inputImage, channelNumber)) {
  }

  class ChannelChoserOperationFloat(inputImage: BufferImage[Float], channelNumber: Int) extends BaseOperation[Float, Float](inputImage)(new ChannelChoserHandlerFloat(inputImage, channelNumber)) {
  }

  class ChannelChoserOperationDouble(inputImage: BufferImage[Double], channelNumber: Int) extends BaseOperation[Double, Double](inputImage)(new ChannelChoserHandlerDouble(inputImage, channelNumber)) {
  }

  import org.shapelogic.sc.numeric.PrimitiveNumberPromoters.NormalPrimitiveNumberPromotionImplicits._

  def makeByteTransform(inputImage: BufferImage[Byte], parameter: String): BufferImage[Byte] = {
    val channelNumber: Int = Try(parameter.trim().toInt).getOrElse(1)
    val thresholdOperation = new ChannelChoserOperationByte(inputImage, channelNumber)
    thresholdOperation.result
  }
}