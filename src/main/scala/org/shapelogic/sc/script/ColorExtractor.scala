package org.shapelogic.sc.script

import org.shapelogic.sc.util.Args
import org.shapelogic.sc.io.LoadImage
import org.shapelogic.sc.io.LoadBufferImage

/**
 * Example script taking input image and x and y and outputting color values
 */
object ColorExtractor {

  def findColorAtXY(filename: String, x: Int, y: Int): Unit = {
    val imageOpt = LoadImage.loadAWTBufferedImage(filename).toOption
    imageOpt match {
      case Some(image) => {
        val wrappedOpt = LoadBufferImage.awtBufferedImage2BufferImage(image)
        wrappedOpt match {
          case Some(wrapped) => {
            val pointRGB = wrapped.getPixel(x, y).toSeq
            println(s"Image loaded. RGB: $pointRGB")
          }
          case None => println("Image could not be processed")
        }
      }
      case None => println("Image could not be loaded")
    }
  }

  def errorMessage(): Unit = {
    Args.parser
  }

  def main(args: Array[String]): Unit = {
    println("script taking input image and x and y and outputting color values")
    val paramOpt = Args.parser.parse(args, Args())
    paramOpt match {
      case Some(param) => {
        val filename = param.input
        val x = param.x
        val y = param.y
        if (!filename.isEmpty) {
          val zeros = findColorAtXY(filename, x, y)
          println(s"color at point x: $x, y: $y in $filename")
        } else {
          errorMessage()
        }
      }
      case None => errorMessage()
    }
  }
}