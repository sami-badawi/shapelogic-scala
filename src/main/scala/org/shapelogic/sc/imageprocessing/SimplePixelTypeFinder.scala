package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.image.BufferImage
import spire.implicits._

/**
 * Find the type of a pixel.
 *
 * This is used in MaxDistanceVectorizer, and it is only using the neighboors.
 *
 * @author Sami Badawi
 *
 */
class SimplePixelTypeFinder(val image: BufferImage[Byte]) extends IPixelTypeFinder {

  val _pixels = image.data
  lazy val cyclePoints = image.cyclePoints

  lazy val xMax: Int = image.xMax
  lazy val yMax: Int = image.yMax
  lazy val xMin: Int = image.xMin
  lazy val yMin: Int = image.yMin

  /**
   * From the current point find direction.
   *
   * A problem with finding maximum is that the neighbor might not be known.
   * Should the maximum only be calculated among unused?
   * I think that if you have a V point and a junction
   * If I already know a pixel should I do the calculation again?
   */
  def findPointType(
    pixelIndex: Int,
    reusedPixelTypeCalculator: PixelTypeCalculator): PixelTypeCalculator = {
    var pixelTypeCalculator: PixelTypeCalculator = reusedPixelTypeCalculator
    if (pixelTypeCalculator == null)
      pixelTypeCalculator = new PixelTypeCalculator()
    else
      pixelTypeCalculator.setup()
    var neighbors: Int = 0
    var countRegionCrossings: Int = 0
    var firstUnusedNeighbor: Byte = Constants.DIRECTION_NOT_USED
    var lastDirection: Byte = Constants.DIRECTION_NOT_USED
    var previousDirection: Byte = Constants.DIRECTION_NOT_USED
    var isBackground: Boolean = false
    var wasBackground: Boolean = PixelType.BACKGROUND_POINT.color == _pixels(pixelIndex + cyclePoints(Constants.DIRECTIONS_AROUND_POINT - 1))
    var unusedNeighbors: Int = 0
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { iInt =>
      val i: Byte = iInt.toByte
      var pixelIndexI: Int = pixelIndex + cyclePoints(i)
      var currentPixel: Byte = _pixels(pixelIndexI)
      isBackground = PixelType.BACKGROUND_POINT.color == currentPixel
      if (!isBackground) {
        neighbors += 1
        if (!PixelType.isUsed(_pixels(pixelIndexI))) {
          unusedNeighbors += 1
          if (firstUnusedNeighbor == Constants.DIRECTION_NOT_USED)
            firstUnusedNeighbor = i
        }
        previousDirection = lastDirection
        lastDirection = i
      }
      if (wasBackground != isBackground) {
        countRegionCrossings += 1
      }
      wasBackground = isBackground
    }
    val distanceBetweenLastDirection = if (previousDirection != Constants.DIRECTION_NOT_USED)
      lastDirection - previousDirection
    else
      0
    pixelTypeCalculator.setInput(
      neighbors = neighbors,
      unusedNeighbors = unusedNeighbors,
      regionCrossings = countRegionCrossings,
      firstUnusedNeighbor = firstUnusedNeighbor,
      distanceBetweenLastDirection = distanceBetweenLastDirection,
      pixelIndex = pixelIndex)
    pixelTypeCalculator.getValue() // Force calc
    if (PixelType.isUnused(_pixels(pixelIndex)))
      _pixels(pixelIndex) = pixelTypeCalculator.getPixelType().color
    else
      _pixels(pixelIndex) = PixelType.toUsed(pixelTypeCalculator.getPixelType())
    return pixelTypeCalculator
  }
}
