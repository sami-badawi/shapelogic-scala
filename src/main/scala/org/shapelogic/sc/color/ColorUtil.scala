package org.shapelogic.sc.color

object ColorUtil {

  val BLUE_MASK: Int = 0xff
  val GREEN_MASK: Int = 0xff00
  val RED_MASK: Int = 0xff0000

  //Saving in this sequence gives the option of saving alpha too
  val RED_POS: Int = 0
  val GREEN_POS: Int = 1
  val BLUE_POS: Int = 2

  val GREEN_OFFSET: Int = 8
  val RED_OFFSET: Int = 16

  /** Split color coded as Int into 3 Int. */
  def splitColor(colorIn: Int): Array[Int] = {
    val iArray = new Array[Int](3)
    iArray(RED_POS) = (colorIn & RED_MASK) >> RED_OFFSET //red
    iArray(GREEN_POS) = (colorIn & GREEN_MASK) >> GREEN_OFFSET //green
    iArray(BLUE_POS) = colorIn & BLUE_MASK //blue
    iArray
  }

  /** Split color coded as Int into 3 Int. */
  def splitColor(colorIn: Int, iArrayIn: Array[Int]): Array[Int] = {
    val iArray = if (iArrayIn == null)
      new Array[Int](3)
    else
      iArrayIn
    iArray(RED_POS) = (colorIn & RED_MASK) >> RED_OFFSET //red
    iArray(GREEN_POS) = (colorIn & GREEN_MASK) >> GREEN_OFFSET //green
    iArray(BLUE_POS) = colorIn & BLUE_MASK //blue
    iArray
  }

  /** Split red from Int. */
  def splitRed(colorIn: Int): Int = {
    (colorIn & RED_MASK) >> RED_OFFSET //red
  }

  /** Split green from Int. */
  def splitGreen(colorIn: Int): Int = {
    (colorIn & GREEN_MASK) >> GREEN_OFFSET //green
  }

  /** Split blue from Int. */
  def splitBlue(colorIn: Int): Int = {
    colorIn & BLUE_MASK //blue
  }

  def packColors(red: Int, green: Int, blue: Int): Int = {
    val result: Int = (red << RED_OFFSET) + (green << GREEN_OFFSET) + blue
    result
  }

  def packColors(colors: Array[Int]): Int = {
    (colors(RED_POS) << RED_OFFSET) + (colors(GREEN_POS) << GREEN_OFFSET) + colors(BLUE_POS)
  }

  def grayToRGB(gray: Int): Int = {
    packColors(gray, gray, gray)
  }

  /**
   * Change an RGB color to a gray value.
   *
   * Based on the perceived contribution to the brightness.
   */
  def rgbToGray(colorIn: Int): Int = {
    val red: Int = (colorIn & RED_MASK) >> RED_OFFSET //red
    val green: Int = (colorIn & GREEN_MASK) >> GREEN_OFFSET //green
    val blue: Int = colorIn & BLUE_MASK //blue
    val brightness: Double = 0.3 * red + 0.59 * green + 0.11 * blue
    brightness.toInt
  }

  /**
   * When you have RGB input but need a gray result.
   *
   * If only the blue is set use that, otherwise do normal color transform.
   * Based on the perceived contribution to the brightness.
   */
  def blueOrRgbToGray(red: Int, green: Int, blue: Int): Int = {
    if (0 == red && 0 == green)
      blue
    else
      (30 * red + 59 * green + 11 * blue) / 100
  }

  def colorToString(color: Int, rgb: Boolean): String = {
    var result: String = null
    if (rgb) {
      result = "" + splitRed(color) + ", " + splitGreen(color) + ", " + splitBlue(color)
    } else
      result = "" + color
    result
  }
}
