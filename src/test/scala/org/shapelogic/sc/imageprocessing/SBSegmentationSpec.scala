package org.shapelogic.sc.imageprocessing

import org.scalatest._

import spire.algebra._
import spire.std._
//import spire.math.Integral
import spire.syntax.ring._
//import spire.math.Numberic
import spire.implicits._
import org.shapelogic.sc.image.BufferImage
import scala.collection.mutable.ArrayBuffer

class SBSegmentationSpec extends FunSuite with BeforeAndAfterEach {
  test("SBSegmentation 1 pixel count") {
    val bytes = Array[Byte](100)
    val inputImage = new BufferImage[Byte](1, 1, 1, bytes)
    val sbSegmentation = new SBSegmentation(inputImage, None)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, false)
    assertResult(1) { sbPendingVerticalSeq.size }
  }

  test("SBSegmentation 3 pixels horizontal count") {
    val bytes = Array[Byte](100, 100, 100)
    val inputImage = new BufferImage[Byte](3, 1, 1, bytes)
    val sbSegmentation = new SBSegmentation(inputImage, None)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, false)
    assertResult(2) { sbPendingVerticalSeq.size }
    assertResult(ArrayBuffer(SBPendingVertical(0, 0, 0, false), SBPendingVertical(1, 2, 0, true))) { sbPendingVerticalSeq }
  }

  test("SBSegmentation 2 pixels vertical count") {
    val bytes = Array[Byte](100, 100)
    val inputImage = new BufferImage[Byte](1, 2, 1, bytes)
    val sbSegmentation = new SBSegmentation(inputImage, None)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, false)
    assertResult(1) { sbPendingVerticalSeq.size }
  }

  test("SBSegmentation 2 pixels horizontal count 2 colors") {
    val bytes = Array[Byte](100, 10)
    val inputImage = new BufferImage[Byte](2, 1, 1, bytes)
    val sbSegmentation = new SBSegmentation(inputImage, None)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, false)
    assertResult(1) { sbPendingVerticalSeq.size }
  }

  test("SBSegmentation 2 pixels vertical count 2 colors") {
    val bytes = Array[Byte](100, 10)
    val inputImage = new BufferImage[Byte](1, 2, 1, bytes)
    val sbSegmentation = new SBSegmentation(inputImage, None)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, false)
    assertResult(1) { sbPendingVerticalSeq.size }
  }

  test("SBSegmentation 1 pixel") {
    val bytes = Array[Byte](100)
    val inputImage = new BufferImage[Byte](1, 1, 1, bytes)
    val outputBufferImage = SBSegmentation.transform(inputImage)
    assertResult(bytes.toSeq) { outputBufferImage.data.toSeq }
  }

  test("SBSegmentation 2 pixels horizontal count expandRight") {
    val bytes = Array[Byte](100, 100)
    val inputImage = new BufferImage[Byte](2, 1, 1, bytes)
    val sbSegmentation = new SBSegmentation(inputImage, None)
    val colorAtStart = sbSegmentation.setPoint(0, 0).toSeq
    assertResult(Seq(100.toByte)) { colorAtStart.toSeq }
    val sbPendingVerticalSeq = sbSegmentation.expandRight(x = 0, y = 0)
    assertResult(1) { sbPendingVerticalSeq.size }
    assertResult(SBPendingVertical(0, 1, 0, true)) { sbPendingVerticalSeq.head }
    println(s"sbPendingVerticalSeq: $sbPendingVerticalSeq")
  }

  test("SBSegmentation 2 pixels horizontal count expandLeft") {
    val bytes = Array[Byte](100, 100)
    val inputImage = new BufferImage[Byte](2, 1, 1, bytes)
    val sbSegmentation = new SBSegmentation(inputImage, None)
    val colorAtStart = sbSegmentation.setPoint(0, 0).toSeq
    assertResult(Seq(100.toByte)) { colorAtStart.toSeq }
    val sbPendingVerticalSeq = sbSegmentation.expandLeft(x = 1, y = 0)
    assertResult(1) { sbPendingVerticalSeq.size }
    assertResult(SBPendingVertical(0, 1, 0, true)) { sbPendingVerticalSeq.head }
    println(s"sbPendingVerticalSeq: $sbPendingVerticalSeq")
  }

}