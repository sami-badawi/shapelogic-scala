---
layout: page
title: Image Operations
permalink: /image-operations/
---

The work horse for generic image processing operations are:

* [SimpleTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/SimpleTransform.scala)
* [ChannelTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelTransform.scala)
* [BaseOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/BaseOperation.scala)
* [ChannelOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelOperation.scala)
* [ImageOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ImageOperation.scala)

These 5 operations are getting to a point where they will get the work done.

There are in the [Image Operations](https://github.com/sami-badawi/shapelogic-scala/tree/master/src/main/scala/org/shapelogic/sc/operation) package.

# Image Operations Common Ideas #

All the operations are using a PixelOperation to do the iteration:

```scala
  lazy val pixelOperation: PixelOperation[T] = new PixelOperation[T](inputImage)
```

### Transform ###

SimpleTransform and SimpleTransform are transformers taking a function as 
input that is working on the input buffer value to produce the output buffer.

### Index Based Runners ###

BaseOperation, BaseOperation and ImageOperation are index based. 
They take a PixelHandler as input that will do the calculation of output 
based on an index into the input buffer.


## SimpleTransform ##

* Input and output buffer type is the same.
* Number of color channels in input are the same.
* Input is number function that take value of pixel in one channel and produced output pixel in same channel.

Simplest Image Operations is [SimpleTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/SimpleTransform.scala)
This creates an image of same type but with same structure where the same function is handling each color channel in parallel.
All it take to write one of those is a one function that take a number from an input channel and calculate the output channel. The problem is that the function need to be generic. Kind of like [Shapeless' Ploy function](https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#polymorphic-function-values)


## ChannelTransform ##

* Input and output buffer type can be different.
* Number of color channels in input are the same.
* Input is number function that take value of pixel in one channel and produced output pixel in same channel.

[ChannelTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelTransform.scala) is almost like SimpleTransform, but the buffer type can be different.
The reason that both SimpleTransform and ChannelTransform exist is that
specialization create a version of the class for every combination of the
generic type parameters T and O

## BaseOperation ##

* Input and output buffer type is the same.
* Number of color channels in input can be anything, in output there is 1 color channel
* This take PixelHandlerSame as input that has an abstract method: ```def calc(index: Int): I``` that need to be overridden

This create an image with the same dimension, but with one output channel, but with the same buffer number type.

## ChannelOperation ##

* Input and output buffer type can be different.
* Number of color channels in input are the same.
* This take PixelHandlerSame as input that has an abstract method: ```def calc(index: Int): I``` that need to be overridden
* Work on one color channel at a time, the channel are handled in parallel. So only one function is needed.

Close to BaseOperation but works one one color channel at a time.

## ImageOperation ##

* Input and output buffer type are the same.
* Number of color channels in input are the same.
* All the input channels can be used to calculate all the output channels. There is no assumption that each channel is treated in the same way.
* This take PixelHandlerSame as input that has an abstract method: ```def calc(indexIn: Int, channelOut: Int): I``` that need to be overridden

Most general operation.


## Generic Image Inverse

Image Inverse is using SimpleTransform. It is the hello world of image processing operations. This is how it looks in ShapeLogic:

```scala

  def makeInverseTransform[@specialized(Byte, Short, Int, Long, Float, Double) 
      T: ClassTag: Numeric: Ordering: TransFunction](
    inputImage: BufferImage[T]): SimpleTransform[T] = {
    import GenericInverse.DirectInverse._
    val genericFunction: TransFunction[T] = implicitly[TransFunction[T]]
    val function: T => T = genericFunction.transform
    new SimpleTransform[T](inputImage)(function)
  }
```

This is using:

```scala

object GenericInverse {
  object DirectInverse {

    val onesByte: Byte = -1
    implicit lazy val byteInverse: TransFunction[Byte] = new TransFunction[Byte] {
      type Res = Byte
      def transform(input: Byte): Byte = {
        (~input).toByte
      }
    }

```

### Color2GrayOperation ###

Turns color images with 3 or 4 color channels into gray scale images with 1 or 2 channels.

[Color2GrayOperation](https://github.com/sami-badawi/shapelogic-scala/blob/master/src/main/scala/org/shapelogic/sc/operation/Color2GrayOperation.scala) it is implemented using BaseOperation.

## Writing Generic Image Operation is Hard

Writing generic image processing code is surprisingly hard.  It is also hard in Haskell. The [Haskell library Repa](https://wiki.haskell.org/Numeric_Haskell:_A_Repa_Tutorial) moved the structure of an image into the type system, but Repa is hard to work with and not very intuitive coming from a non Haskell background.

The C++ [Generic Image Library ](http://www.boost.org/doc/libs/1_44_0/libs/gil/doc/html/giltutorial.html) is pretty nice, but not simple.

