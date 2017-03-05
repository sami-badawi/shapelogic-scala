package org.shapelogic.sc.old

import org.shapelogic.sc.numeric.NumberPromotion
import org.shapelogic.sc.pixel.PixelHandler

/**
 * Refinement of PixelHandler
 */
trait PixelHandlerMax[I, C] extends PixelHandler[I, C] {
   override def promoter: NumberPromotion.Aux[I, C]

}