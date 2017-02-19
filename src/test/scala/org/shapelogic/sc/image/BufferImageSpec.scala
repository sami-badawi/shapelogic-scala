package org.shapelogic.sc.image

import org.scalatest._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._

class BufferImageSpecSpec extends FunSuite with BeforeAndAfterEach {
  test("Image get instantiated to 0") {
    val grayByteImage: BufferImage[Byte] = BufferImage.makeBufferImage[Byte](
      width = 15,
      height = 10,
      numBands = 1,
      bufferInput = null)
    assertResult(0) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
    assertResult(0) { grayByteImage.getChannel(x = 14, y = 9, ch = 0) }
  }

  test("Image fill with 100") {
    val grayByteImage = BufferImage.makeBufferImage[Byte](
      width = 15,
      height = 10,
      numBands = 1,
      bufferInput = null)
    grayByteImage.data(0)
    println("grayByteImage.data.getClass.getSimpleName " + grayByteImage.data.getClass.getSimpleName)
    grayByteImage.fill(100)
    assertResult(100) { grayByteImage.getChannel(x = 0, y = 0, ch = 0) }
  }
}