package org.shapelogic.sc.color

import org.shapelogic.sc.imageutil.PixelArea
import org.shapelogic.sc.mathematics.StorelessDiscriptiveStatistic
import org.shapelogic.sc.imageutil.PixelArea

/**
 * GrayAndVariance describes an average color with variance for gray 8 bit.
 * <br />
 * Used for color clustering, and particle counter.<br />
 *
 * @author Sami Badawi
 *
 */
class GrayAndVariance extends IColorAndVariance {

  var _pixelArea: PixelArea = null

  protected val _grayStatistic = new StorelessDiscriptiveStatistic();

  /** Not sure if these are needed. */
  protected var _minColor = 256;
  protected var _maxColor = 0;

  /** Distance from colorCenter that will be accepted in this Range. */
  protected var _maxDistance: Double = 0

  /**
   * Add the color for a given point (x,y). <br />
   *
   * The points needs to be added in sequence.
   */
  override def putPixel(x: Int, y: Int, color: Int): Unit = {
    if (_pixelArea != null)
      _pixelArea.addPoint(x, y)
    else
      _pixelArea = new PixelArea(x, y)
    _grayStatistic.increment(color);
    if (_maxColor < color)
      _maxColor = color;
    if (color < _minColor)
      _minColor = color;
  }

  override def getArea(): Int = {
    return _grayStatistic.getCount();
  }

  override def getMeanColor(): Int = {
    _grayStatistic.getMean().toInt
  }

  override def getStandardDeviation(): Double = {
    return _grayStatistic.getStandardDeviation()
  }

  override def merge(colorAndVariance: IColorAndVariance): Unit = {
    if (!(colorAndVariance.isInstanceOf[GrayAndVariance]))
      return ;
    val grayRange: GrayAndVariance = colorAndVariance.asInstanceOf[GrayAndVariance]
    _grayStatistic.merge(grayRange._grayStatistic);
  }

  /**
   * Color vector to be used for color distance.<br />
   *
   * Should I use the center color or the mean color?<br />
   *
   * I will start by using the mean color.<br />
   *
   * I think that I will start without including the standard deviation.
   */
  override def getColorChannels(): Array[Int] = {
    Array[Int](getMeanColor())
  }

  def getMeanGray(): Int = {
    return _grayStatistic.getMean().toInt
  }

  override def getMeanRed(): Int = {
    return _grayStatistic.getMean().toInt
  }

  override def getMeanGreen(): Int = {
    return _grayStatistic.getMean().toInt
  }

  override def getMeanBlue(): Int = {
    return _grayStatistic.getMean().toInt
  }

  override def getPixelArea(): PixelArea = {
    return _pixelArea;
  }

  override def setPixelArea(pixelArea: PixelArea): Unit = {
    _pixelArea = pixelArea;
  }

  override def toString(): String = {
    getColorChannels().mkString(", ")
  }
}
