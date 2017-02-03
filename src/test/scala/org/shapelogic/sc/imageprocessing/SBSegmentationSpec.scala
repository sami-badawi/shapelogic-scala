package org.shapelogic.sc.imageprocessing

import org.scalatest._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._
import org.shapelogic.sc.image.BufferImage

class SBSegmentationSpec extends FunSuite with BeforeAndAfterEach {
  test("") {
    val bytes = Array[Byte](100)
    val inputImage = new BufferImage[Byte](1, 1, 1, bytes)
    val segmentation = new SBSegmentation(inputImage, None)
    val outputBufferImage = segmentation.result
    assertResult(bytes.toSeq) { outputBufferImage.data.toSeq }
  }
}