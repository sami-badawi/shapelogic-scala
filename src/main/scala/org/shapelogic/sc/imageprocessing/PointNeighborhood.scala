package org.shapelogic.sc.imageprocessing

import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.pixel.PixelSimilarity
import scala.reflect.ClassTag
import org.shapelogic.sc.util.Constants

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

  def pixelIsMatch(x: Int, y: Int): Boolean = {
    pixelSimilarity.pixelMatch(x, y)
  }
}
