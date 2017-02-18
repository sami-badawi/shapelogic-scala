package org.shapelogic.sc.color

import org.shapelogic.sc.imageutil.PixelArea

object ColorAreaFactory extends BaseAreaFactory {

  override def makePixelArea(x: Int, y: Int, startColor: Array[Byte]): IColorAndVariance = {
    val result = new ColorAndVariance(startColor.length)
    result.setPixelArea(new PixelArea(x, y))
    //XXX not sure if this could result in double counting
    result.putPixel(x, y, startColor)
    _store.append(result)
    result
  }
}
