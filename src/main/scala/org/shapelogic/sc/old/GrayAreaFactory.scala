package org.shapelogic.sc.color

import org.shapelogic.sc.imageutil.PixelArea
import org.shapelogic.sc.old.GrayAndVariance

/**
 * GrayAreaFactory is a factory and store for GrayEdgeArea.
 * <br />
 *
 * @author Sami Badawi
 *
 */
object GrayAreaFactory extends BaseAreaFactory[Int] {

  override def makePixelArea(x: Int, y: Int, startColor: Array[Int]): IColorAndVariance[Int] = {
    val result = new GrayAndVariance()
    result.setPixelArea(new PixelArea(x, y))
    //XXX not sure if this could result in double counting
    result.putPixel(x, y, startColor)
    _store.append(result)
    result
  }
}
