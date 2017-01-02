package org.shapelogic.sc.script

import org.shapelogic.sc.io.LoadImage
import org.shapelogic.sc.io.BufferedImageConverter
import org.shapelogic.sc.util.Args
import org.shapelogic.sc.operation.ThresholdOperation

/**
 * Read image and do ThresholdOperation
 */
object Threshold {

  def doThreshold(filename: String,
    outFilename: String,
    threshold: Double): Unit = {
    val imageOpt = LoadImage.loadAWTBufferedImage(filename).toOption
    imageOpt match {
      case Some(image) => {
        val bufferImageOpt = BufferedImageConverter.awtBufferedImage2BufferImage(image)
        bufferImageOpt match {
          case Some(bufferImage) => {
            val operation = new ThresholdOperation(bufferImage, threshold)
            val outputImage = operation.result
            println("outputImage created")
            val outPutName = if (outFilename.isEmpty()) "image/output.png" else outFilename
            val imageOpt = BufferedImageConverter.bufferImage2AwtBufferedImage(outputImage)
            imageOpt match {
              case Some(bufferedImage) => {
                LoadImage.saveAWTBufferedImage(bufferedImage, "png", outPutName)
                println("Saved " + outPutName)
              }
              case None => {
                println("Could not convert image")
              }
            }
          }
          case None => println("Image could not be processed")
        }
      }
      case None => println("Image could not be loaded")
    }
  }

  def errorMessage(): Unit = {
    "Failed \n" +
      Args.parser.help("help")
  }

  def main(args: Array[String]): Unit = {
    println("script taking input image and x and y and outputting color values")
    val paramOpt = Args.parser.parse(args, Args())
    paramOpt match {
      case Some(param) => {
        val filename = param.input
        val threshold = param.threshold
        val outFilename = param.output
        if (!filename.isEmpty) {
          val zeros = doThreshold(filename, outFilename, threshold)
        } else {
          errorMessage()
        }
      }
      case None => errorMessage()
    }
  }
}