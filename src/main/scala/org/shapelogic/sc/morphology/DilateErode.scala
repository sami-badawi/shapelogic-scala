package org.shapelogic.sc.morphology

import spire.implicits._
import spire.math._
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.image.HasBufferImage

/**
 * Take specialized binary gray scale byte image
 *
 * Code is a port from ImageJ from
 * https://raw.githubusercontent.com/imagej/imagej1/master/ij/process/BinaryProcessor.java
 *
 * Found this reference explaining Hilditch's Algorithm
 *
 * http://cgm.cs.mcgill.ca/~godfried/teaching/projects97/azar/skeleton.html
 *
 * DilateErode does not modify the input image
 *
 * @param image input image need to be gray scale
 * @param inverted true if black should be background
 */
class DilateErode(
    image: BufferImage[Byte],
    inverted: Boolean,
    dilate: Boolean = false) extends HasBufferImage[Byte] {

  var verboseLogging = false
  val margin = 2
  val maxPass = 100

  lazy val xMin: Int = image.xMin + margin
  lazy val xMax: Int = image.xMax - margin
  lazy val yMin: Int = image.yMin + margin
  lazy val yMax: Int = image.yMax - margin
  lazy val width: Int = image.width
  lazy val height: Int = image.height
  lazy val cyclePoints = image.cyclePoints
  lazy val bgColor: Byte = if (inverted)
    0
  else
    -1 //255
  lazy val fgColor: Byte = if (!inverted)
    0
  else
    -1 //255

  var inputPixels: Array[Byte] = image.data
  //  val outputImage: BufferImage[Byte] = image.copy()
  var outputPixels: Array[Byte] = image.data.clone()

  def collision(index: Int, color: Byte): Boolean = {
    if (inputPixels(index) == color)
      return true
    cfor(0)(_ < 8, _ + 1) { i =>
      if (inputPixels(index + cyclePoints(i)) == color)
        return true
    }
    false
  }

  def handlePixelDilate(x: Int, y: Int): Unit = {
    val index = image.getIndex(x, y)
    if (collision(index, fgColor))
      outputPixels(index) = fgColor
  }

  def handlePixelErode(x: Int, y: Int): Unit = {
    val index = image.getIndex(x, y)
    if (collision(index, bgColor))
      outputPixels(index) = bgColor
  }

  /**
   * DilateErode
   */
  def calc(): BufferImage[Byte] = {
    cfor(yMin)(_ < yMax, _ + 1) { y =>
      cfor(xMin)(_ < xMax, _ + 1) { x =>
        if (dilate)
          handlePixelDilate(x, y)
        else
          handlePixelErode(x, y)
      }
    }
    BufferImage.copyWithNewBuffer(image, outputPixels)
  }

  lazy val result = calc()
}

object DilateErode {

  // ================ Erode ================

  def erode(image: BufferImage[Byte]): BufferImage[Byte] = {
    if (image.numBands != 1) {
      println(s"Can only handle images with 1 channel run treshold first")
      return image
    }
    val skeletonize = new DilateErode(
      image,
      inverted = false,
      dilate = false)
    skeletonize.result
  }

  // ================ Dilate ================

  def dilate(image: BufferImage[Byte]): BufferImage[Byte] = {
    if (image.numBands != 1) {
      println(s"Can only handle images with 1 channel run treshold first")
      return image
    }
    val dilate = new DilateErode(
      image,
      inverted = false,
      dilate = true)
    dilate.calc()
  }

  def open(image: BufferImage[Byte]): BufferImage[Byte] = {
    dilate(erode(image))
  }

  def close(image: BufferImage[Byte]): BufferImage[Byte] = {
    erode(dilate(image))
  }

}