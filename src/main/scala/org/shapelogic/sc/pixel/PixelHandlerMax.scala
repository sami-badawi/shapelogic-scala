package org.shapelogic.sc.pixel

import org.shapelogic.sc.numeric.NumberPromotion

/**
 * Refinement of PixelHandler
 */
trait PixelHandlerMax[I, C] extends PixelHandler[I, C] {
   override def promoter: NumberPromotion.Aux[I, C]

}