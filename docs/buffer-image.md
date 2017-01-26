---
layout: page
title: Buffer Image
permalink: /buffer-image/
---

Central to Shapelogic Scala is minimal generic image traits and classes. 
This is about the main image class BufferImage and a few traits that it implements.

# Image Trait

There are 3 image trait. They are all minimal generic.

* [ImageShape](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/ImageShape.scala) non generic image dimentions
* [ReadImage](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/ReadImage.scala) image that you can read from
* [WriteImage](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/WriteImage.scala) Image that you can write to
* [BufferImageTrait](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/BufferImageTrait.scala) BufferImage trait

They are [Simulacrum](https://github.com/mpilquist/simulacrum) type classes. This means that it is easy to wrap these around many image classes.

## ReadImage
[ReadImage](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/ReadImage.scala) has minimal read functionality.

## WriteImage
[WriteImage](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/WriteImage.scala) has minimal read and write functionality.
Subclass of ReadImage

## BufferImageTrait
[BufferImageTrait](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/BufferImageTrait) has minimal buffered image functionality.
Subclass of ReadImage and WriteImage.


# Image Classes

* BufferImage

## BufferImage
[BufferImage](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/image/BufferImage) is a buffered memory based image. 
Implementing traits ReadImage, WriteImage and BufferImageTrait.
It has minimal read and write functionality.

