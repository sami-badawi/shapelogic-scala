package org.shapelogic.sc.numeric

/**
 * There are problems with implicit.
 * You can specify all the primitive types.
 * But you cannot if specify all possible types.
 * If you try to do a generic definition it does not compile.
 * There is a rule saying that with implicit if will look in 
 */
package object fallback {

  def makeFallback[T >: Null](): TransFunction[T] = {
    val res = new TransFunction[T] {
      def transform(input: T): T = {
        input
      }
    }
    res
  }

  implicit lazy val fallbackInverse: TransFunction[_ >: Null ] = makeFallback[AnyRef]()

}