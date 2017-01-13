package org.shapelogic.sc.util

import org.scalatest._

import ConvertBytes._

class ConvertBytesSpec extends FunSuite with BeforeAndAfterEach {

  test("intArray2ByteArray(Array(1))") {
    val intArray = Array(1)
    val byteSeq = Seq(0, 0, 0, 1)
    val byteArray = intArray2ByteArray(intArray)
    assertResult(4) { byteArray.length }
    assertResult(byteSeq) { byteArray.toSeq }
  }

  test("intArray2ByteArray(Array(1, 2))") {
    val intArray = Array(1, 2)
    val byteSeq = Seq(0, 0, 0, 1, 0, 0, 0, 2)
    val byteArray = intArray2ByteArray(intArray)
    assertResult(8) { byteArray.length }
    assertResult(byteSeq) { byteArray.toSeq }
  }
}