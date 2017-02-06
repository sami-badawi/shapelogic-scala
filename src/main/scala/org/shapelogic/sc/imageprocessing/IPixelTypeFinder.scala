package org.shapelogic.sc.imageprocessing


/**
 * Interface that are used for several Vectorizer.
 *
 * @author Sami Badawi
 *
 */
trait IPixelTypeFinder extends PixelJumperByte {
  def findPointType(pixelIndex: Int, reusedPixelTypeCalculator: PixelTypeCalculator): PixelTypeCalculator
}
