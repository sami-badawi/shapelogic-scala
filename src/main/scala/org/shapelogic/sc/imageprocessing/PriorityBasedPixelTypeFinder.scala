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
class PriorityBasedPixelTypeFinder(val image: BufferImage[Byte]) extends IPixelTypeFinder {
  lazy val _pixels: Array[Byte] = image.data

  lazy val xMin: Int = image.xMin
  lazy val xMax: Int = image.xMax
  lazy val yMin: Int = image.yMin
  lazy val yMax: Int = image.yMax
  lazy val pixelCount = image.pixelCount

  lazy val cyclePoints: Array[Int] = image.cyclePoints

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
    val indexC = pixelIndex + cyclePoints(Constants.DIRECTIONS_AROUND_POINT - 1)
    val color = if (0 <= indexC && indexC < pixelCount)
      _pixels(indexC)
    else
      PixelType.BACKGROUND_POINT.color
    var wasBackground: Boolean = PixelType.BACKGROUND_POINT.color == color
    var unusedNeighbors: Int = 0
    var highestRankedUnusedPixelTypeColor: Int = 0
    var highestRankedPixelTypeColor: Int = 0
    var highestRankedUnusedNeighbor: Byte = Constants.DIRECTION_NOT_USED
    var highestRankedUnusedIsUnique: Boolean = true
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { i =>
      var pixelIndexI: Int = pixelIndex + cyclePoints(i)
      var currentPixel: Byte = if (0 <= indexC && indexC < pixelCount)
        _pixels(pixelIndexI)
      else
        PixelType.BACKGROUND_POINT.color
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
    pixelTypeCalculator.highestRankedUnusedIsUnique = highestRankedUnusedIsUnique
    pixelTypeCalculator.highestRankedUnusedNeighbor = highestRankedUnusedNeighbor
    pixelTypeCalculator.highestRankedUnusedPixelTypeColor = highestRankedUnusedPixelTypeColor
    val distanceBetweenLastDirection = if (previousDirection != Constants.DIRECTION_NOT_USED)
      lastDirection - previousDirection
    else
      0
    pixelTypeCalculator.setInput(neighbors = neighbors,
      unusedNeighbors = unusedNeighbors,
      regionCrossings = countRegionCrossings,
      firstUnusedNeighbor = firstUnusedNeighbor,
      distanceBetweenLastDirection = distanceBetweenLastDirection,
      pixelIndex = pixelIndex)

    if (PixelType.isUnused(_pixels(pixelIndex)))
      _pixels(pixelIndex) = pixelTypeCalculator.getPixelType().color
    else
      _pixels(pixelIndex) = PixelType.toUsed(pixelTypeCalculator.getPixelType())
    if ( //				unusedNeighbors != 1 && 
    0 != highestRankedPixelTypeColor &&
      highestRankedPixelTypeColor + 1 < (Constants.BYTE_MASK & _pixels(pixelIndex))) //To take care of used unused issues
      pixelTypeCalculator.isLocalMaximum = true
    pixelTypeCalculator.getValue() // Force calc
    pixelTypeCalculator
  }

}
