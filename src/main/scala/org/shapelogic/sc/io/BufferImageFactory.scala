package org.shapelogic.sc.io

import org.shapelogic.sc.image.BufferImage
import scala.util.Try
import scala.reflect.ClassTag

trait BufferImageFactory[T] {

  def loadBufferImageOpt(filename: String): Option[BufferImage[T]] = {
    loadBufferImageTry(filename).toOption
  }

  def loadBufferImageTry(filename: String): Try[BufferImage[T]]
}