package org.shapelogic.sc.imageutil

import org.shapelogic.sc.polygon.BBox
import org.shapelogic.sc.polygon.CPointDouble
import org.shapelogic.sc.polygon.IPoint2D
import org.shapelogic.sc.polygon.CPointInt

/**
 * SegmentArea holds the information.
 * <br />
 *
 * @author Sami Badawi
 *
 */
class PixelArea(xIn: Int, yIn: Int) {
  var _aggregationPoint: CPointDouble = new CPointDouble(0, 0)

  var _startX: Int = xIn
  var _startY: Int = yIn

  lazy val _boundingBox: BBox = new BBox(new CPointInt(xIn, yIn))

  /** Number of pixels. */
  var _area: Int = 0

  /** Area is background, value null means not known. */
  var _background: Boolean = false

  /** If any line in the area has a line that was split. */
  var _gapInLine: Boolean = false

  def addPoint(x: Int, y: Int): Unit = {
    _boundingBox.addPoint(x, y)
    //    	_gapInLine = false
    if (_aggregationPoint == null)
      _aggregationPoint = new CPointDouble(x, y)
    else
      _aggregationPoint.setLocation(_aggregationPoint.x + x, _aggregationPoint.y + y)
    _area += 1
  }

  def getCenterPoint(): IPoint2D = {
    val result: IPoint2D = _aggregationPoint.copy().multiply(1.0 / _area)
    result
  }

  /** Number of pixels. */
  def getArea(): Int = {
    _area
  }

  /** If any line in the area has a line that was split. */
  def isGapInLine(): Boolean = {
    _gapInLine
  }

  def getBoundingBox(): BBox = {
    _boundingBox
  }

  def getBackground(): Boolean = {
    _background
  }

  def setBackground(background: Boolean): Unit = {
    _background = background
  }

  def putPixel(x: Int, y: Int, colors: Array[Byte]) = {
    addPoint(x, y)
  }

  def getStartX(): Int = {
    _startX
  }

  def getStartY(): Int = {
    _startY
  }

}