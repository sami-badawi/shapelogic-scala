package org.shapelogic.sc.pixel

import org.shapelogic.sc.polygon.Box
import org.shapelogic.sc.polygon.BoxLike
import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import scala.reflect.ClassTag

/**
 * Abstract the similarity
 *
 * First assume that the image only have one channel
 * I could make a different one with more if needed
 */
class PixelIdentity[T: ClassTag](
    image: BufferImage[T],
    refColor: T,
    val similarIsMatch: Boolean = true) extends PixelSimilarity {

  //  def getIndex(x: Int, y: Int): Int

  lazy val box: BoxLike = image.box
  lazy val data: Array[T] = image.data

  def similar(x: Int, y: Int): Boolean = {
    image.getChannel(x, y, ch = 0) == refColor
  }

  def similarIndex(index: Int): Boolean = {
    data(index) == refColor
  }
}