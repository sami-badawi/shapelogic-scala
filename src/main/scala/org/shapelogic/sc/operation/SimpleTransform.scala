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
class SimpleTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag : Numeric: Ordering,
  @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag : Numeric: Ordering](
    inputImage: BufferImage[T])(implicit promoter: NumberPromotionMax.Aux[T, O] ) {
  
  lazy val outputImage = inputImage.empty()
  lazy val inBuffer = inputImage.data
  lazy val outBuffer = outputImage.data
  lazy val inputNumBands = inputImage.numBands
  lazy val indexColorPixel: IndexColorPixel[T] = IndexColorPixel.apply(inputImage)
  lazy val pixelOperation: PixelOperation[T] = new PixelOperation(inputImage)

}