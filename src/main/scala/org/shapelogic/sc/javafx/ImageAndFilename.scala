package org.shapelogic.sc.javafx

/**
 * There are 2 image types
 * Since image seems to be saving in RGBA
 * And for some operations it is important that it is a specific type
 * The idea is that you will take the best image type for what you need.
 * 
 * So if you need to run an operation on an ImageAndFilename and it already 
 * is present in bufferImage format you do not need to translate it first.
 */
case class ImageAndFilename(
    bufferImage: org.shapelogic.sc.image.BufferImage[_],
    image: javafx.scene.image.Image,
    url: String)