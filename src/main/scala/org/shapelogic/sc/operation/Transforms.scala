package org.shapelogic.sc.operation

import spire.math.Numeric
import spire.implicits._

import org.shapelogic.sc.image.BufferImage
import org.shapelogic.sc.numeric._
import org.shapelogic.sc.pixel._
import spire.math._
import spire.implicits._
import scala.reflect.ClassTag
import org.shapelogic.sc.numeric.GenericInverse
import org.shapelogic.sc.numeric.GenericFunction
//import org.shapelogic.sc.numeric.GenericFunction._
import org.shapelogic.sc.image.ImageTransformWithName
import org.shapelogic.sc.image.ImageTransformWithNameT
import org.shapelogic.sc.image.ImageTransformDialog
import org.shapelogic.sc.image.ImageTransformDialogT
import org.shapelogic.sc.operation.implement.ImageOperationBandSwap
import org.shapelogic.sc.operation.implement.SobelOperation
import org.shapelogic.sc.operation.implement.Color2GrayOperation
import org.shapelogic.sc.operation.implement.ColorSimilarityOperation
import org.shapelogic.sc.imageprocessing.SBSegmentation
import org.shapelogic.sc.imageprocessing.SBSegmentation
import org.shapelogic.sc.imageprocessing.EdgeTracerColor

object Transforms {

  lazy val inverseImageTransformWithName: ImageTransformWithName[Byte] = {
    import GenericInverse.DirectInverse._
    AssembleOperation.makeGenericImageTransformWithName[Byte]("Inverse")
  }

  lazy val blackImageTransformWithName: ImageTransformWithName[Byte] = {
    import GenericFunctions.DirectBlack._
    AssembleOperation.makeGenericImageTransformWithName[Byte]("Black")
  }

  lazy val whiteImageTransformWithName: ImageTransformWithName[Byte] = {
    import GenericFunctions.DirectWhite._
    AssembleOperation.makeGenericImageTransformWithName[Byte]("White")
  }

  /**
   * This is packing up all the image operation to be displayed in
   *
   * For loose coupling there is no GUI concept here
   */
  def makeImageTransformWithNameSeq(): Seq[ImageTransformWithNameT[Byte]] = {
    Seq(
      inverseImageTransformWithName,
      ImageTransformWithName(ImageOperationBandSwap.redBlueImageOperationTransform, "Swap"),
      ImageTransformWithName(SobelOperation.sobelOperationByteFunction, "Sobel"),
      ImageTransformWithName(Color2GrayOperation.color2GrayByteTransform, "Gray"),
      //      ImageTransformWithName(SBSegmentation.transform, "Segmentation"),
      blackImageTransformWithName,
      whiteImageTransformWithName)
  }

  def makeImageTransformDialogSeq(): Seq[ImageTransformDialogT] = {
    Seq(
      ImageTransformDialog(
        transform = ThresholdOperation.makeByteTransform,
        name = "Threshold",
        dialog = "Input threshold",
        defaultValue = "111"),
      ImageTransformDialog(ChannelChoserOperation.makeByteTransform, "Channel Choser", "Channel number", "1"),
      ImageTransformDialog(EdgeTracerColor.makeByteTransform, "Edge", "x,y,distance of start point", "10,10,10"),
      ImageTransformDialog(SBSegmentation.makeByteTransform, "Segmentation", "Distance", "10"),
      ImageTransformDialog(ColorSimilarityOperation.pointSimilarOperationByteTransform, "Background", "x,y,distance of start point", "10,10,10"))
  }

}