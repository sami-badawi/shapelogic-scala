package org.shapelogic.sc.imageprocessing

import spire.implicits._
import org.shapelogic.sc.image.BufferImage
import java.awt.Rectangle
import scala.collection.mutable.ArrayBuffer
import org.shapelogic.sc.color.ValueAreaFactory
import org.shapelogic.sc.color.IColorAndVariance
import org.shapelogic.sc.color.GrayAreaFactory
import org.shapelogic.sc.color.GrayAndVariance
import org.shapelogic.sc.color.ColorAreaFactory
import org.shapelogic.sc.color.ColorAndVariance
import org.shapelogic.sc.image.BufferBooleanImage
import org.shapelogic.sc.pixel.PixelDistance
import org.shapelogic.sc.pixel.PixelHandlerMax
import org.shapelogic.sc.numeric.PrimitiveNumberPromotersAux
import scala.util.Try
import org.shapelogic.sc.pixel.PixelDistance
import org.shapelogic.sc.image.HasBufferImage

/**
 * Image segmentation
 * Ported from ShapeLogic Java
 *
 * This is mainly a flood fill that has better properties
 * It is not generic yet
 */
class SBSegmentation(
  val bufferImage: BufferImage[Byte],
  roi: Option[Rectangle],
  maxDistance: Int = 10)
    extends Iterator[Seq[SBPendingVertical]] with HasBufferImage[Byte] {
  import SBSegmentation._

  // ================= parameters =================
  val doAction: Boolean = false
  val driveAsIterator = false
  val _slowTestMode: Boolean = true

  // ================= lazy init =================

  lazy val outputImage: BufferImage[Byte] = bufferImage.empty()
  lazy val numBands = bufferImage.numBands
  lazy val height = bufferImage.height
  lazy val width = bufferImage.width
  lazy val stride = bufferImage.stride

  /**
   * true means that a pixel is handled
   */
  lazy val handledPixelImage = new BufferBooleanImage(bufferImage.width, bufferImage.height, 1)

  /** Dimensions of ROI. */
  val _min_x: Int = roi.map(_.x).getOrElse(0)
  val _max_x: Int = roi.map(_.width).getOrElse(bufferImage.width - 1)
  val _min_y: Int = roi.map(_.y).getOrElse(0)
  val _max_y: Int = roi.map(_.height).getOrElse(bufferImage.height - 1)

  import PrimitiveNumberPromotersAux.AuxImplicit._
  lazy val pixelDistance = new PixelDistance(bufferImage, maxDistance)

  lazy val _segmentAreaFactory: ValueAreaFactory = ColorAreaFactory

  // ================= var init =================

  var actionCount = 0
  var _currentSegmentArea: IColorAndVariance = new ColorAndVariance(numBands)

  var _status: String = ""

  var _nextX: Int = _max_x
  var _nextY: Int = _min_y - 1

  var segmentCount: Int = 0

  val currentSBPendingVerticalBuffer: ArrayBuffer[SBPendingVertical] = new ArrayBuffer()
  var _currentArea: Int = 0
  var _referenceColor: Array[Byte] = Array.fill[Byte](numBands)(0) //was Int = 0
  var _paintColor: Array[Byte] = Array.fill[Byte](numBands)(-1) //was Int = -1

  // ================= util =================
  /**
   * Convenience method to get the offset from the start of the image
   * array to the first pixel of a line, at the edge of the image
   * not the edge of to ROI.
   *
   * @param y
   * @return
   */
  def offsetToLineStart(y: Int): Int = {
    val offset = y * stride
    offset
  }

  def pointToIndex(x: Int, y: Int): Int = {
    bufferImage.getIndex(x, y)
  }

  def pixelIsHandledIndex(index: Int): Boolean = {
    handledPixelImage.getChannel(x = index, y = 0, ch = 0) //XXX better way
  }

  def pixelIsHandled(x: Int, y: Int): Boolean = {
    handledPixelImage.getChannel(x, y, 0)
  }

  def setColorForPoint(x: Int, y: Int): Array[Byte] = {
    pixelDistance.takeColorFromPoint(x, y)
  }

  def markPixelHandled(x: Int, y: Int): Unit = {
    //    println(s"markPixelHandled($x, $y))")
    handledPixelImage.setChannel(x, y, 0, true)
  }

  def newSimilarIndex(index: Int): Boolean = {
    !pixelIsHandledIndex(index) && pixelDistance.similarIndex(index)
  }

  def newSimilar(x: Int, y: Int): Boolean = {
    !pixelIsHandled(x, y) && pixelDistance.similar(x, y)
  }

  def storeLine(curLine: SBPendingVertical): Unit = {
    if (curLine == null) {
      println("Error: storeLine(curLine) for curLine == null")
      return
    }
    if (_slowTestMode && !checkLineIsOK(curLine))
      checkLineIsOK(curLine) //for debugging
    currentSBPendingVerticalBuffer.+=(curLine)
  }

  def storeLines(curLines: Seq[SBPendingVertical]): Unit = {
    val notNull = curLines.filter(_ != null)
    currentSBPendingVerticalBuffer.++=(notNull)
  }

  def popLine(): Option[SBPendingVertical] = {
    val sbPendingVerticalOpt = currentSBPendingVerticalBuffer.lastOption
    if (!currentSBPendingVerticalBuffer.isEmpty)
      currentSBPendingVerticalBuffer.remove(currentSBPendingVerticalBuffer.size - 1)
    sbPendingVerticalOpt
  }

  // ================= debug code =================

  /** Make sure that every point on curLine is similar the the chosen color */
  def checkLineIsOK(curLine: SBPendingVertical): Boolean = {
    val offset = offsetToLineStart(curLine.y)
    var problem = false
    var stop = false
    cfor(curLine.xMin)(_ <= curLine.xMax & !stop, _ + 1) { i =>
      if (pixelDistance.similarIndex(offset + i))
        stop = true
      else {
        val handledBefore = pixelDistance.similarIndex(offset + i) //for debugging
        problem = true
      }
    }
    !problem
  }

  // ================= segment =================

  def segmentAll(): Unit = {
    cfor(_min_y)(_ <= _max_y, _ + 1) { y =>
      cfor(_min_x)(_ <= _max_x, _ + 1) { x =>
        if (!pixelIsHandled(x, y)) {
          segment(x, y, None)
        }
      }
    }
    println(s"segmentAll(): segmentCount: $segmentCount")
  }

  /**
   * Start segmentation by selecting a point
   *
   * Use the color of that point at your goal color
   *
   * @param referenceColorOpt if you want to start segmentation not on the first point but on a color
   *
   * @return lines that belongs to what should be printed
   */
  def segment(x: Int, y: Int, referenceColorOpt: Option[Array[Byte]]): Seq[SBPendingVertical] = {
    currentSBPendingVerticalBuffer.clear()
    val effectiveColor = referenceColorOpt.getOrElse(pixelDistance.takeColorFromPoint(x, y))
    _referenceColor = effectiveColor

    if (pixelIsHandled(x, y)) {
      _status = "Error: First pixel did not match. Segmentation is empty."
      //      println(_status)
      return Seq()
    }
    //    println(s"============ effectiveColor for ($x,$y):  " + effectiveColor.toSeq)

    //Init
    _currentArea = 0
    _currentSegmentArea = _segmentAreaFactory.makePixelArea(x, y, effectiveColor)
    val firstLine: SBPendingVertical = new SBPendingVertical(x, y)
    if (firstLine == null) {
      println(s"Error in segment")
      return Seq()
    }

    val paintAndCheck = handleLineExpand(firstLine)
    //    println(s"paintAndCheck for $x, $y buffer: ${currentSBPendingVerticalBuffer.size}: $paintAndCheck")
    var paintLines: Seq[SBPendingVertical] = paintAndCheck.paintLines
    storeLines(paintAndCheck.checkLines)
    val maxIterations = 1000 + bufferImage.pixelCount / 10
    if (currentSBPendingVerticalBuffer.size != 0) {
      cfor(1)(_ <= maxIterations && !currentSBPendingVerticalBuffer.isEmpty, _ + 1) { i =>
        val curLineOpt = popLine()
        if (!curLineOpt.isEmpty) {
          val paintAndCheck2 = handleLineExpand(curLineOpt.get)
          paintLines = paintLines ++ paintAndCheck2.paintLines
          storeLines(paintAndCheck2.checkLines)
        }
      }
    }
    paintSegment(paintLines, effectiveColor)
    segmentCount += 1
    val area = Try(_currentSegmentArea.getPixelArea().getArea()).getOrElse(0) //XXX
    paintLines
  }

  /**
   * This used for changes to other images or say modify all colors
   * to the first found.
   */
  def action(index: Int): Unit = {
    if (doAction) {
      if (pixelDistance.similarIndex(index)) {
        outputImage.setPixel(x = index, y = 0, value = pixelDistance.referencePointI)
      }
    }
  }

  def action(x: Int, y: Int): Unit = {
    if (!doAction)
      return
    if (pixelDistance.similar(x, y)) {
      outputImage.setPixel(x = x, y = y, value = pixelDistance.referencePointI)
    }
    actionCount += 1
  }

  /**
   * Call action on the line itself and then setHandled, so it will not
   * be run again.
   *
   * @param curLine, containing the current line, that is already found
   */
  def handlePotentialLine(potentialLine: SBPendingVertical): Seq[SBPendingVertical] = {
    var y = potentialLine.y
    var paintLine: SBPendingVertical = null
    var paintBuffer = scala.collection.mutable.ArrayBuffer[SBPendingVertical]()
    if (!newSimilar(potentialLine.xMin, y)) {
      val isNew = !pixelIsHandled(potentialLine.xMin, y)
      val refColor = pixelDistance.referencePointI.toSeq
      val distance = pixelDistance.similar(potentialLine.xMin, y)
      //      println(s"first point in line is not newSimilar: isNew: $isNew, refColor: $refColor, distance: $distance")
    }
    cfor(potentialLine.xMin)(_ <= potentialLine.xMax, _ + 1) { i =>
      if (newSimilar(i, y)) {
        if (paintLine == null) {
          //          println(s"create new SBPendingVertical ($i,$y)")
          paintLine = SBPendingVertical(i, i, y, potentialLine.searchUp)
        } else {
          paintLine = paintLine.copy(xMax = i)
        }
        markPixelHandled(x = i, y = y)
        if (_currentSegmentArea != null)
          _currentSegmentArea.putPixel(i, y, _referenceColor)
        _currentSegmentArea.putPixel(i, y, pixelDistance.referencePointI) //XXX maybe this could be reference too
        action(i, y)
        _currentArea += 1
      } else {
        if (paintLine != null) {
          paintBuffer.+=(paintLine)
          paintLine = null
        }
      }
    }
    if (paintLine != null)
      paintBuffer.+=(paintLine)
    paintBuffer.toSeq
  }

  def expandLeft(x: Int, y: Int): Option[SBPendingVertical] = {
    if (!bufferImage.isInBounds(x, y)) return None
    if (pixelIsHandled(x, y)) {
      //      println(s"expandLeft stopped for: pixelIsHandled($x, $y)")
      return None
    }
    var paintLine: SBPendingVertical = null
    var paintBuffer = scala.collection.mutable.ArrayBuffer[SBPendingVertical]()
    var i = x
    cfor(x)(_min_x <= _, _ - 1) { i =>
      if (newSimilar(i, y)) {
        markPixelHandled(x = i, y = y)
        if (paintLine == null) {
          paintLine = SBPendingVertical(i, i, y, searchUp = true)
        } else {
          paintLine = paintLine.copy(xMin = i)
        }
        if (_currentSegmentArea != null)
          _currentSegmentArea.putPixel(i, y, _referenceColor)
        action(i, y)
        _currentArea += 1
      } else {
        return Option[SBPendingVertical](paintLine)
      }
    }
    Option[SBPendingVertical](paintLine)
  }

  def expandRight(x: Int, y: Int): Option[SBPendingVertical] = {
    if (!bufferImage.isInBounds(x, y)) return None
    if (pixelIsHandled(x, y)) {
      //      println(s"expandRight stopped for: pixelIsHandled($x, $y)")
      return None
    }
    var paintLine: SBPendingVertical = null
    var paintBuffer = scala.collection.mutable.ArrayBuffer[SBPendingVertical]()
    var i = x
    cfor(x)(_ <= _max_x, _ + 1) { i =>
      if (newSimilar(i, y)) {
        markPixelHandled(x = i, y = y)
        if (paintLine == null) {
          paintLine = SBPendingVertical(i, i, y, searchUp = true)
        } else {
          paintLine = paintLine.copy(xMax = i)
        }
        if (_currentSegmentArea != null)
          _currentSegmentArea.putPixel(i, y, _referenceColor)
        action(i, y)
        _currentArea += 1
      } else {
        return Option(paintLine)
      }
    }
    Option(paintLine)
  }

  def handleLineExpand(potentialLine: SBPendingVertical): PaintAndCheckLines = {
    val y = potentialLine.y
    val center = handlePotentialLine(potentialLine)
    val rightOpt = expandRight(potentialLine.xMax + 1, y)
    val leftOpt = expandLeft(potentialLine.xMin - 1, y)

    //XXX not full
    if (center.size == 1) {
      val centerOne = center(0)
      val minX = leftOpt.map(_.xMin).getOrElse(centerOne.xMin)
      val maxX = rightOpt.map(_.xMax).getOrElse(centerOne.xMax)
      val fullLine = center(0).copy(minX, maxX, y, potentialLine.searchUp)
      return makePotentialNeibhbors(potentialLine, Seq(fullLine))
    }

    val left = leftOpt.toSeq
    val right = rightOpt.toSeq
    val yNext = potentialLine.nextY
    val leftRight = right ++ left
    val paintBufferSeq = center ++ right ++ left
    if (paintBufferSeq.isEmpty)
      //      println(s"Warn paintBufferSeq empty for (${potentialLine.xMin}, $y)")
      if (true)
        return makePotentialNeibhbors(potentialLine, paintBufferSeq)

    val centerChecklines = if (yNext < _min_y || _max_y < yNext)
      Seq()
    else
      center.map(_.copy(y = yNext))
    var leftRightChecklines: Seq[SBPendingVertical] = Seq()
    val yM1 = y - 1
    val yP1 = y + 1
    if (_min_y <= yM1 && yM1 <= _max_y) {
      leftRightChecklines ++= leftRight.map(_.copy(y = yM1, searchUp = false))
    }
    if (_min_y <= yP1 && yP1 <= _max_y) {
      leftRightChecklines ++= leftRight.map(_.copy(y = yP1, searchUp = true))
    }
    PaintAndCheckLines(paintBufferSeq, centerChecklines ++ leftRightChecklines)

  }

  def findStatus(): String = {
    var status = ""
    if (_segmentAreaFactory != null) {
      val areas = _segmentAreaFactory.getStore().size
      status += "Numbers of areas = " + areas
      if (0 < areas)
        status += "\nPixels per area = " + bufferImage.pixelCount / areas
      else
        status += ", segmentation was not run."
    }
    return status
  }

  def makePotentialNeibhbors(potentialLine: SBPendingVertical, foundLines: Seq[SBPendingVertical]): PaintAndCheckLines = {
    val y = potentialLine.y
    var checkLinesUp: Seq[SBPendingVertical] = Seq()
    var checkLinesDown: Seq[SBPendingVertical] = Seq()
    val yM1 = y - 1
    val yP1 = y + 1
    if (_min_y <= yM1 && yM1 <= _max_y) {
      checkLinesDown = foundLines.map(_.copy(y = yM1, searchUp = false))
    }
    if (_min_y <= yP1 && yP1 <= _max_y) {
      checkLinesUp = foundLines.map(_.copy(y = yP1, searchUp = true))
    }

    val checkLines: Seq[SBPendingVertical] = checkLinesUp ++ checkLinesDown
    PaintAndCheckLines(foundLines, checkLines)
  }

  override def hasNext(): Boolean = {
    if (_nextY < _max_y)
      true
    else if (_nextY == _max_y && _nextX < _max_x)
      true
    else
      false
  }

  override def next(): Seq[SBPendingVertical] = {
    while (true) {
      if (!hasNext())
        return null
      if (_nextX < _max_x)
        _nextX += 1
      else {
        _nextY += 1
        _nextX = _min_x
      }
      if (!pixelIsHandled(_nextX, _nextY)) {
        segment(_nextX, _nextY, None)
        return currentSBPendingVerticalBuffer
      }
    }
    Seq() //XXX should never happen
  }

  /**
   * XXX Currently a mutable update
   * Should be changed
   */
  def paintSegment(lines: Seq[SBPendingVertical], paintColor: Array[Byte]): Unit = {
    if (null != lines) {
      lines.foreach { (line: SBPendingVertical) =>
        {
          //          println(s"paintSegment line: $line")
          cfor(line.xMin)(_ <= line.xMax, _ + 1) { i =>
            outputImage.setPixel(i, line.y, paintColor)
          }
        }
      }
    }
  }

  /**
   * now this could implement CalcValue trait
   */
  def getValue(): BufferImage[Byte] = {
    var calcIndex = 0
    if (!driveAsIterator) {
      println(s"Start segmentAll()")
      segmentAll()
      println(s"End segmentAll()")
    } else {
      while (hasNext()) {
        next()
        calcIndex += 1
        if (calcIndex % 20 == 0)
          println(s"calcIndex: $calcIndex")
      }
    }
    outputImage
  }

  lazy val result: BufferImage[Byte] = {
    getValue()
  }
}

object SBSegmentation {
  case class PaintAndCheckLines(paintLines: Seq[SBPendingVertical], checkLines: Seq[SBPendingVertical])

  def transform(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    val segment = new SBSegmentation(inputImage, None)
    segment.result
  }

  def makeByteTransform(inputImage: BufferImage[Byte], parameter: String): BufferImage[Byte] = {
    val distance: Int = Try(parameter.trim().toInt).getOrElse(10)
    val thresholdOperation = new SBSegmentation(inputImage, None, distance)
    thresholdOperation.result
  }
}