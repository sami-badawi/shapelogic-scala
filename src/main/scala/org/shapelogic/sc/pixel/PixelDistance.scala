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
class PixelDistance[I: ClassTag, C: Numeric: Ordering](bufferImage: BufferImage[I], maxDist: C)(implicit promoterIn: NumberPromotionMax.Aux[I, C])
    extends PixelHandlerMax[I, C] {
  val data: Array[I] = bufferImage.data
  val inputNumBands: Int = bufferImage.numBands
  val rgbOffsets: RGBOffsets = bufferImage.getRGBOffsetsDefaults
  def inputHasAlpha: Boolean = rgbOffsets.hasAlpha

  val referencePoint = new Array[I](inputNumBands)

  override def promoter: NumberPromotionMax.Aux[I, C] = {
    promoterIn
  }

  def setIndexPoint(index: Int): Unit = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      referencePoint(i) = data(i)
    }
  }

  def setPoint(x: Int, y: Int): Unit = {
    val index = bufferImage.getIndex(x, y)
    setIndexPoint(index)
  }

  def calc(indexIn: Int, channelOut: Int): I = {
    cfor(0)(_ < inputNumBands, _ + 1) { i =>
      val diff = promoterIn.promote(data(i)) - promoterIn.promote(referencePoint(i))
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