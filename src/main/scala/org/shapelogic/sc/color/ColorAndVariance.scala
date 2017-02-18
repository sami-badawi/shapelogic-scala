package org.shapelogic.sc.color

import org.shapelogic.sc.imageutil.PixelArea
import org.shapelogic.sc.mathematics.StorelessDiscriptiveStatistic
import spire.implicits._
import org.shapelogic.sc.color.ColorUtil._

/**
 * GrayAndVariance describes an average color with variance for gray 8 bit.
 * <br />
 * Used for color clustering, and particle counter.<br />
 *
 * XXX this class should be renamed to ColorArea
 *
 * @author Sami Badawi
 *
 */
class ColorAndVariance(numBands: Int) extends IColorAndVariance {
  var _pixelArea: PixelArea = null
  val _colorStatistics: Array[StorelessDiscriptiveStatistic] = new Array[StorelessDiscriptiveStatistic](numBands)
  cfor(0)(_ < numBands, _ + 1) { i =>
    _colorStatistics(i) = new StorelessDiscriptiveStatistic()
  }

  val _splitColors: Array[Int] = new Array[Int](numBands)

  /**
   * Add the color for a given point (x,y). <br />
   *
   * The points needs to be added in sequence.
   */
  override def putPixel(x: Int, y: Int, color: Array[Byte]): Unit = {
    if (_pixelArea != null)
      _pixelArea.addPoint(x, y)
    cfor(0)(_ < numBands, _ + 1) { i =>
      _colorStatistics(i).increment(color(i))
    }
  }

  override def getArea(): Int = {
    _colorStatistics(0).getCount()
  }

  override def getStandardDeviation(): Double = {
    _colorStatistics(0).getStandardDeviation()
  }

  override def merge(colorAndVariance: IColorAndVariance): Unit = {
    if (!(colorAndVariance.isInstanceOf[ColorAndVariance]))
      return
    cfor(0)(_ < numBands, _ + 1) { i =>
      _colorStatistics(i).merge((colorAndVariance.asInstanceOf[ColorAndVariance])._colorStatistics(i))
    }
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

  def getMeanRed(): Int = {
    _colorStatistics(ColorUtil.RED_POS).getMean().toInt
  }

  def getMeanGreen(): Int = {
    _colorStatistics(ColorUtil.GREEN_POS).getMean().toInt
  }

  def getMeanBlue(): Int = {
    _colorStatistics(ColorUtil.BLUE_POS).getMean().toInt
  }

  def getMeanColor(): Array[Int] = {
    Array(
      _colorStatistics(ColorUtil.RED_POS).getMean().toInt,
      _colorStatistics(ColorUtil.GREEN_POS).getMean().toInt,
      _colorStatistics(ColorUtil.BLUE_POS).getMean().toInt)
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
