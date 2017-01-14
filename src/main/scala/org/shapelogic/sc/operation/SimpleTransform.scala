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
case class SimpleTransform[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag : Numeric: Ordering,
  @specialized(Byte, Short, Int, Long, Float, Double) O: ClassTag : Numeric: Ordering](
   implicit promoter: NumberPromotionMax.Aux[T, O] ) {

}