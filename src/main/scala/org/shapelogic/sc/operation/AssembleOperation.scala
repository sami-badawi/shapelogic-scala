package org.shapelogic.sc.operation

import scala.reflect.ClassTag
import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.numeric.TransFunction

object AssembleOperation {

  def makeGenericTransFunctionInstance[T: ClassTag: TransFunction](inputImage: BufferImage[T]): BufferImage[T] = {
    val transFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = transFunction.transform _
    val simpleTransform = new SimpleTransform[T](inputImage)(function)
    simpleTransform.result
  }
}