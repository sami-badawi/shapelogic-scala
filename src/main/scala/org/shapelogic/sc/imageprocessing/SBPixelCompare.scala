package org.shapelogic.sc.imageprocessing

import org.shapelogic.sc.image.BufferImage

/** 
 *  Ported from ShapeLogic Java
 *  XXX Need changes
 *  
 *  Interface for doing pixel compare.
 * 
 * In order to handle both colors and gray scale have an Interface that tells 
 * if 2 pixels are similar.
 *
 * @author Sami Badawi
 */
trait SBPixelCompare {
  
  def bufferImage: BufferImage[Byte]
	
	/** Test if a pixel at index is similar to the Internal state
	 * 
	 * @param index to image
	 */
	def similar(index: Int): Boolean
	def newSimilar(index: Int): Boolean
	def action(index: Int)
	def isHandled(index: Int): Boolean
	def setHandled(index: Int): Unit
	def colorDistance( color1: Int,  color2: Int): Int
	def getNumberOfPixels(): Int
	def grabColorFromPixel( startX: Int,  startY: Int)
	
	/** Should pixels be modified. */
	def isModifying(): Boolean
	def setModifying( input: Boolean): Unit
	def getColorAsInt(index: Int): Int
	def setCurrentColor(color: Int): Unit
  def setMaxDistance( maxDistance: Int): Unit
  def isFarFromReferencColor(): Boolean
  def setFarFromReferencColor( farFromColor: Boolean)
}