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
  import SBSegmentation._

  test("SBSegmentation 1 pixel count") {
    val bytes = Array[Byte](100)
    val inputImage = new BufferImage[Byte](1, 1, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, None)
    assertResult(1) { sbPendingVerticalSeq.size }
  }

  test("SBSegmentation 3 pixels horizontal count") {
    val bytes = Array[Byte](100, 100, 100)
    val inputImage = new BufferImage[Byte](3, 1, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, None)
    assertResult(1) { sbPendingVerticalSeq.size }
    assertResult(ArrayBuffer(SBPendingVertical(0, 2, 0, false))) { sbPendingVerticalSeq }
  }

  test("SBSegmentation 2 pixels vertical count") {
    val bytes = Array[Byte](100, 100)
    val inputImage = new BufferImage[Byte](1, 2, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, None)
    assertResult(2) { sbPendingVerticalSeq.size }
    assertResult(ArrayBuffer(SBPendingVertical(0, 0, 0, false), SBPendingVertical(0, 0, 1, true))) { sbPendingVerticalSeq }
  }

  test("SBSegmentation 2 pixels horizontal count 2 colors") {
    val bytes = Array[Byte](100, 10)
    val inputImage = new BufferImage[Byte](2, 1, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, None)
    assertResult(1) { sbPendingVerticalSeq.size }
  }

  test("SBSegmentation 2 pixels vertical count 2 colors") {
    val bytes = Array[Byte](100, 10)
    val inputImage = new BufferImage[Byte](1, 2, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val sbPendingVerticalSeq = sbSegmentation.segment(x = 0, y = 0, None)
    assertResult(1) { sbPendingVerticalSeq.size }
    assertResult(List(SBPendingVertical(0, 0, 0, false))) { sbPendingVerticalSeq }
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
    val sbSegmentation = SBSegmentation(inputImage)
    val colorAtStart = sbSegmentation.takeColorFromPoint(0, 0).toSeq
    assertResult(Seq(100.toByte)) { colorAtStart.toSeq }
    val sbPendingVerticalSeq = sbSegmentation.expandRight(x = 0, y = 0)
    assertResult(1) { sbPendingVerticalSeq.size }
    assertResult(SBPendingVertical(0, 1, 0, true)) { sbPendingVerticalSeq.head }
    println(s"sbPendingVerticalSeq: $sbPendingVerticalSeq")
  }

  test("SBSegmentation 2 pixels horizontal count expandLeft") {
    val bytes = Array[Byte](100, 100)
    val inputImage = new BufferImage[Byte](2, 1, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val colorAtStart = sbSegmentation.takeColorFromPoint(0, 0).toSeq
    assertResult(Seq(100.toByte)) { colorAtStart.toSeq }
    val sbPendingVerticalSeq = sbSegmentation.expandLeft(x = 1, y = 0)
    assertResult(1) { sbPendingVerticalSeq.size }
    assertResult(SBPendingVertical(0, 1, 0, true)) { sbPendingVerticalSeq.head }
    println(s"sbPendingVerticalSeq: $sbPendingVerticalSeq")
  }

  // =================== makePotentialNeibhbors ===================

  test("makePotentialNeibhbors height == 1") {
    val bytes = Array[Byte](100, 100)
    val inputImage = new BufferImage[Byte](2, 1, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val pending = SBPendingVertical(0, 0, 0, true)
    val found = Seq(pending)
    val actual = sbSegmentation.makePotentialNeibhbors(pending, found)

    assertResult(PaintAndCheckLines(found, Seq())) { actual }
  }

  test("makePotentialNeibhbors height == 2") {
    val bytes = Array[Byte](100, 100)
    val inputImage = new BufferImage[Byte](1, 2, 1, bytes)
    val sbSegmentation = SBSegmentation(inputImage)
    val pending = SBPendingVertical(0, 0, 0, true)
    val found = Seq(pending)
    val actual = sbSegmentation.makePotentialNeibhbors(pending, found)

    assertResult(PaintAndCheckLines(found, Seq(SBPendingVertical(0, 0, 1, true)))) { actual }
  }
}