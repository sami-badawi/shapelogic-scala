package org.shapelogic.sc.io

import org.shapelogic.sc.image.BufferImage
import scala.util.Try
import scala.reflect.ClassTag

/**
 * To make image loading more independent
 */
trait BufferImageFactory[T] {

  def loadBufferImage(filename: String): BufferImage[T]

  def loadBufferImageOpt(filename: String): Option[BufferImage[T]] = {
    loadBufferImageTry(filename).toOption
  }

  def loadBufferImageTry(filename: String): Try[BufferImage[T]] = {
    Try(loadBufferImage(filename))
  }
}