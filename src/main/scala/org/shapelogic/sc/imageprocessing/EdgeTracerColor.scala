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
    similarIsMatch: Boolean) extends PixelFollow(image, maxDistance, similarIsMatch) {
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
    maxDistance: Double = 10,
    similarIsMatch: Boolean): EdgeTracerColor = {
    val edgeTracer = new EdgeTracerColor(image, maxDistance, similarIsMatch = true)
    edgeTracer.takeColorFromPoint(x, y)
    edgeTracer
  }
}
