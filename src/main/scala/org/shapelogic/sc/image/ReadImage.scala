package org.shapelogic.sc.image

import simulacrum._

/**
 * First version of real classes
 * Might be changed to be a typeclass in Cats later
 */
@typeclass trait ReadImage[@specialized T] extends Any with Serializable {

  /**
   * A way to check if an image is immutable
   * Not sure about this
   */
  def frozen: Boolean

  def numBands: Int

  def getChannel(x: Int, y: Int, ch: Int): T

  def getPixel(x: Int, y: Int): Array[T]
}
