package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants
import spire.implicits._
import org.shapelogic.sc.image.BufferImage

/**
 * Neighbor Checker.
 *
 * Runs around a point and find what type all the neighbor points have
 *
 * @author Sami Badawi
 *
 */
class NeighborChecker(
    image: BufferImage[Byte],
    //    parent: IPixelTypeFinder, 
    currentPixelIndex: Int) extends IPixelTypeFinder {
  //Find and set the type of all the neighbor points
  var extraNeighborPoint: FirstDirectionForType = new FirstDirectionForType()
  var junction: FirstDirectionForType = new FirstDirectionForType()
  var other: FirstDirectionForType = new FirstDirectionForType()
  var used: FirstDirectionForType = new FirstDirectionForType()
  var vCornerPoint: FirstDirectionForType = new FirstDirectionForType()
  var localPixelTypeCalculator: PixelTypeCalculator = new PixelTypeCalculator()

  var _parent: IPixelTypeFinder = null
  val _pixels: Array[Byte] = image.data
  val bufferLenght = image.bufferLenght
  var cyclePoints: Array[Int] = image.cyclePoints
  var _currentPixelIndex: Int = 0

  //	public NeighborChecker(IPixelTypeFinder parent, Int currentPixelIndex)
  //	{
  //		_parent = parent
  //		_pixels = getPixels()
  //		cyclePoints = getCyclePoints()
  //		_currentPixelIndex = currentPixelIndex
  //	}

  /** Run over the neighbors points and put them in categories. */
  def checkNeighbors(): Unit = {
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + 1) { iInt =>
      val i = iInt.toByte
      var pixelIndexI: Int = _currentPixelIndex + cyclePoints(i)
      var pixel: Byte = if (0 <= pixelIndexI && pixelIndexI < bufferLenght)
        _pixels(pixelIndexI)
      else
        PixelType.BACKGROUND_POINT.color
      if (pixel == PixelType.PIXEL_FOREGROUND_UNKNOWN.color) {
        localPixelTypeCalculator.setup()
        findPointType(pixelIndexI, localPixelTypeCalculator)
        pixel = localPixelTypeCalculator.getPixelType().color
        _pixels(pixelIndexI) = pixel
      }
      var isUsed: Boolean = PixelType.isUsed(pixel)
      if (isUsed) {
        used.addDirection(i, isUsed)
      }

      if (PixelType.BACKGROUND_POINT.color == pixel) {
        //        continue
      } else if (PixelType.PIXEL_JUNCTION.equalsIgnore(pixel)) {
        junction.addDirection(i, isUsed)
      } else if (PixelType.PIXEL_EXTRA_NEIGHBOR.equalsIgnore(pixel)) {
        extraNeighborPoint.addDirection(i, isUsed)
      } else if (PixelType.PIXEL_V_CORNER.equalsIgnore(pixel)) {
        vCornerPoint.addDirection(i, isUsed)
      } else {
        other.addDirection(i, isUsed)
      }
    }
  }

  def allNeighbors(): Int = {
    return extraNeighborPoint.count +
      junction.count +
      other.count +
      used.count +
      vCornerPoint.count
  }

  def falseJunction(): Boolean = {
    return 0 < vCornerPoint.count && allNeighbors() - vCornerPoint.count <= 2
  }

  def margin: Int = 0
  lazy val xMin: Int = image.xMin + margin
  lazy val xMax: Int = image.xMax - margin
  lazy val yMin: Int = image.yMin + margin
  lazy val yMax: Int = image.yMax - margin

  override def getPixels(): Array[Byte] = {
    return image.data
  }

  lazy val priorityBasedPixelTypeFinder = new PriorityBasedPixelTypeFinder(image)

  /**
   * XXX Not sure if I am using the right PixelTypeFinder
   */
  override def findPointType(pixelIndex: Int,
    reusedPixelTypeCalculator: PixelTypeCalculator): PixelTypeCalculator = {
    return priorityBasedPixelTypeFinder.findPointType(pixelIndex, reusedPixelTypeCalculator)
  }
}
