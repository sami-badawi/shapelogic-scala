package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.image.BufferImage
import java.util.BitSet

/**
 * Abstract class for compare.<br />
 *
 * @author Sami Badawi
 *
 */
abstract class SBSimpleCompare(val bufferImage: BufferImage[Byte]) extends SBPixelCompare {

  var _slImage: BufferImage[Byte] = bufferImage

  var _currentColor: Int = 0
  var handledColor: Int = 0
  var mask: Int = 0
  var _maxDistance: Int = 10
  var bitSet: BitSet = null
  var fillWithOwnColor: Boolean = true
  var numberOfPixels: Int = 0
  var _modifying: Boolean = true
  var _farFromReferenceColor: Boolean = false

  /**
   * Similar and not handled
   */
  def newSimilar(index: Int): Boolean = {
    !isHandled(index) && similar(index)
  }

  /**
   * @return Returns the currentColor.
   */
  def getCurrentColor(): Int = {
    _currentColor
  }
  /**
   * @param currentColor The currentColor to set.
   */
  def setCurrentColor(currentColor: Int) {
    this._currentColor = currentColor
  }
  /**
   * @return Returns the handledColor.
   */
  def getHandledColor(): Int = {
    handledColor
  }
  /**
   * @param handledColor The handledColor to set.
   */
  def setHandledColor(handledColor: Int): Unit = {
    this.handledColor = handledColor
  }
  /**
   * @return Returns the maxDist.
   */
  def getMaxDist(): Int = {
    _maxDistance
  }
  /**
   * @param maxDist The maxDist to set.
   */
  def setMaxDist(maxDist: Int) = {
    this._maxDistance = maxDist
  }

  def grabColorFromPixel(startX: Int, startY: Int): Unit = {
    //    _currentColor = _slImage.get(startX, startY) & mask //XXX put back
    if (fillWithOwnColor)
      handledColor = _currentColor
  }

  /** Call at start, this might also work as a reset	 */
  def init(image: BufferImage[Byte]) =
    {
      bitSet = new BitSet(image.pixelCount)
      numberOfPixels = 0
      _slImage = image
    }

  /**
   * Check if pixel at index already have been handled.
   */
  def isHandled(index: Int): Boolean = {
    bitSet.get(index)
  }

  /**
   * Mark that pixel at index has been handled
   */
  def setHandled(index: Int): Unit = {
    bitSet.set(index, true)
    numberOfPixels += 1 //this assumes that each pixel is only called once 
  }

  def getNumberOfPixels(): Int = {
    numberOfPixels
  }

  /** Should pixels be modified. */
  override def isModifying(): Boolean = {
    _modifying
  }

  override def setModifying(input: Boolean): Unit = {
    _modifying = input
  }
  override def setMaxDistance(maxDistance: Int): Unit = {
    _maxDistance = maxDistance
  }

  override def isFarFromReferencColor(): Boolean = {
    _farFromReferenceColor
  }

  override def setFarFromReferencColor(farFromColor: Boolean) = {
    _farFromReferenceColor = farFromColor
  }
}
