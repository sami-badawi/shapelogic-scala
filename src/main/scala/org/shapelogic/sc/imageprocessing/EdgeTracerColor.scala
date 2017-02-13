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
 * Edge Tracer. <br />
 *
 * The first version is based on Wand from ImageJ 1.38.<br />
 *
 * It traces with a 2 x 2 square that put the top left pixels inside the
 * particle and the bottom right outside.<br />
 *
 * Might be replaced with a version that has all the pixels inside.<br />
 *
 * @author Sami Badawi
 *
 */
class EdgeTracerColor(
    image: BufferImage[Byte],
    maxDistance: Double,
    similarIsMatch: Boolean) extends PixelFollow(image, maxDistance, similarIsMatch) with IEdgeTracer {

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

  /**
   * Traces the boundary of an area of uniform color, where
   * 'startX' and 'startY' are somewhere inside the area.
   * A 16 entry lookup table is used to determine the
   * direction at each step of the tracing process.
   */
  def autoOutline(startX: Int, startY: Int): Polygon = {
    val topOption = findTop(startX, startY)
    topOption match {
      case Some((x, y)) => traceEdge(x, y, 2)
      case None => {
        println(s"Top point not found starting at x: $startX, y: $startY")
        null
      }
    }
  }
}

object EdgeTracerColor {

  def fromBufferImage(
    image: BufferImage[Byte],
    referenceColor: Array[Byte],
    maxDistance: Double,
    similarIsMatch: Boolean): EdgeTracerColor = {
    val edgeTracer = new EdgeTracerColor(image, maxDistance, similarIsMatch)
    edgeTracer.setReferencePointArray(referenceColor)
    edgeTracer
  }

  def fromBufferImageAndPoint(
    image: BufferImage[Byte],
    x: Int,
    y: Int,
    maxDistance: Double = 10): EdgeTracerColor = {
    val edgeTracer = new EdgeTracerColor(image, maxDistance, similarIsMatch = true)
    edgeTracer.takeColorFromPoint(x, y)
    edgeTracer
  }
}
