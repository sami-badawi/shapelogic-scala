package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import spire.math._
import spire.implicits._
import scala.reflect.ClassTag

/**
 * Should take an image and a value
 * Return gray scale image with 2 values 0 and 255
 */
class ThresholdOperation[T :Numeric :ClassTag](inputImage: BufferImage[T], threshold: Double) {

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

  /**
   * This easily get very inefficient
   */
  def handleIndex(index: Int): Unit = {
    val oneChannel = indexColorPixel.getRed(index)
    if (threshold < oneChannel) { //Problem with sign
      outBuffer(index) = 127
    } else {
      outBuffer(index) = 0
    }
  }

  /**
   * Run over input and output
   * Should I do by line?
   */
  def calc(): BufferImage[Byte] = {
    val pointCount = inputImage.width * inputImage.height
    var i: Int = 0
    while (i < pointCount) {
      handleIndex(i)
      i += 1
    }
    outputImage
  }

  lazy val result: BufferImage[Byte] = calc()
}