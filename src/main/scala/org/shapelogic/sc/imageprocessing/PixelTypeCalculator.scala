package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.calculation.CalcInvoke
import org.shapelogic.sc.util.Constants

/**
 * PixelTypeCalculator stores some values for points and
 * calculated the type of points based on them.
 *
 * This is a very problematic way to do lazy calculations
 * Set mutable variables one by one
 *
 * One reason it to not create a new object each time
 *
 * @author Sami Badawi
 *
 */
class PixelTypeCalculator extends CalcInvoke[PixelType] {
  val verboseLogging = false

  var neighbors: Int = 0
  var unusedNeighbors: Int = 0
  var regionCrossings: Int = 0
  var firstUnusedNeighbor: Byte = Constants.DIRECTION_NOT_USED
  var distanceBetweenLastDirection: Int = 0
  var pixelType: PixelType = PixelType.PIXEL_FOREGROUND_UNKNOWN

  /**
   * The input variables should not be set individually
   */
  def setInput(neighbors: Int,
    unusedNeighbors: Int,
    regionCrossings: Int,
    firstUnusedNeighbor: Byte = Constants.DIRECTION_NOT_USED,
    distanceBetweenLastDirection: Int = 0,
    pixelIndex: Int = Constants.BEFORE_START_INDEX): Unit = {
    this.neighbors = neighbors
    this.unusedNeighbors = unusedNeighbors
    this.regionCrossings = regionCrossings
    this.firstUnusedNeighbor = firstUnusedNeighbor
    this.distanceBetweenLastDirection = distanceBetweenLastDirection
    this.pixelIndex = pixelIndex

    if (verboseLogging) {
      val logMessage = s"""
neighbors: $neighbors
unusedNeighbors: $unusedNeighbors
regionCrossings: $regionCrossings
firstUnusedNeighbor: $firstUnusedNeighbor
distanceBetweenLastDirection: $distanceBetweenLastDirection
"""
      println(logMessage)
    }

    _dirty = true
  }

  /**
   * Do full calculation of PixelType from input
   * More functional style of calculation
   */
  def findPixelType(neighbors: Int,
    unusedNeighbors: Int,
    regionCrossings: Int,
    firstUnusedNeighbor: Byte = Constants.DIRECTION_NOT_USED,
    distanceBetweenLastDirection: Int = 0,
    pixelIndex: Int = Constants.BEFORE_START_INDEX): PixelType = {
    setInput(neighbors,
      unusedNeighbors,
      regionCrossings,
      firstUnusedNeighbor,
      distanceBetweenLastDirection,
      pixelIndex)
    getValue()
  }

  /** This is for debugging so you can see what pixel this was last run for*/
  var pixelIndex: Int = 0

  /**
   * Only some of the pixel type finders are finding the highestRanked
   * values and no calculations are done on them. Could be split out in sub
   * class, but does not seem to be worth it.
   */
  var highestRankedUnusedPixelTypeColor: Int = 0
  var highestRankedUnusedNeighbor: Byte = Constants.DIRECTION_NOT_USED
  var highestRankedUnusedIsUnique: Boolean = true
  var isLocalMaximum: Boolean = false
  var _dirty: Boolean = true

  def getPixelType(): PixelType = {
    getValue()
  }

  override def setup(): Unit = {
    neighbors = 0
    unusedNeighbors = 0
    regionCrossings = 0
    firstUnusedNeighbor = Constants.DIRECTION_NOT_USED
    distanceBetweenLastDirection = 0
    pixelType = PixelType.PIXEL_FOREGROUND_UNKNOWN
    pixelIndex = Constants.BEFORE_START_INDEX
    highestRankedUnusedPixelTypeColor = 0
    highestRankedUnusedNeighbor = Constants.DIRECTION_NOT_USED
    highestRankedUnusedIsUnique = true
    isLocalMaximum = false
    _dirty = true
  }

  /**
   * Just look at the center point and see if different from foreground unknown
   * I do not think this is right for the priority based version.
   * Since you can run more times.
   */
  override def isDirty(): Boolean = {
    _dirty
    //		return pixelType == PixelType.PIXEL_FOREGROUND_UNKNOWN
  }

  override def invoke(): PixelType = {
    distanceBetweenLastDirection %= Constants.DIRECTIONS_AROUND_POINT
    val newPixelType: PixelType = if (regionCrossings == 4) {
      if (neighbors == 2) {
        if (distanceBetweenLastDirection == 2 || distanceBetweenLastDirection == 6)
          PixelType.PIXEL_L_CORNER
        else
          PixelType.PIXEL_ON_LINE
      } else if (neighbors > 2)
        PixelType.PIXEL_EXTRA_NEIGHBOR
      else
        PixelType.PIXEL_EXTRA_NEIGHBOR // XXX should never get here        
    } else if (regionCrossings > 4)
      PixelType.PIXEL_JUNCTION //Junction point, more than cross index of 4
    else if (regionCrossings == 2) {
      if (neighbors == 1)
        PixelType.PIXEL_LINE_END
      else if (neighbors == 2)
        PixelType.PIXEL_V_CORNER
      else
        PixelType.PIXEL_BORDER //Edge of solid, cross index of 2
    } else if (regionCrossings == 0)
      PixelType.PIXEL_SOLID //Inner point, 8 neighbors or 7 where the last on is an even number.
    else
      PixelType.PIXEL_FOREGROUND_UNKNOWN //Before it is calculated
    _dirty = false
    pixelType = newPixelType
    pixelType
  }

  override def getValue(): PixelType = {
    if (isDirty())
      invoke()
    pixelType
  }
}
