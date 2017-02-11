package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.polygon.BoxLike

/**
 * Not sure if I need this, but this is a little bit of structure build on top
 * of the ByteProcessor.
 *
 * I think that the idea is that you just take one of them and use it in an adapter.
 *
 * @author Sami Badawi
 *
 */
trait PixelJumperByte extends BoxLike {

  def getPixels(): Array[Byte]

  /** What you need to add to the the index in the pixels array to get to the indexed point. */
  def getCyclePoints(): Array[Int]
}
