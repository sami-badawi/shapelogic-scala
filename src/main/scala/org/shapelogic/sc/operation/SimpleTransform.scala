package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation

import spire.math.Numeric
import spire.implicits._

import scala.specialized
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.numeric.NumberPromotionMax

/**
 * Takes input image and create identical output image.
 *
 */
class SimpleTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T])(transform: T => T) {

  val verboseLogging = false

  lazy val inBuffer = inputImage.data
  lazy val outputImage = inputImage.empty()
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
    val pointCount = inputImage.width * inputImage.height
    pixelOperation.reset()
    var index: Int = pixelOperation.index
    while (pixelOperation.hasNext) {
      index = pixelOperation.next()
      handleIndex(index, index)
    }
    if (verboseLogging)
      println(s"low count: index: $index")
    outputImage
  }

  lazy val result: BufferImage[T] = calc()
}