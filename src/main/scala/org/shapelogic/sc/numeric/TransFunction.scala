package org.shapelogic.sc.numeric

import simulacrum._

@typeclass trait TransFunction[I] {
  def transform(input: I): I
}