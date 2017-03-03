package org.shapelogic.sc.imageprocessing


import org.scalatest._
import org.shapelogic.sc.factory.ImageLoad

/**
 *
 * @author Sami Badawi
 *
 */
class PriorityBasedPixelTypeFinderSpec extends FunSuite with BeforeAndAfterEach {

  val _fileFormat: String = ".gif"
  lazy val imageLoad = new ImageLoad(
    baseDir = "./src/test/resources/data/images",
    imageDir = "smallThinShapes",
    _fileFormat = ".gif",
    useJavaFXImage = false)

  test("ShortVerticalAndHorizontal") {
    val fileName = "verticalAndHorizontal"
    val image = imageLoad.loadBufferImage(imageLoad.filePath(fileName))
    assertResult(20) { image.width }
    val pixel = image.getChannel(0, 0, 0)
    assertResult(PixelType.BACKGROUND_POINT.color) { pixel }
    val priorityBasedPixelTypeFinder = new PriorityBasedPixelTypeFinder(image)
    val indexOfL = image.getIndex(1, 17)
    val reusedPixelTypeCalculator = new PixelTypeCalculator()
    val pixelTypeCalculator = priorityBasedPixelTypeFinder.findPointType(indexOfL, reusedPixelTypeCalculator)
    val pixelTypeOfL = pixelTypeCalculator.getPixelType()
    assertResult(PixelType.PIXEL_L_CORNER) { pixelTypeOfL }
  }
}