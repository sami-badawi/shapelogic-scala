package org.shapelogic.sc.image

/**
 * Most basic
 * Non generic
 */
trait ImageShape extends Any with Serializable {
  def width: Int

  def height: Int

  def numBands: Int

  /**
   * How to translate the channels to RGB
   */
  def rgbOffsetsOpt: Option[RGBOffsets]
}