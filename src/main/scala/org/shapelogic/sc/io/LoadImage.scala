package org.shapelogic.sc.io

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._

object LoadImage {

  def loadFile(filename: String) = {
    try {
      val img = ImageIO.read(new File(filename))
      val height = img.getHeight
      println(s"filename: $filename had height: $height")
    } catch {
      case ex: Throwable => println("Bummer ")
    }
  }

  def main(args: Array[String]): Unit = {
    val filename = args(0)
    loadFile(filename)
  }
}