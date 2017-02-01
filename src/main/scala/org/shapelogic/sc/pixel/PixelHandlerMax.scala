package org.shapelogic.sc.pixel

import org.shapelogic.sc.numeric.NumberPromotionMax

/**
 * Refinement of PixelHandler
 */
trait PixelHandlerMax[I, C] extends PixelHandler[I, C] {
   override def promoter: NumberPromotionMax.Aux[I, C]

}