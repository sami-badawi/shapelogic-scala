package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants.DOWN
import org.shapelogic.sc.util.Constants.LEFT
import org.shapelogic.sc.util.Constants.RIGHT
import org.shapelogic.sc.util.Constants.UP

import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.polygon.Polygon
import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.image.BufferImage

import spire.implicits._
import spire.math._
import org.shapelogic.sc.color.IColorDistanceWithImage
import org.shapelogic.sc.pixel.PixelDistance
import org.shapelogic.sc.numeric.PrimitiveNumberPromotersAux
import scala.util.Try
import org.shapelogic.sc.image.BufferBooleanImage
import scala.reflect.ClassTag
import org.shapelogic.sc.numeric.NumberPromotion

/**
 * PixelFollow is based on EdgeTracer
 *
 * It is meant to be the base class for vectorize, edge trace,
 * binary operation, segmentation
 *
 * @author Sami Badawi
 *
 */
abstract class PixelFollow[T: ClassTag, C: ClassTag: Numeric: Ordering](
    image: BufferImage[T],
    maxDistance: C,
    similarIsMatch: Boolean)(implicit promoterIn: NumberPromotion.Aux[T, C])
    extends PixelFollowSimilarity[T](image, similarIsMatch) {

  // =============== lazy init ===============

  lazy val pixelDistance = new PixelDistance[T, C](image, maxDistance, similarIsMatch)(
    implicitly[ClassTag[T]],
    implicitly[ClassTag[C]],
    implicitly[Numeric[C]],
    implicitly[Ordering[C]],
    promoterIn)

  // =============== util for abstract ===============

  /**
   * Set reference color to the color of a point
   */
  def takeColorFromPoint(x: Int, y: Int): Array[T] = {
    pixelDistance.takeColorFromPoint(x, y)
  }

  /**
   * Set reference color directly in Byte
   */
  def setReferencePointArray(iArray: Array[T]): Unit = {
    pixelDistance.setReferencePointArray(iArray)
  }

  // =============== util ===============


}
