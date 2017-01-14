package org.shapelogic.sc.numeric

/**
 * Functions that are define for different types
 */
trait GenericFunction[I] {
  type Res
  def transform(input: I): Res
}

object GenericFunction {

  type Aux[I, O] = GenericFunction[I] { type Res = O }

}