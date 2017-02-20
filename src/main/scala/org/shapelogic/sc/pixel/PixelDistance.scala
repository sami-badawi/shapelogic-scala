package org.shapelogic.sc.pixel

import org.shapelogic.sc.numeric.NumberPromotion
import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import spire.math.Numeric
import spire.math.Integral
import spire.implicits._
import org.shapelogic.sc.image.{ RGBOffsets }
import org.shapelogic.sc.polygon.Box

/**
 * Used to run over image to calculate distance
 */
class PixelDistance[T: ClassTag, C: ClassTag: Numeric: Ordering](
  bufferImage: BufferImage[T],
  maxDist: C,
  val similarIsMatch: Boolean = true)(
    implicit promoterIn: NumberPromotion.Aux[T, C])
    extends PixelHandler[T, C] with PixelHandler1ByteResult[T, C] with PixelSimilarity {

  lazy val data: Array[T] = bufferImage.data
  lazy val inputNumBands: Int = bufferImage.numBands
  lazy val box: Box = bufferImage.box
  lazy val rgbOffsets: RGBOffsets = bufferImage.getRGBOffsetsDefaults
  def inputHasAlpha: Boolean = rgbOffsets.hasAlpha

  val referencePointI = new Array[T](inputNumBands)
  val referencePointC = new Array[C](inputNumBands)

  def getIndex(x: Int, y: Int): Int = {
    bufferImage.getIndex(x, y)
  }

  override val promoter: NumberPromotion.Aux[T, C] = {
    promoterIn
  }

  def setIndexPoint(index: Int): Array[T] = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      referencePointI(i) = data(index + i)
      referencePointC(i) = promoter.promote(data(index + i))
    }
    referencePointI
  }

  def takeColorFromPoint(x: Int, y: Int): Array[T] = {
    val index = bufferImage.getIndex(x, y)
    setIndexPoint(index)
  }

  def setReferencePointArray(iArray: Array[T]): Unit = {
    var numBands: Int = iArray.length
    if (inputNumBands != iArray.length) {
      if (inputNumBands + 1 == iArray.length) {
        println(s"setReferencePointArray: inputNumBands: $inputNumBands color depth: ${iArray.length}")
        numBands = inputNumBands
      } else {
        println(s"setReferencePointArray array should have same size")
        throw new Exception(s"setReferencePointArray array should have same size")
      }
    }
    cfor(0)(_ < numBands, _ + 1) { i =>
      referencePointI(i) = iArray(i)
      referencePointC(i) = promoter.promote(iArray(i))
    }
  }

  def calc(indexIn: Int, channelOut: Int): T = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      val diff = promoterIn.promote(data(i)) - referencePointC(i)
      if (maxDist < diff || diff < -maxDist)
        return promoterIn.maxValueBuffer
    }
    promoterIn.minValueBuffer
  }

  def calcByte(indexIn: Int): Byte = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      val diff = promoterIn.promote(data(i)) - referencePointC(i)
      if (maxDist < diff || diff < -maxDist)
        return 1
    }
    0
  }

  def similarIndex(indexIn: Int): Boolean = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      val diff = promoterIn.promote(data(indexIn + i)) - referencePointC(i)
      if (maxDist < diff || diff < -maxDist)
        return false
    }
    true
  }

  def similar(x: Int, y: Int): Boolean = {
    val res = similarIndex(bufferImage.getIndex(x, y))
    //    println(s"x: $x, y: $y, similar: $res")
    res
  }
}