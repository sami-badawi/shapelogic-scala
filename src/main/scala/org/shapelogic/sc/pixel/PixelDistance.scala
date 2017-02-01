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

  override def promoter: NumberPromotionMax.Aux[I, C] = {
    promoterIn
  }

  def setIndexPoint(index: Int): Unit = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      referencePointI(i) = data(i)
      referencePointC(i) = promoterIn.proromote(data(i))
    }
  }

  def setPoint(x: Int, y: Int): Unit = {
    val index = bufferImage.getIndex(x, y)
    setIndexPoint(index)
  }

  def setReferencePointArray(iArray: Array[I]): Unit = {
    if (inputNumBands != iArray.length)
      println(s"setReferencePointArray array should have same size")
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      referencePointI(i) = iArray(i)
      referencePointC(i) = promoterIn.proromote(iArray(i))
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

  def calcClose(indexIn: Int): Boolean = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      val diff = promoterIn.promote(data(i)) - promoterIn.promote(referencePoint(i))
      if (maxDist < diff || diff < -maxDist)
        return false
    }
    true
  }
}