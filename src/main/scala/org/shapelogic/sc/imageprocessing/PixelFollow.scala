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
import org.shapelogic.sc.image.BufferBooleanImage

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

  /**
   * true means that a pixel is handled
   */
  lazy val handledPixelImage = new BufferBooleanImage(image.width, image.height, 1)

  // lazy get normal image properties for faster access
  lazy val width: Int = image.width
  lazy val height: Int = image.height
  lazy val numBands = image.numBands
  lazy val stride = image.stride
  lazy val xMin: Int = image.xMin
  lazy val xMax: Int = image.xMax
  lazy val yMin: Int = image.yMin
  lazy val yMax: Int = image.yMax
  lazy val cyclePoints = image.cyclePoints

  var _dirs = new Array[Boolean](Constants.DIRECTIONS_AROUND_POINT)
  val STEP_SIZE_FOR_4_DIRECTIONS = 2

  lazy val maxLength = scala.math.min(10000, image.pixelCount + 4)

  // =============== util for abstract ===============

  def pixelIsHandledIndex(index: Int): Boolean = {
    handledPixelImage.getChannel(x = index, y = 0, ch = 0) //XXX better way
  }

  def markPixelHandled(x: Int, y: Int): Unit = {
    handledPixelImage.setChannel(x, y, 0, true)
  }

  def pixelIsHandled(x: Int, y: Int): Boolean = {
    handledPixelImage.getChannel(x, y, ch = 0)
  }

  def newSimilarIndex(index: Int): Boolean = {
    pixelDistance.similarIndex(index) && !pixelIsHandledIndex(index)
  }

  def newSimilar(x: Int, y: Int): Boolean = {
    newSimilarIndex(image.getIndex(x, y))
  }

  /**
   *  Use XOR to either handle colors close to reference color or far away.
   */
  def matchInBounds(x: Int, y: Int): Boolean = {
    return pixelDistance.matchInBounds(x, y)
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
    if (!matchInBounds(x, y)) {
      println(s"First point matchInBounds($x, $y) not matchInBounds. Exit")
      return None
    }
    //Find top point matchInBounds
    do {
      y -= 1
    } while (matchInBounds(x, y))
    y += 1
    //Find leftmost top point matchInBounds
    do {
      x -= 1
    } while (matchInBounds(x, y))
    x += 1
    if (verboseLogging)
      println(s"Top point matchInBounds($x, $y) found start traceEdge(x, y, 2)")
    Try((x, y)).toOption
  }

}

object PixelFollow {

}
