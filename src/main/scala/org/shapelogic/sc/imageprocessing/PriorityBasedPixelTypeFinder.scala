package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants
import spire.implicits._
import org.shapelogic.sc.image.BufferImage

/**
 * Classify points to find out what type they are.
 *
 * This is used in ShortLineBasedVectorizer. Those vectorizers did not work very well.
 *
 * @author Sami Badawi
 *
 */
class PriorityBasedPixelTypeFinder(image: BufferImage[Byte]) extends IPixelTypeFinder {
  lazy val _pixels: Array[Byte] = getPixels()
  lazy val _cyclePoints: Array[Int] = getCyclePoints()

  /**
   * From the current point find direction.
   *
   * A problem with finding maximum is that the neighbor might not be known.
   * Should the maximum only be calculated among unused?
   * I think that if you have a V point and a junction
   * If I already know a pixel should I do the calculation again?
   */
  def findPointType(pixelIndex: Int, reusedPixelTypeCalculator: PixelTypeCalculator): PixelTypeCalculator = {
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
    var wasBackground: Boolean = PixelType.BACKGROUND_POINT.color == _pixels(pixelIndex + _cyclePoints(Constants.DIRECTIONS_AROUND_POINT - 1))
    var unusedNeighbors: Int = 0
    var highestRankedUnusedPixelTypeColor: Int = 0
    var highestRankedPixelTypeColor: Int = 0
    var highestRankedUnusedNeighbor: Byte = Constants.DIRECTION_NOT_USED
    var highestRankedUnusedIsUnique: Boolean = true
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { i =>
      var pixelIndexI: Int = pixelIndex + _cyclePoints(i)
      var currentPixel: Byte = _pixels(pixelIndexI)
      isBackground = PixelType.BACKGROUND_POINT.color == currentPixel
      if (!isBackground) {
        neighbors += 1
        if (!PixelType.isUsed(_pixels(pixelIndexI))) {
          unusedNeighbors += 1
          if (firstUnusedNeighbor == Constants.DIRECTION_NOT_USED)
            firstUnusedNeighbor = i.toByte
        }
        if (PixelType.PIXEL_FOREGROUND_UNKNOWN.color != currentPixel) {
          var currentPixelInt: Int = currentPixel & Constants.BYTE_MASK
          if (PixelType.isUnused(currentPixel)) {
            if (highestRankedUnusedPixelTypeColor <= currentPixelInt) {
              if (highestRankedUnusedPixelTypeColor == currentPixelInt) {
                highestRankedUnusedIsUnique = false
              } else {
                highestRankedUnusedIsUnique = true
                highestRankedUnusedPixelTypeColor = currentPixelInt
                highestRankedUnusedNeighbor = i.toByte
              }
            }
          }
          if (highestRankedPixelTypeColor + 1 < currentPixelInt) { //To take care of used unused issues
            highestRankedPixelTypeColor = currentPixelInt
          }
        }
        previousDirection = lastDirection
        lastDirection = i.toByte
      }
      if (wasBackground != isBackground) {
        countRegionCrossings += 1
      }
      wasBackground = isBackground
    }
    pixelTypeCalculator.regionCrossings = countRegionCrossings
    pixelTypeCalculator.neighbors = neighbors
    pixelTypeCalculator.firstUnusedNeighbor = firstUnusedNeighbor
    pixelTypeCalculator.unusedNeighbors = unusedNeighbors
    pixelTypeCalculator.pixelIndex = pixelIndex
    pixelTypeCalculator.highestRankedUnusedIsUnique = highestRankedUnusedIsUnique
    pixelTypeCalculator.highestRankedUnusedNeighbor = highestRankedUnusedNeighbor
    pixelTypeCalculator.highestRankedUnusedPixelTypeColor = highestRankedUnusedPixelTypeColor
    if (previousDirection != Constants.DIRECTION_NOT_USED)
      pixelTypeCalculator.distanceBetweenLastDirection = lastDirection - previousDirection
    if (PixelType.isUnused(_pixels(pixelIndex)))
      _pixels(pixelIndex) = pixelTypeCalculator.getPixelType().color
    else
      _pixels(pixelIndex) = PixelType.toUsed(pixelTypeCalculator.getPixelType())
    if ( //				unusedNeighbors != 1 && 
    0 != highestRankedPixelTypeColor &&
      highestRankedPixelTypeColor + 1 < (Constants.BYTE_MASK & _pixels(pixelIndex))) //To take care of used unused issues
      pixelTypeCalculator.isLocalMaximum = true
    pixelTypeCalculator
  }

  override def getCyclePoints(): Array[Int] = {
    image.cyclePoints
  }

  override def getMaxX(): Int = {
    image.xMax
  }

  override def getMaxY(): Int = {
    image.yMax
  }

  override def getMinX(): Int = {
    image.xMin
  }

  override def getMinY(): Int = {
    image.yMin
  }

  override def getPixels(): Array[Byte] = {
    image.data
  }

}
