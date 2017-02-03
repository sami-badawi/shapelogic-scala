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
 */
class SBSegmentation(
  val bufferImage: BufferImage[Byte],
  roi: Option[Rectangle],
  maxDistance: Int = 10)
    extends Iterator[Seq[SBPendingVertical]] with HasBufferImage[Byte] {

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
  val pixelDistance = new PixelDistance(bufferImage, maxDistance)

  val _segmentAreaFactory: ValueAreaFactory = ColorAreaFactory // XXX should be dynamic
  var _currentSegmentArea: IColorAndVariance = new ColorAndVariance(numBands)

  var _status: String = ""
  var _slowTestMode: Boolean = true

  var _nextX: Int = _max_x
  var _nextY: Int = _min_y - 1

  val currentSBPendingVerticalBuffer: ArrayBuffer[SBPendingVertical] = new ArrayBuffer()
  var _currentArea: Int = 0
  var _referenceColor: Array[Byte] = Array.fill[Byte](numBands)(0) //was Int = 0
  var _paintColor: Array[Byte] = Array.fill[Byte](numBands)(-1) //was Int = -1

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

  def markPixelHandled(x: Int, y: Int): Unit = {
    handledPixelImage.setChannel(x, y, 0, true)
  }

  def newSimilarIndex(index: Int): Boolean = {
    return !pixelIsHandledIndex(index) && pixelDistance.similar(index)
  }

  def newSimilar(x: Int, y: Int): Boolean = {
    pixelIsHandled(x, y) && pixelDistance.similar(x, y)
  }

  /**
   * Given a point find the longest line vertical line similar to the chosen colors.
   * <br />
   * If the start point does not match return null.<br />
   *
   * @param x
   * @param y
   * @return this does not contain any up or down information
   */
  def expandSBPendingVertical(lineIn: SBPendingVertical): SBPendingVertical = {
    val y = lineIn.y
    val offset = offsetToLineStart(y)
    if (newSimilar(lineIn.xMin, y) ||
      newSimilar(lineIn.xMax, y))
      return lineIn; // this should never happen
    var i_low: Int = lineIn.xMin - 1
    var stopMin = false
    while (_min_x <= i_low) {
      if (newSimilar(i_low, y)) {
        i_low += 1
        stopMin = true
      }
      i_low -= 1
    }
    var i_high: Int = lineIn.xMax + 1
    var stopMax = false
    while (_max_x >= i_high && !stopMax) {
      if (newSimilar(i_high, y)) {
        i_high -= 1
        stopMax = true
      }
      i_high += 1
    }
    val x1 = Math.max(_min_x, i_low)
    val x2 = Math.min(_max_x, i_high)
    val newLine: SBPendingVertical = new SBPendingVertical(x1, x2, lineIn.y, lineIn.searchUp)
    newLine
  }

  def segmentAll(): Unit = {
    cfor(_min_x)(_ <= _max_x, _ + 1) { x =>
      cfor(_min_y)(_ <= _max_y, _ + 1) { y =>
        if (!pixelIsHandled(x, y)) {
          pixelDistance.setPoint(x, y)
          segment(x, y, false);
        }
      }
    }
  }

  /**
   * Set every pixel that has the input color, regardless of connectivity.<br />
   *
   * @param color
   */
  def segmentAll(color: Array[Byte]): Unit = {
    _referenceColor = color
    pixelDistance.setReferencePointArray(color)
    cfor(_min_y)(_ <= _max_y, _ + 1) { y =>
      var lineStart = pointToIndex(0, y)
      cfor(_min_x)(_ <= _max_x, _ + 1) { x =>
        var index = lineStart + x
        if (!pixelIsHandled(x, y) &&
          pixelDistance.similar(index)) {
          segment(x, y, true)
        }
      }
    }
  }

  /**
   * Start segmentation by selecting a point
   *
   * Use the color of that point at your goal color
   *
   * @param x
   * @param y
   */
  def segment(x: Int, y: Int, useReferenceColor: Boolean): Seq[SBPendingVertical] = {
    currentSBPendingVerticalBuffer.clear()
    if (!newSimilar(x, y)) {
      _status = "Error: First pixel did not match. Segmentation is empty.";
      return Seq()
    }

    //Init
    _currentArea = 0
    var effectiveColor = _referenceColor;
    if (!useReferenceColor)
      effectiveColor = pixelDistance.setPoint(x, y)
    else
      _referenceColor
    _currentSegmentArea = _segmentAreaFactory.makePixelArea(x, y, effectiveColor)
    val firstLine: SBPendingVertical = new SBPendingVertical(x, y)
    if (firstLine == null) {
      println(s"Error in segment")
      return Seq()
    }

    val paintAndCheck = handleLineExpand(firstLine)
    var paintLines: Seq[SBPendingVertical] = paintAndCheck.paintLines
    paintAndCheck.checkLines.foreach(checkLine => storeLine(checkLine))
    val maxIterations = 1000 + bufferImage.pixelCount / 10
    if (currentSBPendingVerticalBuffer.size != 0) {
      cfor(1)(_ <= maxIterations && !currentSBPendingVerticalBuffer.isEmpty, _ + 1) { i =>
        val curLine = currentSBPendingVerticalBuffer.last
        currentSBPendingVerticalBuffer.remove(currentSBPendingVerticalBuffer.size - 1)
        val paintAndCheck2 = handleLineExpand(firstLine)
        paintLines ++= paintAndCheck2.paintLines
        paintAndCheck2.checkLines.foreach(checkLine => storeLine(checkLine))
      }
    }
    paintSegment(paintLines, effectiveColor)
    val area = Try(_currentSegmentArea.getPixelArea().getArea()).getOrElse(0) //XXX
    paintLines
  }

  /** line is at the edge of image and pointing away from the center	 */
  def atEdge(curLine: SBPendingVertical): Boolean = {
    if (curLine.y == _max_y && curLine.searchUp)
      return true;
    if (curLine.y == _min_y && !curLine.searchUp)
      return true;
    false;
  }

  def isExpandable(curLine: SBPendingVertical): Boolean = {
    val y = curLine.y
    val offset = offsetToLineStart(curLine.y)
    if (_min_x <= curLine.xMin - 1) {
      val indexLeft = offset + curLine.xMin - 1;
      if (newSimilar(curLine.xMin - 1, y)) {
        return true
      }
    }
    if (_max_x >= curLine.xMax + 1) {
      val indexRight = offset + curLine.xMax + 1;
      if (newSimilar(curLine.xMax + 1, y)) {
        return true
      }
    }
    false
  }

  /** If the whole line is handled */
  def lineIsHandled(curLine: SBPendingVertical): Boolean = {
    val offset = offsetToLineStart(curLine.y);
    cfor(curLine.xMin)(_ <= curLine.xMax, _ + 1) { i =>
      if (!pixelIsHandled(i, curLine.y)) {
        return false;
      }
    }
    true
  }

  val doAction: Boolean = true
  /**
   * This used for changes to other images or say modify all colors
   * to the first found.
   */
  val red: Array[Byte] = Array(-1, 50, 50, -1)
  var actionCount = 0
  def action(index: Int): Unit = {
    if (doAction) {
      if (pixelDistance.similar(index)) {
        outputImage.setPixel(x = index, y = 0, value = red) //pixelDistance.referencePointI)
      }
    }
  }

  def action(x: Int, y: Int): Unit = {
    if (!doAction)
      return ;
    if (pixelDistance.similar(x, y)) {
      outputImage.setPixel(x = x, y = y, value = red) //pixelDistance.referencePointI)
    }
    actionCount += 1
    if (actionCount % 100 == 0)
      println(s"actionCount: $actionCount")
  }

  case class PaintAndCheckLines(paintLines: Seq[SBPendingVertical], checkLines: Seq[SBPendingVertical])

  /**
   * Call action on the line itself and then setHandled, so it will not
   * be run again.
   *
   * @param curLine, containing the current line, that is already found
   */
  def handleLine(potentialLine: SBPendingVertical): Seq[SBPendingVertical] = {
    var y = potentialLine.y
    var paintLine: SBPendingVertical = null
    var paintBuffer = scala.collection.mutable.ArrayBuffer[SBPendingVertical]()
    cfor(potentialLine.xMin)(_ <= potentialLine.xMax, _ + 1) { i =>
      if (newSimilar(i, y)) {
        if (paintLine == null) {
          paintLine = SBPendingVertical(i, i, y, potentialLine.searchUp)
        } else {
          paintLine = paintLine.copy(xMax = paintLine.xMax + 1)
        }
        markPixelHandled(x = i, y = y)
        if (_currentSegmentArea != null)
          _currentSegmentArea.putPixel(i, y, pixelDistance.setPoint(i, y))
        _currentSegmentArea.putPixel(i, y, pixelDistance.referencePointI) //XXX maybe this coudl be reference too
        action(i, y)
        _currentArea += 1
      } else {
        paintBuffer.+=(paintLine)
        paintLine = null
      }
    }
    if (paintLine != null)
      paintBuffer.+=(paintLine)
    paintBuffer.toSeq
  }

  def expandLeft(x: Int, y: Int): Seq[SBPendingVertical] = {
    if (!bufferImage.isInBounds(x, y)) return Seq()
    var paintLine: SBPendingVertical = null
    var paintBuffer = scala.collection.mutable.ArrayBuffer[SBPendingVertical]()
    var i = x
    var stop = false
    cfor(x)(0 <= _ && !stop, _ - 1) { i =>
      if (newSimilar(i, y)) {
        if (paintLine == null) {
          paintLine = SBPendingVertical(i, i, y, searchUp = true)
        } else {
          paintLine = paintLine.copy(xMax = paintLine.xMax + 1)
        }
        markPixelHandled(x = i, y = y)
        if (_currentSegmentArea != null)
          _currentSegmentArea.putPixel(i, y, pixelDistance.setPoint(i, y))
        _currentSegmentArea.putPixel(i, y, pixelDistance.referencePointI) //XXX maybe this coudl be reference too
        action(i, y)
        _currentArea += 1
      } else {
        stop = true
        paintBuffer.+=(paintLine)
        paintLine = null
      }
    }
    if (paintLine != null)
      paintBuffer.+=(paintLine)
    paintBuffer.toSeq

  }

  def expandRight(x: Int, y: Int): Seq[SBPendingVertical] = {
    if (!bufferImage.isInBounds(x, y)) return Seq()
    var paintLine: SBPendingVertical = null
    var paintBuffer = scala.collection.mutable.ArrayBuffer[SBPendingVertical]()
    var i = x
    var stop = false
    cfor(x)(_ < _max_x && !stop, _ + 1) { i =>
      if (newSimilar(i, y)) {
        if (paintLine == null) {
          paintLine = SBPendingVertical(i, i, y, searchUp = true)
        } else {
          paintLine = paintLine.copy(xMax = paintLine.xMax + 1)
        }
        markPixelHandled(x = i, y = y)
        if (_currentSegmentArea != null)
          _currentSegmentArea.putPixel(i, y, pixelDistance.setPoint(i, y))
        _currentSegmentArea.putPixel(i, y, pixelDistance.referencePointI) //XXX maybe this coudl be reference too
        action(i, y)
        _currentArea += 1
      } else {
        stop = true
        paintBuffer.+=(paintLine)
        paintLine = null
      }
    }
    if (paintLine != null)
      paintBuffer.+=(paintLine)
    paintBuffer.toSeq
  }

  def handleLineExpand(potentialLine: SBPendingVertical): PaintAndCheckLines = {
    val y = potentialLine.y
    val center = handleLine(potentialLine)
    val right = expandRight(potentialLine.xMax + 1, potentialLine.y)
    val left = expandLeft(potentialLine.xMin - 1, potentialLine.y)

    val yNext = potentialLine.nextY
    val leftRight = right ++ left
    val paintBufferSeq = center ++ right ++ left

    val centerChecklines = if (yNext < _min_y || _max_y < yNext)
      Seq()
    else
      center.map(_.copy(y = yNext))
    var leftRightChecklines: Seq[SBPendingVertical] = Seq()
    val yM1 = y - 1
    val yP1 = y + 1
    if (_min_y <= yM1 && yM1 <= _max_y) {
      leftRightChecklines ++= leftRight.map(_.copy(y = yM1))
    }
    if (_min_y <= yP1 && yP1 <= _max_y) {
      leftRightChecklines ++= leftRight.map(_.copy(y = yP1))
    }
    PaintAndCheckLines(paintBufferSeq, centerChecklines ++ leftRightChecklines)

  }

  /**
   * After handling a line continue in the same direction.
   * Inside
   * @param potentialLine
   */
  def handleNextLine(curLine: SBPendingVertical): Unit = {
    val y = curLine.y
    if (atEdge(curLine))
      return ;
    var insideSimilar = false;
    var lowX = 0;
    var direction = -1; //down
    if (curLine.searchUp)
      direction = 1;
    val yNew = curLine.y + direction;
    if (!(_min_y <= yNew && yNew <= _max_y))
      return ;
    var offset = offsetToLineStart(yNew);
    cfor(curLine.xMin)(_ <= curLine.xMax, _ + 1) { i =>
      val curSimilar = newSimilar(i, y)
      if (!insideSimilar && curSimilar) { //enter
        lowX = i;
        insideSimilar = true;
      } else if (insideSimilar && !curSimilar) { //leave
        val newLine = new SBPendingVertical(lowX, i - 1, yNew,
          curLine.searchUp);
        storeLine(newLine);
        insideSimilar = false;
      }
    }
    if (insideSimilar) {
      val newLine = new SBPendingVertical(lowX, curLine.xMax, yNew,
        curLine.searchUp);
      storeLine(newLine);
    }
  }

  def fullLineTreatment(curLineIn: SBPendingVertical): Unit = {
    if (curLineIn == null)
      return
    var curLine = curLineIn
    if (isExpandable(curLine)) {
      var expanded: SBPendingVertical = expandSBPendingVertical(curLine)
      //check that new line is still good
      if (!_slowTestMode || !checkLine(expanded)) {
        expanded = expandSBPendingVertical(curLine)
      }
      curLine = expanded
      storeLine(SBPendingVertical.opposite(expanded))
    }
    handleLine(curLine)
    try {
      handleNextLine(curLine);
    } catch {
      case ex: Throwable => {
        ex.printStackTrace()
      }
    }
  }

  /**
   * @return Returns the status.
   */
  def getStatus(): String = {
    if (_status == null || "".equals(_status))
      _status = findStatus();
    return _status;
  }

  def findStatus(): String = {
    var status = "";
    if (_segmentAreaFactory != null) {
      val areas = _segmentAreaFactory.getStore().size
      status += "Numbers of areas = " + areas;
      if (0 < areas)
        status += "\nPixels per area = " + bufferImage.pixelCount / areas;
      else
        status += ", segmentation was not run.";
    }
    return status;
  }

  /** Make sure that every point on curLine is similar the the chosen color */
  def checkLine(curLine: SBPendingVertical): Boolean =
    {
      val offset = offsetToLineStart(curLine.y)
      var problem = false
      var stop = false
      cfor(curLine.xMin)(_ <= curLine.xMax & !stop, _ + 1) { i =>
        if (pixelDistance.similar(offset + i))
          stop = true
        else {
          val handledBefore = pixelDistance.similar(offset + i); //for debugging
          problem = true;
        }
      }
      return !problem;
    }

  def storeLine(curLine: SBPendingVertical): Unit = {
    if (_slowTestMode && !checkLine(curLine))
      checkLine(curLine); //for debugging
    currentSBPendingVerticalBuffer.+=(curLine)
  }

  override def hasNext(): Boolean = {
    if (_nextY < _max_y)
      true
    else if (_nextY == _max_y && _nextX < _max_x)
      true
    else
      false;
  }

  override def next(): Seq[SBPendingVertical] = {
    while (true) {
      if (!hasNext())
        return null;
      if (_nextX < _max_x)
        _nextX += 1
      else {
        _nextY += 1
        _nextX = _min_x;
      }
      if (!pixelIsHandled(_nextX, _nextY)) {
        segment(_nextX, _nextY, true);
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
          cfor(line.xMin)(_ <= line.xMax, _ + 1) { i =>
            outputImage.setPixel(i, line.y, paintColor);
          }
        }
      }
    }
  }

  val driveAsIterator = false
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
  def transform(inputImage: BufferImage[Byte]): BufferImage[Byte] = {
    val segment = new SBSegmentation(inputImage, None)
    segment.result
  }
}