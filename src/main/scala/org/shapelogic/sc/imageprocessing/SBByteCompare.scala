package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.image.BufferImage

/**
 * Compare implementations for gray scale.
 *
 * @author Sami Badawi
 *
 */
class SBByteCompare(bufferImage: BufferImage[Byte]) extends SBSimpleCompare(bufferImage) {

//  var bufferImage: BufferImage[Byte] = null // should be same as _slImage I think
  def defcolorDistance(color1: Int, color2: Int): Int = {
    scala.math.abs(color1 - color2)
  }

  private var _pixels: Array[Byte] = null
  var MASK: Int = 0xff;

  /**
   * Tells if the color at index is close enough the set color to
   * be considered part of the segmented area.
   */
  def similar(index: Int): Boolean = {
    val localColor: Int = _pixels(index) & MASK;
    val diffL: Int = Math.abs(localColor - _currentColor);
    return (diffL <= _maxDistance) ^ _farFromReferenceColor;
  }

  override def init(ipIn: BufferImage[Byte]): Unit = {
    _slImage = ipIn;
    if (_slImage == null) {
      throw new Exception("ImageProcessor == null");
    }
    _pixels = _slImage.data
    mask = MASK;
    handledColor = 200;
    super.init(_slImage);
  }

  def colorDistance(color1: Int, color2: Int): Int = {
    return Math.abs(color1 - color2);
  }

  /**
   * This used for changes to other images or say modify all colors
   * to the first found.
   */
  def action(index: Int): Unit = {
    if (!isModifying())
      return ;
    _pixels(index) = handledColor.toByte
  }

  def getColorAsInt(index: Int): Int = {
    return _pixels(index) & MASK
  }
}
