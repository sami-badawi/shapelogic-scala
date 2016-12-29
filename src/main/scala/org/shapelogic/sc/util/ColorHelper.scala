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
}