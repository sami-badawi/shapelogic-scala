package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage

/**
 * Should take an image and a value
 * Return gray scale image with 2 values 0 and 255
 */
class ThresholdOperation[T](inputImage: BufferImage[T], threshold: Double) {

  lazy val outputImage = new BufferImage[Byte](
    width = inputImage.width,
    height = inputImage.height,
    numBands = 1,
    bufferInput = null,
    rgbOffsetsOpt = None)

  /**
   * Run over input and output
   * Should I do by line?
   */
  def calc(): BufferImage[Byte] = {
    val buffer = outputImage.data

  }

  lazy val result: BufferImage[Byte] = calc()
}