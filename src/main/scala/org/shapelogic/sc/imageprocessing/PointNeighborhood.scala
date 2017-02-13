package org.shapelogic.sc.imageprocessing

import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelSimilarity
import scala.reflect.ClassTag
import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.image.CrossingCountAndMatcing

/**
 * This is for calculating local properties
 * Crossing count
 * Similar neighbors
 * There are useful for pixel tracing algorithms
 */
class PointNeighborhood[T: ClassTag](
    image: BufferImage[T],
    pixelSimilarity: PixelSimilarity) {
  val cyclePoints = image.cyclePoints

  def crossingCount(x: Int, y: Int): Int = {
    var countRegionCrossings: Int = 0
    val pixelIndex: Int = image.getIndex(x, y)
    var isSimilar: Boolean = false
    var wasSimilar: Boolean = pixelSimilarity.similarIndex(pixelIndex + cyclePoints(0))
    cfor(1)(_ <= Constants.DIRECTIONS_AROUND_POINT, _ + 1) { i =>
      isSimilar = pixelSimilarity.similarIndex(pixelIndex + cyclePoints(i))
      if (wasSimilar != isSimilar) {
        countRegionCrossings += 1
      }
      wasSimilar = isSimilar
    }
    countRegionCrossings
  }

  /**
   * This is important for many pixel running operations.
   * E.g. skeleton algorithm
   *
   * This implementation is slow
   */
  def findCrossingCountAndMatcing(x: Int, y: Int): CrossingCountAndMatcing = {
    var countRegionCrossings: Int = 0
    val pixelIndex: Int = image.getIndex(x, y)
    var similarCount: Int = 0
    var isSimilar: Boolean = false
    var wasSimilar: Boolean = pixelSimilarity.similarIndex(pixelIndex + cyclePoints(0))
    cfor(1)(_ <= Constants.DIRECTIONS_AROUND_POINT, _ + 1) { i =>
      isSimilar = pixelSimilarity.similarIndex(pixelIndex + cyclePoints(i))
      if (isSimilar)
        similarCount += 1
      if (wasSimilar != isSimilar) {
        countRegionCrossings += 1
      }
      wasSimilar = isSimilar
    }
    val matchingCount = if (pixelSimilarity.similarIsMatch)
      similarCount
    else
      Constants.DIRECTIONS_AROUND_POINT - similarCount
    CrossingCountAndMatcing(countRegionCrossings, matchingCount)
  }

  def pixelIsMatch(x: Int, y: Int): Boolean = {
    pixelSimilarity.pixelMatch(x, y)
  }
}
