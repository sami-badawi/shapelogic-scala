package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.image.BufferImage
import spire.implicits._
import org.shapelogic.sc.color.ColorUtil

/**
 * Compare implementations for RGB.
 *
 * @author Sami Badawi
 *
 */
class SBColorCompare(bufferImage: BufferImage[Byte]) extends SBSimpleCompare(bufferImage) {

  val numBands: Int = bufferImage.numBands
  val numBandsNoAlpha = if (bufferImage.getRGBOffsetsDefaults.hasAlpha)
    numBands - 1
  else
    numBands

  val pixels: Array[Byte] = bufferImage.data
  val MASK: Int = 0xffffff
  val _colorChannels = new Array[Int](numBandsNoAlpha)
  val _splitColorChannels = new Array[Int](numBandsNoAlpha)

  /**
   * Tells if the color at index is close enought the set color to
   * be considered part of the segmented area.
   *
   * XXX very slow split currentColor into components
   */
  def similar(index: Int): Boolean = {
    val localColor: Int = pixels(index) & mask;
    //localColor
    ColorUtil.splitColor(localColor, _splitColorChannels)
    val diff: Int = colorDistance(_colorChannels, _splitColorChannels);
    return (diff <= _maxDistance) ^ _farFromReferenceColor;
  }

  /** split color coded as Int into 3 Int */
  def colorDistance(color1: Int, color2: Int): Int = {
    val rgb1: Array[Int] = ColorUtil.splitColor(color1);
    val rgb2: Array[Int] = ColorUtil.splitColor(color2);
    var dist: Int = 0;
    cfor(0)(_ < rgb1.length, _ + 1) { i =>
      dist += Math.abs(rgb1(i) - rgb2(i))
    }
    dist = dist / 3; // to make it fit with grayscale
    return dist;
  }

  /** split color coded as Int into 3 Int */
  def colorDistance(rgb1: Array[Int], rgb2: Array[Int]): Int = {
    var dist: Int = 0
    cfor(0)(_ < rgb1.length, _ + 1) { i =>
      dist += Math.abs(rgb1(i) - rgb2(i))
    }
    dist = dist / 3; // to make it fit with grayscale
    return dist;
  }

  /**
   * This used for changes to other images or say modify all colors
   * to the first found.
   */
  def action(index: Int): Unit = {
    if (!isModifying())
      return ;
    val dist: Int = colorDistance(pixels(index), handledColor)
    if (dist <= _maxDistance)
      pixels(index) = handledColor.toByte
    else {
    }
  }

  def getColorAsInt(index: Int): Int = {
    return pixels(index)
  }

  override def setCurrentColor(color: Int): Unit = {
    _currentColor = color;
    ColorUtil.splitColor(color, _colorChannels)
  }

  override def grabColorFromPixel(startX: Int, startY: Int): Unit = {
    super.grabColorFromPixel(startX, startY);
    ColorUtil.splitColor(_currentColor, _colorChannels)
  }
}
