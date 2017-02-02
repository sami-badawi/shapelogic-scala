package org.shapelogic.sc.imageprocessing

import spire.implicits._
import org.shapelogic.sc.image.BufferImage
import java.awt.Rectangle
import scala.collection.mutable.ArrayBuffer
import org.shapelogic.sc.color.ValueAreaFactory
import org.shapelogic.sc.color.IColorAndVariance
import org.shapelogic.sc.color.GrayAreaFactory
import org.shapelogic.sc.color.GrayAndVariance
import org.shapelogic.sc.image.BufferBooleanImage
import org.shapelogic.sc.pixel.PixelDistance
import org.shapelogic.sc.pixel.PixelHandlerMax
import org.shapelogic.sc.numeric.PrimitiveNumberPromotersAux

/**
 * Image segmentation
 * Ported from ShapeLogic Java
 */
class SBSegmentation(
  val bufferImage: BufferImage[Byte],
  roi: Option[Rectangle],
  maxDistance: Int = 10)
    extends Iterator[Seq[SBPendingVertical]] {

  lazy val outputImage: BufferImage[Byte] = bufferImage.empty()

  /**
   * true means that a pixel is handled
   */
  lazy val handledPixelImage = new BufferBooleanImage(bufferImage.width, bufferImage.height, 1)

  val _vPV: ArrayBuffer[SBPendingVertical] = new ArrayBuffer()
  /** Dimensions of ROI. */
  val _min_x: Int = roi.map(_.x).getOrElse(0)
  val _max_x: Int = roi.map(_.width).getOrElse(bufferImage.width - 1)
  val _min_y: Int = roi.map(_.y).getOrElse(0)
  val _max_y: Int = roi.map(_.height).getOrElse(bufferImage.height - 1)

  val _pixelCompare: SBPixelCompare = new SBByteCompare(bufferImage) //XXX fix

  import PrimitiveNumberPromotersAux.AuxImplicit._
  val pixelDistance = new PixelDistance(bufferImage, maxDistance)

  var _segmentAreaFactory: ValueAreaFactory = new GrayAreaFactory() // XXX should be dynamic
  var _currentSegmentArea: IColorAndVariance = new GrayAndVariance() // XXX should be dynamic

  var _status: String = ""
  var _slowTestMode: Boolean = false;
  var _farFromReferenceColor: Boolean = false;

  var _nextX: Int = _max_x
  var _nextY: Int = _min_y - 1

  var _currentList: ArrayBuffer[SBPendingVertical] = new ArrayBuffer()
  var _currentArea: Int = 0
  var _referenceColor: Array[Byte] = null //was Int = 0
  var _paintColor: Array[Byte] = null //was Int = -1

  /**
   * Conviniens method to get the offset from the start of the image
   * array to the first pixel of a line, at the edge of the image
   * not the edge of to ROI.
   *
   * @param y
   * @return
   */
  def offsetToLineStart(y: Int): Int = {
    val width: Int = bufferImage.width
    val offset = y * width
    offset
  }

  def pointToIndex(x: Int, y: Int): Int = {
    bufferImage.getIndex(x, y)
  }

  def pixelIsHandled(index: Int): Boolean = {
    handledPixelImage.getChannel(x = index, y = 0, ch = 0) //XXX better way
  }

  def newSimilar(index: Int): Boolean = {
    return !pixelIsHandled(index) && pixelDistance.similar(index)
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
    val offset = offsetToLineStart(lineIn.y)
    if (newSimilar(offset + lineIn.xMin) ||
      newSimilar(offset + lineIn.xMax))
      return lineIn; // this should never happen
    var i_low: Int = lineIn.xMin - 1
    var stopMin = false
    while (_min_x <= i_low) {
      if (newSimilar(offset + i_low)) {
        i_low += 1
        stopMin = true
      }
      i_low -= 1
    }
    var i_high: Int = lineIn.xMax + 1
    var stopMax = false
    while (_max_x >= i_high && !stopMax) {
      if (newSimilar(offset + i_high)) {
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
        if (!pixelIsHandled(pointToIndex(x, y))) {
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
        if (!pixelIsHandled(index) &&
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
  def segment(x: Int, y: Int, useReferenceColor: Boolean): Unit = {
    _currentList = new ArrayBuffer()
    _currentArea = 0
    var index = pointToIndex(x, y)
    var effectiveColor = _referenceColor;
    if (!useReferenceColor)
      //      effectiveColor = _pixelCompare.getColorAsInt(index);
      effectiveColor = pixelDistance.setIndexPoint(index)
    if (_segmentAreaFactory != null)
      _currentSegmentArea = _segmentAreaFactory.makePixelArea(x, y, effectiveColor)
    if (newSimilar(index)) {
      _status = "First pixel did not match. Segmentation is empty.";
      return
    }
    val firstLine: SBPendingVertical = expandSBPendingVertical(new SBPendingVertical(x, y))
    if (firstLine == null)
      return ;
    storeLine(firstLine)
    storeLine(SBPendingVertical.opposite(firstLine));
    val maxIterations = 1000 + bufferImage.pixelCount / 10
    if (_vPV.size != 0) {
      cfor(1)(_ <= maxIterations, _ + 1) { i =>
        val obj = _vPV.last
        _vPV.remove(_vPV.size - 1)
        val curLine: SBPendingVertical = obj.asInstanceOf[SBPendingVertical]
        fullLineTreatment(curLine)
      }
    }
    //        if (useReferenceColor)
    //            paintSegment(_currentList,_paintColor);
    _pixelCompare.getNumberOfPixels();
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
    val offset = offsetToLineStart(curLine.y)
    if (_min_x <= curLine.xMin - 1) {
      val indexLeft = offset + curLine.xMin - 1;
      if (newSimilar(indexLeft)) {
        return true
      }
    }
    if (_max_x >= curLine.xMax + 1) {
      val indexRight = offset + curLine.xMax + 1;
      if (newSimilar(indexRight)) {
        return true
      }
    }
    false
  }

  /** If the whole line is handled */
  def pixelIsHandled(curLine: SBPendingVertical): Boolean = {
    val offset = offsetToLineStart(curLine.y);
    cfor(curLine.xMin)(_ <= curLine.xMax, _ + 1) { i =>
      if (!pixelIsHandled(offset + i)) {
        return false;
      }
    }
    true
  }

  /**
   * Call action on the line itself and then setHandled, so it will not
   * be run again.
   *
   * @param curLine, containing the current line, that is already found
   */
  def handleLine(curLine: SBPendingVertical): Unit = {
    val offset = offsetToLineStart(curLine.y);
    var y = curLine.y
    var stop = false
    cfor(curLine.xMin)(!stop && _ <= curLine.xMax, _ + 1) { i =>
      if (pixelIsHandled(offset + i))
        stop = true
      else {
        if (!pixelIsHandled(offset + i)) {
          _pixelCompare.action(offset + i)
          _currentArea += 1
          handledPixelImage.setChannel(x = offset + i, y = 0, ch = 0, true)
          if (_currentSegmentArea != null)
            _currentSegmentArea.putPixel(i, y, pixelDistance.setIndexPoint(offset + i))
        }
      }
    }
  }

  /**
   * After handling a line continue in the same direction.
   *
   * @param curLine
   */
  def handleNextLine(curLine: SBPendingVertical): Unit = {
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
      val curSimilar = newSimilar(offset + i)
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
    _currentList.+=(curLine)
    _vPV.append(curLine)
  }

  def setSegmentAreaFactory(areaFactory: ValueAreaFactory): Unit = {
    _segmentAreaFactory = areaFactory;
  }

  def getSegmentAreaFactory(): ValueAreaFactory = {
    _segmentAreaFactory
  }

  def isFarFromReferencColor(): Boolean = {
    return _farFromReferenceColor;
  }

  def setFarFromReferencColor(farFromColor: Boolean): Unit = {
    _farFromReferenceColor = farFromColor;
    _pixelCompare.setFarFromReferencColor(farFromColor);
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
      if (!pixelIsHandled(pointToIndex(_nextX, _nextY))) {
        segment(_nextX, _nextY, true);
        return _currentList;
      }
    }
    Seq() //XXX should never happen
  }

  def setReferenceColor(referenceColor: Array[Byte]): Unit = {
    _referenceColor = referenceColor;
  }

  def getCurrentArea(): Int = {
    return _currentArea;
  }

  /**
   * XXX Currently a mutable update
   * Should be changed
   */
  def paintSegment(lines: Seq[SBPendingVertical], paintColor: Int): Unit = {
    if (null != lines) {
      lines.foreach { (line: SBPendingVertical) =>
        {
          cfor(line.xMin)(_ <= line.xMax, _ + 1) { i =>
            val array: Array[Byte] = null // paintColor
            outputImage.setPixel(i, line.y, array);
          }
        }
      }
    }
  }
}