package org.shapelogic.sc.image

import simulacrum._

@typeclass trait BufferImageTrait[@specialized T] extends Any with Serializable {
  /*
   * 
   */
  def stride: Int

  /**
   * If there is a subimage
   */
  def startIndex: Int

  def bufferLenght: Int

  /**
   * Get the first channel if this is byte array
   * If it is an Int array with bytes packed in it would be the Int
   */
  def getIndex(x: Int, y: Int): Int

  /**
   * Might change to data
   */
  def data: Array[T]
}