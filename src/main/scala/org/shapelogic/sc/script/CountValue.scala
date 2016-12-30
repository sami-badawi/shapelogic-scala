package org.shapelogic.sc.script

import org.shapelogic.sc.util.Args
import org.shapelogic.sc.io.LoadImage
import org.shapelogic.sc.io.LoadBufferImage

/**
 * Example displaying creation of
 */
object CountValue {

  def countZero(filename: String): Long = {
    val imageOpt = LoadImage.loadAWTBufferedImage(filename).toOption
    imageOpt match {
      case Some(image) => {
        val wrappedOpt = LoadBufferImage.awtBufferedImage2BufferImage(image)
        wrappedOpt match {
          case Some(wrapped) => {
            val pointRGB = wrapped.getPixel(10, 10).toSeq
            println(s"Image loaded. RGB: $pointRGB")
          }
          case None => println("Very strange")
        }
      }
      case None => println("Image could not be loaded")
    }
    0
  }

  def errorMessage(): Unit = {
    Args.parser
  }

  def main(args: Array[String]): Unit = {
    val paramOpt = Args.parser.parse(args, Args())
    paramOpt match {
      case Some(param) => {
        val filename = param.input
        if (!filename.isEmpty) {
          val zeros = countZero(filename)
          println(s"zero count: $zeros in $filename")
        } else {
          errorMessage()
        }
      }
      case None => errorMessage()
    }
  }
}