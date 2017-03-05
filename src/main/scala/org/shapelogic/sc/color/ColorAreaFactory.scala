package org.shapelogic.sc.color

import org.shapelogic.sc.imageutil.PixelArea
import scala.reflect.ClassTag
import spire.implicits._
import spire.math._

/**
 * @param [T] calculated color for a Byte image that would be Int
 *
 */
class ColorAreaFactory[T: ClassTag: Numeric] extends BaseAreaFactory[T] {

  override def makePixelArea(x: Int, y: Int, startColor: Array[T]): IColorAndVariance[T] = {
    val result = new ColorAndVariance[T](startColor.length)
    result.setPixelArea(new PixelArea(x, y))
    //XXX not sure if this could result in double counting
    result.putPixel(x, y, startColor)
    _store.append(result)
    result
  }
}
