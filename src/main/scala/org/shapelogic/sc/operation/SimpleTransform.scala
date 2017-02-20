package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation

import spire.math.Numeric
import spire.implicits._

import scala.specialized
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
 * Takes input image and create identical output image.
 * SimpleTransform has no knowledge of the internals of the numbers
 * It is just a runner
 * If it was not for demands by BufferImage all it needed was 
 * context bounds for:
 * ClassTag and the transform: T => T parameter
 */
class SimpleTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag](
    inputImage: BufferImage[T])(transform: T => T) {

  /**
   * Having this be a lazy init did not work
   */
  var outputImage: BufferImage[T] = null
  val verboseLogging = false

  lazy val inBuffer = inputImage.data
  lazy val rgbOffsets = inputImage.getRGBOffsetsDefaults
  lazy val alphaChannel = if (rgbOffsets.hasAlpha) rgbOffsets.alpha else -1
  lazy val outBuffer = outputImage.data
  lazy val inputNumBands = inputImage.numBands
  lazy val pixelOperation: PixelOperation[T] = new PixelOperation(inputImage)

  /**
   * Not taking alpha channel into account it should
   */
  def handleIndex(index: Int, indexOut: Int): Unit = {
    try {
      var i = 0
      do {
        if (i == alphaChannel)
          outBuffer(index + i) = inBuffer(index + i)
        else {
          outBuffer(index + i) = transform(inBuffer(index + i))
        }
        i += 1
      } while (i < inputNumBands)

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
  def calc(): BufferImage[T] = {
    outputImage = inputImage.empty()
    val pointCount = inputImage.width * inputImage.height
    pixelOperation.reset()
    var count = 0
    var index: Int = pixelOperation.index
    while (pixelOperation.hasNext) {
      index = pixelOperation.next()
      handleIndex(index, indexOut = index)
      count += 1
    }
    if (verboseLogging)
      println(s"count: $count, index: $index")
    outputImage
  }

  lazy val result: BufferImage[T] = calc()
}