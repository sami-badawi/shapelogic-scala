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
 * @author Sami Badawi
 *
 */
trait IColorAndVariance extends PixelHandler with ColorChannels with HasArea with HasPixelArea
{

  /** */
  def merge(colorAndVariance: IColorAndVariance): IColorAndVariance

  def getStandardDeviation(): Double

  def getMeanColor(): Int

  def getMeanRed(): Int

  def getMeanGreen(): Int

  def getMeanBlue(): Int
}
