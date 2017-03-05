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
  lazy val inputHasAlpha: Boolean = rgbOffsets.hasAlpha
  lazy val alphaChannel = bufferImage.alphaChannel

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
    count += 1
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      if (i != alphaChannel) {
        val diff = promoterIn.promote(data(indexIn + i)) - referencePointC(i)
        if (maxDist < diff || maxDist < -diff) {
          if (similarIsMatch)
            return promoterIn.maxValueBuffer
          else
            return promoterIn.minValueBuffer
        }
      }
    }
    matchCount += 1
    if (similarIsMatch)
      promoterIn.minValueBuffer
    else
      promoterIn.maxValueBuffer
  }

  var matchCount: Int = 0
  var count: Int = 0

  def calcByte(indexIn: Int): Byte = {
    count += 1
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      if (i != alphaChannel) {
        val diff = promoterIn.promote(data(indexIn + i)) - referencePointC(i)
        //        println(s"diff: $diff")
        if (maxDist < diff || maxDist < -diff) {
          if (similarIsMatch)
            return 0
          else
            return -1
        }
      }
    }
    matchCount += 1
    if (similarIsMatch)
      -1
    else
      1
  }

  def info(): String = {
    val colorString = referencePointC.mkString("RGB: (", ",", ")")
    s"count: $count, matchCount: $matchCount, colorString: $colorString, alphaChannel: $alphaChannel"
  }

  def similarIndex(indexIn: Int): Boolean = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      if (i != alphaChannel) {
        val diff = promoterIn.promote(data(indexIn + i)) - referencePointC(i)
        if (maxDist < diff || diff < -maxDist)
          return false
      }
    }
    true
  }

  def similar(x: Int, y: Int): Boolean = {
    val res = similarIndex(bufferImage.getIndex(x, y))
    //    println(s"x: $x, y: $y, similar: $res")
    res
  }
}

object PixelDistance {

  def apply[T: ClassTag, C: ClassTag: Numeric: Ordering](
    bufferImage: BufferImage[T],
    maxDist: C,
    similarIsMatch: Boolean = true)(
      implicit promoterIn: NumberPromotion.Aux[T, C]): PixelDistance[T, C] = {
    val pixelDistance = new PixelDistance[T, C](bufferImage, maxDist, similarIsMatch)(
      implicitly[ClassTag[T]],
      implicitly[ClassTag[C]],
      implicitly[Numeric[C]],
      implicitly[Ordering[C]],
      promoterIn)
    pixelDistance
  }

  def makeFromColor[T: ClassTag, C: ClassTag: Numeric: Ordering](
    bufferImage: BufferImage[T],
    maxDist: C,
    colorArray: Array[T],
    similarIsMatch: Boolean = true)(
      implicit promoterIn: NumberPromotion.Aux[T, C]): PixelDistance[T, C] = {
    val pixelDistance = new PixelDistance[T, C](bufferImage, maxDist, similarIsMatch)(
      implicitly[ClassTag[T]],
      implicitly[ClassTag[C]],
      implicitly[Numeric[C]],
      implicitly[Ordering[C]],
      promoterIn)
    pixelDistance.setReferencePointArray(colorArray)
    pixelDistance
  }

  def makeFromPoint[T: ClassTag, C: ClassTag: Numeric: Ordering](
    bufferImage: BufferImage[T],
    maxDist: C,
    x: Int,
    y: Int,
    similarIsMatch: Boolean = true)(
      implicit promoterIn: NumberPromotion.Aux[T, C]): PixelDistance[T, C] = {
    val pixelDistance = new PixelDistance[T, C](bufferImage, maxDist, similarIsMatch)(
      implicitly[ClassTag[T]],
      implicitly[ClassTag[C]],
      implicitly[Numeric[C]],
      implicitly[Ordering[C]],
      promoterIn)
    pixelDistance.takeColorFromPoint(x, y)
    pixelDistance
  }
}