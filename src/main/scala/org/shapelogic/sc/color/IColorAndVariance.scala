package org.shapelogic.sc.color

import org.shapelogic.sc.imageutil.PixelHandler
import org.shapelogic.sc.imageutil.HasArea
import org.shapelogic.sc.imageutil.HasPixelArea

/**
 *  Ported from ShapeLogic Java
 *  XXX Need changes
 *
 * ColorAndVarianceI is a color aggregate with a mean color and a standard deviation.<br />
 *
 * ColorAndVarianceI is an interface for a color / gray implementations.
 *
 * @param [T] calculated color for a Byte image that would be Int
 *
 * @author Sami Badawi
 *
 */
trait IColorAndVariance[T] extends PixelHandler[T] with ColorChannels[T] with HasArea with HasPixelArea {

  /** */
  def merge(colorAndVariance: IColorAndVariance[T]): Unit

  def getStandardDeviation(): Double

  def getMeanColor(): Array[T]

  def getMeanRed(): T

  def getMeanGreen(): T

  def getMeanBlue(): T
}
