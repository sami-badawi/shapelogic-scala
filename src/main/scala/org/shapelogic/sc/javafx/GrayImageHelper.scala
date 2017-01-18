package org.shapelogic.sc.javafx

import javafx.scene.image.Image
import java.awt.image.IndexColorModel

/**
 * Somehow it JavaFX Image does not seem to have a 
 */
object GrayImageHelper {

  def getDefaultColorModel(): IndexColorModel = {
    val r: Array[Byte] = new Array[Byte](256)
    val g: Array[Byte] = new Array[Byte](256)
    val b: Array[Byte] = new Array[Byte](256)
    for (i <- Range(0, 256)) {
      r(i) = i.toByte
      g(i) = i.toByte
      b(i) = i.toByte
    }
    val defaultColorModel: IndexColorModel = new IndexColorModel(8, 256, r, g, b)
    defaultColorModel
  }

  def gray2Image(bytearray: Array[Byte], width: Int, height: Int): Image = {
    null
  }
}