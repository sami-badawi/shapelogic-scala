package org.shapelogic.sc.image

import simulacrum._

/**
 * Most basic image
 * I could possibly take out the read operations
 * First version of real classes
 * Might be changed to be a typeclass in Cats later
 */
@typeclass trait ReadImage[@specialized T] extends Any with Serializable {

  def width: Int

  def height: Int

  def numBands: Int

  /**
   * A way to check if an image is immutable
   * Not sure about this
   */
  def frozen: Boolean

  def getChannel(x: Int, y: Int, ch: Int): T

  def getPixel(x: Int, y: Int): Array[T]

  /**
   * How to translate the channels to RGB
   */
  def rgbOffsetsOpt: Option[RGBOffsets]
}
