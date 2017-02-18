package org.shapelogic.sc.old

import org.shapelogic.sc.imageutil.PixelArea
import org.shapelogic.sc.mathematics.StorelessDiscriptiveStatistic
import org.shapelogic.sc.imageutil.PixelArea
import org.shapelogic.sc.color.IColorAndVariance

/**
 * GrayAndVariance describes an average color with variance for gray 8 bit.
 * <br />
 * Used for color clustering, and particle counter.<br />
 *
 * @author Sami Badawi
 *
 */
class GrayAndVariance extends IColorAndVariance[Int] {

  var _pixelArea: PixelArea = null

  protected val _grayStatistic = new StorelessDiscriptiveStatistic()

  /** Not sure if these are needed. */
  protected var _minColor = 256
  protected var _maxColor = 0

  /** Distance from colorCenter that will be accepted in this Range. */
  protected var _maxDistance: Double = 0

  /**
   * Add the color for a given point (x,y). <br />
   *
   * The points needs to be added in sequence.
   */
  override def putPixel(x: Int, y: Int, colors: Array[Int]): Unit = {
    val color = colors(0).toInt & 0xff //XXX fix sign
    if (_pixelArea != null)
      _pixelArea.addPoint(x, y)
    else
      _pixelArea = new PixelArea(x, y)
    _grayStatistic.increment(color)
    if (_maxColor < color)
      _maxColor = color
    if (color < _minColor)
      _minColor = color
  }

  override def getArea(): Int = {
    _grayStatistic.getCount()
  }

  override def getMeanColor(): Array[Int] = {
    Array(_grayStatistic.getMean().toInt)
  }

  override def getStandardDeviation(): Double = {
    _grayStatistic.getStandardDeviation()
  }

  override def merge(colorAndVariance: IColorAndVariance[Int]): Unit = {
    if (!(colorAndVariance.isInstanceOf[GrayAndVariance]))
      return
    val grayRange: GrayAndVariance = colorAndVariance.asInstanceOf[GrayAndVariance]
    _grayStatistic.merge(grayRange._grayStatistic)
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
    getMeanColor()
  }

  def getMeanGray(): Int = {
    _grayStatistic.getMean().toInt
  }

  override def getMeanRed(): Int = {
    _grayStatistic.getMean().toInt
  }

  override def getMeanGreen(): Int = {
    _grayStatistic.getMean().toInt
  }

  override def getMeanBlue(): Int = {
    _grayStatistic.getMean().toInt
  }

  override def getPixelArea(): PixelArea = {
    _pixelArea
  }

  override def setPixelArea(pixelArea: PixelArea): Unit = {
    _pixelArea = pixelArea
  }

  override def toString(): String = {
    getColorChannels().mkString(", ")
  }
}
