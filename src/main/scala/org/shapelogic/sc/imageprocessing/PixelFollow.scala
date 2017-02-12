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

/**
 * PixelFollow is based on EdgeTracer
 *
 * It is meant to be the base class for vectorize, edge trace, 
 * binary operation, segmentation
 *
 * @author Sami Badawi
 *
 */
class PixelFollow(image: BufferImage[Byte], maxDistance: Double, traceCloseToColor: Boolean) extends IEdgeTracer {
  val verboseLogging = false

  //  var _colorDistanceWithImage:  = //ColorFactory.makeColorDistanceWithImage(image)
  import PrimitiveNumberPromotersAux.AuxImplicit._
  lazy val pixelDistance = new PixelDistance(image, maxDistance.toInt) //XXX 

  lazy val width: Int = image.width
  lazy val height: Int = image.height

  var _dirs = new Array[Boolean](Constants.DIRECTIONS_AROUND_POINT)
  val STEP_SIZE_FOR_4_DIRECTIONS = 2

  lazy val maxLength = scala.math.min(10000, image.pixelCount + 4)

  /**
   *  Use XOR to either handle colors close to reference color or far away.
   */
  def inside(x: Int, y: Int): Boolean = {
    if (x < 0 || y < 0)
      return false
    if (width <= x || height <= y)
      return false
    traceCloseToColor ^ (!pixelDistance.similar(x, y))
  }

  /**
   * Traces the boundary of an area of uniform color, where
   * 'startX' and 'startY' are somewhere inside the area.
   * A 16 entry lookup table is used to determine the
   * direction at each step of the tracing process.
   */
  def autoOutline(startX: Int, startY: Int): Polygon = {
    var x = startX
    var y = startY
    if (!inside(x, y)) {
      println(s"First point inside($x, $y) not inside. Exit")
      return null
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
    traceEdge(x, y, 2)
  }

  def nextDirection(x: Int, y: Int, lastDirection: Int, clockwise: Boolean): Int = {
    var directions: Array[Boolean] = makeDirections(x, y, true)
    val lastDirectionReleativeCurrent = lastDirection + Constants.DIRECTIONS_AROUND_POINT / 2
    val stepSize = STEP_SIZE_FOR_4_DIRECTIONS
    cfor(2)(_ <= Constants.DIRECTIONS_AROUND_POINT, _ + stepSize) { i =>
      var step = i
      if (!clockwise)
        step = Constants.DIRECTIONS_AROUND_POINT - i
      val real_direction = (lastDirectionReleativeCurrent + step) % Constants.DIRECTIONS_AROUND_POINT
      //Return first point that is inside
      if (directions(real_direction))
        return real_direction
    }
    -1 //Not found
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

  /**
   * Set reference color to the color of a point
   */
  def setPoint(x: Int, y: Int): Array[Byte] = {
    pixelDistance.setPoint(x, y)
  }

  /**
   * Set reference color directly in Byte
   */
  def setReferencePointArray(iArray: Array[Byte]): Unit = {
    pixelDistance.setReferencePointArray(iArray)
  }

  def traceEdge(xstart: Int, ystart: Int, startingDirectionIn: Int): Polygon = { //XXX
    val polygon = new Polygon()
    polygon.startMultiLine()
    val chainCodeHandler = new ChainCodeHandler(polygon.getAnnotatedShape())
    chainCodeHandler.setup()
    chainCodeHandler.setMultiLine(polygon.getCurrentMultiLine())
    chainCodeHandler.setFirstPoint(new CPointInt(xstart, ystart))
    var x = xstart
    var y = ystart
    val startingDirection = BaseVectorizer.oppesiteDirection(nextDirection(x, y, startingDirectionIn - 2, false).toByte)
    var direction: Int = startingDirection
    var count = 0
    var stop = false
    do {
      count += 1
      direction = nextDirection(x, y, direction, true)
      if (-1 == direction)
        stop = true
      direction match {
        case UP => {
          y = y - 1
        }
        case DOWN => {
          y = y + 1
        }
        case LEFT => {
          x = x - 1
        }
        case RIGHT => {
          x = x + 1
        }
        case -1 => {
          stop = true
        }
      }
      if (verboseLogging)
        println(s"direction: $direction new x: $x, y: $y")
      if (maxLength < count) {
        println(s"EdgeTracer: count $count exceeded max lenght")
        throw new Exception(s"EdgeTracer: count $count exceeded max lenght")
        stop = true
      }
      //If the chain becomes too long just give up
      if (!chainCodeHandler.addChainCode(direction.toByte))
        stop = true
      //		} while ((x!=xstart || y!=ystart))
      //Original clause causes termination problems
    } while (x != xstart ||
      y != ystart ||
      direction != startingDirection ||
      stop)
    chainCodeHandler.getValue()
    polygon.setPerimeter(chainCodeHandler.getPerimeter())
    polygon.getValue()
    polygon.getBBox().add(chainCodeHandler._bBox)
    polygon
  }
}

object PixelFollow {

  def fromBufferImage(
    image: BufferImage[Byte],
    referenceColor: Array[Byte],
    maxDistance: Double,
    traceCloseToColor: Boolean): PixelFollow = {
    val edgeTracer = new PixelFollow(image, maxDistance, traceCloseToColor)
    edgeTracer.setReferencePointArray(referenceColor)
    edgeTracer
  }
}
