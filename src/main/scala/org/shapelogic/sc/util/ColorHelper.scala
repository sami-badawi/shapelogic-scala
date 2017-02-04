package org.shapelogic.sc.util

object ColorHelper {

  val byteAllZero: Byte = 0
  val byteAllOnes: Byte = -1.toByte
  val alpha1: Int = byteAllOnes.toInt << 24

  def byte2RbgInt(byte: Byte): Int = {
    byte.toInt | (byte << 8) | (byte << 16)
  }

  def byte2RbgaInt(byte: Byte): Int = {
    alpha1 | byte | (byte << 8) | (byte << 16)
  }

  def getBitInIntArray(intArray: Array[Int], index: Int): Boolean = {
    val intIndex = index / 32
    val bitIndex = index % 32
    val bitMask = 1 << bitIndex
    try {
      (intArray(intIndex) & bitMask) != 0
    } catch {
      case ex: Throwable => {
        println(s"index: $index, intIndex: $intIndex, intArray.lenght: ${intArray.length}  bitIndex: $bitIndex, bitMask: $bitMask")
        throw ex
      }
    }
  }

  def setBitInIntArray(intArray: Array[Int], index: Int, bit: Boolean): Unit = {
    val intIndex = index >> 5
    val bitIndex = index & 31
    val bitMask = 1 << bitIndex
    val newInt = intArray(intIndex) | bitMask
    intArray(intIndex) = newInt
  }
}