package org.shapelogic.sc.javafx

case class ImageAndFilename(
    bufferImage: org.shapelogic.sc.image.BufferImage[Byte],
    image: javafx.scene.image.Image,
    url: String)