package org.shapelogic.sc.imageutil

/**
 * Interface for anything that can handle an isolated pixel.<br />
 *
 * @author Sami Badawi
 */
trait PixelHandler {

  /** Handle a pixel with a color and a coordinate. */
  def putPixel(x: Int, y: Int, colors: Array[Byte]): Unit

}
