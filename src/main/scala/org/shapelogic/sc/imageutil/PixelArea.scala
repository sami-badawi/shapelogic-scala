package org.shapelogic.sc.imageutil

import org.shapelogic.sc.polygon.BBox
import org.shapelogic.sc.polygon.CPointDouble
import org.shapelogic.sc.polygon.IPoint2D

/**
 * SegmentArea holds the information.
 * <br />
 *
 * @author Sami Badawi
 *
 */
class PixelArea(xIn: Int, yIn: Int) {
  var _aggregationPoint: CPointDouble = null

  var _startX: Int = xIn
  var _startY: Int = yIn

  var _boundingBox: BBox = null

  /** Number of pixels. */
  var _area: Int = 0

  /** Area is background, value null means not known. */
  var _background: Boolean = false

  /** If any line in the area has a line that was split. */
  var _gapInLine: Boolean = false

  def init(x: Int, y: Int) = {
    _startX = x;
    _startY = y;
    _area = 0;
    _boundingBox = new BBox();
    _gapInLine = false;
    _aggregationPoint = new CPointDouble(0, 0);
    //    	addPoint(x, y); //Went it gets created it should not add the first point
  }

  def addPoint(x: Int, y: Int): Unit = {
    if (_boundingBox == null)
      _boundingBox = new BBox()
    _boundingBox.addPoint(x, y);
    //    	_gapInLine = false;
    if (_aggregationPoint == null)
      _aggregationPoint = new CPointDouble(x, y)
    else
      _aggregationPoint.setLocation(_aggregationPoint.x + x, _aggregationPoint.y + y);
    _area += 1
  }

  def getCenterPoint(): IPoint2D = {
    val result: IPoint2D = _aggregationPoint.copy().multiply(1.0 / _area);
    return result;
  }

  /** Number of pixels. */
  def getArea(): Int = {
    return _area;
  }

  /** If any line in the area has a line that was split. */
  def isGapInLine(): Boolean = {
    return _gapInLine;
  }

  def getBoundingBox(): BBox = {
    return _boundingBox;
  }

  def getBackground(): Boolean = {
    return _background;
  }

  def setBackground(background: Boolean): Unit = {
    _background = background;
  }

  def putPixel(x: Int, y: Int, colors: Array[Byte]) = {
    addPoint(x, y);
  }

  def getStartX(): Int = {
    return _startX;
  }

  def getStartY(): Int = {
    return _startY;
  }

}