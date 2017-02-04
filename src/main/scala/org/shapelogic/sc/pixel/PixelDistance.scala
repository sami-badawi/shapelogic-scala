package org.shapelogic.sc.pixel

import org.shapelogic.sc.numeric.NumberPromotionMax
import org.shapelogic.sc.image.BufferImage
import scala.reflect.ClassTag
import spire.math.Numeric
import spire.math.Integral
import spire.implicits._
import org.shapelogic.sc.image.RGBOffsets

/**
 * Used to run over image to calculate distance
 */
class PixelDistance[I: ClassTag, C: ClassTag: Numeric: Ordering](bufferImage: BufferImage[I], maxDist: C)(implicit promoterIn: NumberPromotionMax.Aux[I, C])
    extends PixelHandlerMax[I, C] {

  val data: Array[I] = bufferImage.data
  val inputNumBands: Int = bufferImage.numBands
  val rgbOffsets: RGBOffsets = bufferImage.getRGBOffsetsDefaults
  def inputHasAlpha: Boolean = rgbOffsets.hasAlpha

  val referencePointI = new Array[I](inputNumBands)
  val referencePointC = new Array[C](inputNumBands)

  override val promoter: NumberPromotionMax.Aux[I, C] = {
    promoterIn
  }

  def setIndexPoint(index: Int): Array[I] = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      referencePointI(i) = data(index + i)
      referencePointC(i) = promoter.promote(data(index + i))
    }
    referencePointI
  }

  def setPoint(x: Int, y: Int): Array[I] = {
    val index = bufferImage.getIndex(x, y)
    setIndexPoint(index)
  }

  def setReferencePointArray(iArray: Array[I]): Unit = {
    if (inputNumBands != iArray.length)
      println(s"setReferencePointArray array should have same size")
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      referencePointI(i) = iArray(i)
      referencePointC(i) = promoter.promote(iArray(i))
    }
  }

  def calc(indexIn: Int, channelOut: Int): I = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      val diff = promoterIn.promote(data(i)) - referencePointC(i)
      if (maxDist < diff || diff < -maxDist)
        return promoterIn.maxValueBuffer
    }
    promoterIn.minValueBuffer
  }

  def similar(indexIn: Int): Boolean = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      val diff = promoterIn.promote(data(indexIn + i)) - referencePointC(i)
      if (maxDist < diff || diff < -maxDist)
        return false
    }
    true
  }

  def similar(x: Int, y: Int): Boolean = {
    val res = similar(bufferImage.getIndex(x, y))
//    println(s"x: $x, y: $y, similar: $res")
    res
  }
}