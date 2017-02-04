package org.shapelogic.sc.imageutil

/**
 * To chain more PixelHandlers.<br />
 *
 * @author Sami Badawi
 */
trait HasPixelArea {
  def getPixelArea(): PixelArea
  def setPixelArea(pixelArea: PixelArea): Unit
}
