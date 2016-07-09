package org.shapelogic.sc.image

import simulacrum._

@typeclass trait BufferImage[@specialized T] extends ReadImage[T] 
{
  /*
   * 
   */
  def stride: Int

  /**
   * If there is a subimage
   */
  def startIndex: Int
  
  /**
   * Get the first channel if this is byte array
   * If it is an Int array with bytes packed in it would be the Int
   */
  def getIndex(x: Int, y: Int): Int
  
  /**
   * Might change to data
   */
  def buffer: Array[T]
}