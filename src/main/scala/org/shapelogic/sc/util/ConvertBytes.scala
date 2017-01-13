package org.shapelogic.sc.util

import collection.JavaConverters._

import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * Helper that will convert between
 */
object ConvertBytes {

  def intArray2ByteArray(intArray: Array[Int]): Array[Byte] = {
    val byteBuffer: ByteBuffer = ByteBuffer.allocate(intArray.length * 4)
    val intBuffer: IntBuffer = byteBuffer.asIntBuffer()
    intBuffer.put(intArray)
    val byteArray: Array[Byte] = byteBuffer.array()
    byteArray
  }

}