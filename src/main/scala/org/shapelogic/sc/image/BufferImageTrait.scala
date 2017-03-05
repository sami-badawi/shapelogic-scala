package org.shapelogic.sc.image


trait BufferImageTrait[T] extends Any with Serializable {
  
  /*
   * Number of elements used by a one line
   */
  def stride: Int

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