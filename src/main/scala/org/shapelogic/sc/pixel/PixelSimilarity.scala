package org.shapelogic.sc.pixel

import org.shapelogic.sc.polygon.Box
import org.shapelogic.sc.polygon.BoxLike

/**
 * Abstract the similarity
 *
 * Does this need to have the image?
 */
trait PixelSimilarity {

  /**
   * This does not really belong in a similarity
   * but to prevent from having one more layer of classes involved
   * add it here
   *
   */
  def similarIsMatch: Boolean

  //  def getIndex(x: Int, y: Int): Int

  def box: BoxLike

  def similar(x: Int, y: Int): Boolean

  def similarIndex(index: Int): Boolean

  def pixelMatch(x: Int, y: Int): Boolean = {
    similarIsMatch ^ !similar(x, y)
  }

  def pixelMatchIndex(index: Int): Boolean = {
    similarIsMatch ^ !similarIndex(index)
  }

  def isInBounds(x: Int, y: Int): Boolean = {
    box.xMin <= x && x <= box.xMax && box.yMin <= y && y <= box.yMax
  }

  def matchInBounds(x: Int, y: Int): Boolean = {
    isInBounds(x, y) && pixelMatch(x, y)
  }
}