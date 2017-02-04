package org.shapelogic.sc.javafx

import org.shapelogic.sc.image.BufferImage
import javafx.scene.image.Image

/**
 * There are 2 image types
 * Since image seems to be saving in RGBA
 * And for some operations it is important that it is a specific type
 * The idea is that you will take the best image type for what you need.
 *
 * So if you need to run an operation on an ImageAndFilename and it already
 * is present in bufferImage format you do not need to translate it first.
 */
case class ImageAndFilename(
    bufferImage: BufferImage[_],
    image: Image,
    url: String) {

  def getWithBufferImage(): ImageAndFilename = {
    if (bufferImage != null) {
      this
    } else if (image != null) {
      val bufferImageNew = LoadJFxImage.jFxImage2BufferImage(image)
      this.copy(bufferImage = bufferImageNew)
    } else {
      println(s"Not good image and bufferImage both missing. Return null")
      null
    }
  }

  def getWithImage(): ImageAndFilename = {
    if (image != null) {
      this
    } else if (bufferImage != null) {
      val imageNew = LoadJFxImage.bufferImage2jFxImage(bufferImage.asInstanceOf[BufferImage[Byte]])
      this.copy(image = imageNew)
    } else {
      println(s"Not good image and bufferImage both missing. Return null")
      null
    }
  }
}