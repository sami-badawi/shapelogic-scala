package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import spire.math._
import spire.implicits._
import scala.reflect.ClassTag

/**
 * Should take an image and a value
 * Return gray scale image with 2 values 0 and 255
 */
class ThresholdOperation[T: Numeric: ClassTag](inputImage: BufferImage[T], threshold: Double) {
  val verboseLogging = false

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

  /**
   * This easily get very inefficient
   */
  def handleIndex(index: Int, indexOut: Int): Unit = {
    try {
      //    val oneChannel = indexColorPixel.getRed(index)
      val oneChannel = indexColorPixel.getRed(index)
      if (threshold < oneChannel) { //Problem with sign 
        high += 1
        outBuffer(indexOut) = 127
      } else {
        low += 1
        outBuffer(indexOut) = 0
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
    if (verboseLogging)
      println(s"Start index: $index, indexOut: $indexOut")
    while (pixelOperation.hasNext) {
      index = pixelOperation.next()
      indexOut += 1
      if (verboseLogging)
        println(s"index: $index, indexOut: $indexOut")
      handleIndex(index, indexOut)
    }
    if (verboseLogging)
      println(s"low count: $low, high: $high, index: $index, indexOut: $indexOut")
    outputImage
  }

  lazy val result: BufferImage[Byte] = calc()
}