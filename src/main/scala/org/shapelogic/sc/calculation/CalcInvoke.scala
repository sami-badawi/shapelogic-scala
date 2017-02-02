package org.shapelogic.sc.calculation

/** Calc0 is does a lazy calculation of a value, that does not have 
 * any dependencies.
 * 
 * This is what LazyCalc was before.
 * 
 * This is sort of the InputStream0, does the calculation with no input
 * 
 * @author Sami Badawi
 *
 * @param <T>
 */
trait CalcInvoke[T] extends LazyCalc[T] with Calc0[T] {
}
