package org.shapelogic.sc.color

/**
 * Color Channels is an array of int representing the different color
 * channels in a color.<br />
 *
 * @author Sami Badawi
 *
 */
trait ColorChannels[T] {

  def getColorChannels(): Array[T]

}
