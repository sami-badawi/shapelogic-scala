package org.shapelogic.sc.color

import org.shapelogic.sc.image.BufferImage

/**
 * A mathematical norm and distance in different color spaces.<br />
 *
 * So it is a distance that conforms with the vector multiplication.<br />
 *
 * This does not just apply to the color itself but also to the standard deviation.<br />
 *
 * How do I present the combination in a simple way?<br />
 *
 * For gray I could make a vector of 2 integers.<br />
 *
 * For color I could make a vector of 4 or 6 integers.<br />
 *
 * Maybe I can use a 1 norm that is divided by the number of dimensions.<br />
 *
 * I am not sure that the variations in the std is as big and as important as
 * the color variations.<br />
 *
 * Maybe all I need to do is giving some weights as input.<br />
 *
 * Maybe I could even have one norm that worked with different dimensions, with
 * different weights.<br />
 *
 * @author Sami Badawi
 *
 */
trait IColorDistanceWithImage extends IColorDistance {
  def distanceToReferenceColor(x: Int, y: Int): Double
  def setImage(image: BufferImage[Byte]): Unit
  def getImage(): BufferImage[Byte]
}

