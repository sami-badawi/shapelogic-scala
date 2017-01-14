package org.shapelogic.sc.operation

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelOperation

import spire.math.Numeric
import spire.implicits._

import scala.specialized
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import org.shapelogic.sc.numeric.NumberPromotionMax
import org.shapelogic.sc.pixel.IndexColorPixel

/**
 * Takes input image and create identical output image.
 *
 */
abstract class SimpleTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag: Numeric: Ordering, @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag: Numeric: Ordering](
    inputImage: BufferImage[T])(implicit promoter: NumberPromotionMax.Aux[T, O]) {

  val verboseLogging = false

  lazy val outputImage = inputImage.empty()
  lazy val inBuffer = inputImage.data
  lazy val outBuffer = outputImage.data
  lazy val inputNumBands = inputImage.numBands
  lazy val indexColorPixel: IndexColorPixel[T] = IndexColorPixel.apply(inputImage)
  lazy val pixelOperation: PixelOperation[T] = new PixelOperation(inputImage)

  /**
   * This easily get very inefficient
   */
  def handleIndex(index: Int, indexOut: Int): Unit = {
    try {
      val oneChannel = promoter.promote(indexColorPixel.getRed(index))
    } catch {
      case ex: Throwable => {
        println(ex.getMessage)
      }
    }
  }
  
  def transform(input: T): T

  /**
   * Run over input and output
   * Should I do by line?
   */
  def calc(): BufferImage[T] = {
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
      println(s"low count: index: $index, indexOut: $indexOut")
    outputImage
  }

  lazy val result: BufferImage[T] = calc()
}