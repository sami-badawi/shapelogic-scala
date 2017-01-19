package org.shapelogic.sc.operation

import spire.math.Numeric
import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.numeric._
import org.shapelogic.sc.pixel._
import spire.math._
import spire.implicits._
import scala.reflect.ClassTag

/**
 * Should take an image and a value
 * 
 * Many input channels one output channel possibly an alpha output channel
 * This has knowledge of the internals of the numbers
 * 
 * Return gray scale image with 2 values 0 and 255
 */
sealed class ThresholdOperation[
  @specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering,
  @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T],
    threshold: O)(implicit promoter: NumberPromotion.Aux[T, O]) {

  lazy val verboseLogging: Boolean = true

  /**
   * This is no generic
   */
  def resToInt(input: promoter.Out): Int = {
    input match {
      case intVal: Int => intVal
      case byteVal: Byte => byteVal.toInt // & NumberPromotion.byteMask
      case _ => 0
    }
  }

  lazy val outputImage = new BufferImage[Byte](
    width = inputImage.width,
    height = inputImage.height,
    numBands = 1,
    bufferInput = null,
    rgbOffsetsOpt = None)

  lazy val inBuffer = inputImage.data
  lazy val outBuffer = outputImage.data
  lazy val inputNumBands = inputImage.numBands
  lazy val indexColorPixel: IndexColorPixel[T] = IndexColorPixel.apply(inputImage)
  lazy val pixelOperation: PixelOperation[T] = new PixelOperation(inputImage)

  var low = 0
  var high = 0

  val lowValue: Byte = 0
  val highValue: Byte = -1 // 255

  /**
   * This easily get very inefficient
   */
  def handleIndex(index: Int, indexOut: Int): Unit = {
    try {
      val oneChannel = promoter.promote(indexColorPixel.getRed(index))
      if (threshold < resToInt(oneChannel)) { //Problem with sign 
        high += 1
        outBuffer(indexOut) = highValue
      } else {
        low += 1
        outBuffer(indexOut) = lowValue
      }
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
      }
    }
  }

  /**
   * Run over input and output
   * Should I do by line?
   */
  def calc(): BufferImage[Byte] = {
    val pointCount = inputImage.width * inputImage.height
    pixelOperation.reset()
    var indexOut: Int = -1
    var index: Int = pixelOperation.index
    while (pixelOperation.hasNext) {
      index = pixelOperation.next()
      indexOut += 1
      handleIndex(index, indexOut)
    }
    if (verboseLogging)
      println(s"low count: $low, high: $high, index: $index, indexOut: $indexOut")
    outputImage
  }

  lazy val result: BufferImage[Byte] = calc()
}