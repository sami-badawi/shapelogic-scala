package org.shapelogic.sc.util

import collection.JavaConverters._

import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * Helper that will convert between
 */
object ConvertBytes {

  /**
   * Based on this: http://stackoverflow.com/questions/1086054/how-to-convert-int-to-byte
   */
  def intArray2ByteArray(intArray: Array[Int]): Array[Byte] = {
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(intArray.length * 4)
    val intBuffer: IntBuffer = byteBuffer.asIntBuffer()
    intBuffer.put(intArray)
    val byteArray: Array[Byte] = byteBuffer.array()
    byteArray
  }

  def byteArray2IntArray(byteArray: Array[Byte]): Array[Int] = {
    val byteBuffer: ByteBuffer = ByteBuffer.wrap(byteArray)
    val intBuffer: IntBuffer = byteBuffer.asIntBuffer()
    intBuffer.array()
  }
}