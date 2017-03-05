package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.image.BufferImage


/**
 * Interface that are used for several Vectorizer.
 *
 * @author Sami Badawi
 *
 */
trait IPixelTypeFinder
{
  def image: BufferImage[Byte]
  def findPointType(pixelIndex: Int, reusedPixelTypeCalculator: PixelTypeCalculator): PixelTypeCalculator
}
