package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.util.Constants.DOWN
import org.shapelogic.sc.util.Constants.LEFT
import org.shapelogic.sc.util.Constants.RIGHT
import org.shapelogic.sc.util.Constants.UP

import org.shapelogic.sc.polygon.CPointInt
import org.shapelogic.sc.polygon.Polygon
import org.shapelogic.sc.util.Constants
import org.shapelogic.sc.image.BufferImage

import spire.implicits._
import org.shapelogic.sc.color.IColorDistanceWithImage
import org.shapelogic.sc.pixel.PixelDistance
import org.shapelogic.sc.numeric.PrimitiveNumberPromotersAux
import scala.util.Try

/**
 * PixelFollow is based on EdgeTracer
 *
 * It is meant to be the base class for vectorize, edge trace,
 * binary operation, segmentation
 *
 * @author Sami Badawi
 *
 */
class PixelFollow(
    image: BufferImage[Byte],
    maxDistance: Double,
    similarIsMatch: Boolean) {
  val verboseLogging = false

  //  var _colorDistanceWithImage:  = //ColorFactory.makeColorDistanceWithImage(image)
  import PrimitiveNumberPromotersAux.AuxImplicit._

  // =============== lazy init ===============

  lazy val pixelDistance = new PixelDistance(image, maxDistance.toInt, similarIsMatch) //XXX 

  lazy val width: Int = image.width
  lazy val height: Int = image.height
  lazy val cyclePoints = image.cyclePoints

  var _dirs = new Array[Boolean](Constants.DIRECTIONS_AROUND_POINT)
  val STEP_SIZE_FOR_4_DIRECTIONS = 2

  lazy val maxLength = scala.math.min(10000, image.pixelCount + 4)

  // =============== abstract ===============
  def markPixelHandled(x: Int, y: Int): Unit = ???
  def newSimilarIndex(index: Int): Boolean = ???
  def newSimilar(x: Int, y: Int): Boolean = ???
  def pixelIsHandledIndex(index: Int): Boolean = ???
  def pixelIsHandled(x: Int, y: Int): Boolean = ???

  // =============== abstract ===============

  /**
   *  Use XOR to either handle colors close to reference color or far away.
   */
  def inside(x: Int, y: Int): Boolean = {
    if (x < 0 || y < 0)
      return false
    if (width <= x || height <= y)
      return false
    similarIsMatch ^ (!pixelDistance.similar(x, y))
  }

  /**
   * Set reference color to the color of a point
   */
  def takeColorFromPoint(x: Int, y: Int): Array[Byte] = {
    pixelDistance.takeColorFromPoint(x, y)
  }

  /**
   * Set reference color directly in Byte
   */
  def setReferencePointArray(iArray: Array[Byte]): Unit = {
    pixelDistance.setReferencePointArray(iArray)
  }

  // =============== util ===============

  def findTop(startX: Int, startY: Int): Option[(Int, Int)] = {
    var x = startX
    var y = startY
    if (!inside(x, y)) {
      println(s"First point inside($x, $y) not inside. Exit")
      return None
    }
    //Find top point inside
    do {
      y -= 1
    } while (inside(x, y))
    y += 1
    //Find leftmost top point inside
    do {
      x -= 1
    } while (inside(x, y))
    x += 1
    if (verboseLogging)
      println(s"Top point inside($x, $y) found start traceEdge(x, y, 2)")
    Try((x, y)).toOption
  }

  def makeDirections(x: Int, y: Int, only4points: Boolean): Array[Boolean] = {
    var stepSize = 1
    if (only4points)
      stepSize = STEP_SIZE_FOR_4_DIRECTIONS
    cfor(0)(_ < Constants.DIRECTIONS_AROUND_POINT, _ + stepSize) { i =>
      _dirs(i) = inside(x + Constants.CYCLE_POINTS_X(i), y + Constants.CYCLE_POINTS_Y(i))
    }
    _dirs
  }

}

object PixelFollow {

}
