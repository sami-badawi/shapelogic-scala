package org.shapelogic.sc.numeric

import simulacrum._

/**
 * Functions that are define for different types
 */
@typeclass trait GenericFunction[I] {
  type Res
  def transform(input: I): Res
}

/**
 * "object GenericFunction" is written automatically so is not used here
 */
object GenericFunctionAux {

  type Aux[I, O] = GenericFunction[I] { type Res = O }

}