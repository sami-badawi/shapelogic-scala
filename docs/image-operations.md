---
layout: page
title: Image Operations
permalink: /image-operations/
---

# Image Operations

The work horse for generic image processing operations are:

* [SimpleTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/SimpleTransform.scala)
* [BaseOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/BaseOperation.scala)
* [ImageOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ImageOperation.scala)
* [ChannelOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelOperation.scala)

These 2 operations are getting to a point where they will get the work done.

There are in the [Image Operations](https://github.com/sami-badawi/shapelogic-scala/tree/master/src/main/scala/org/shapelogic/sc/operation) package.

## SimpleTransform

Simplest Image Operations is [SimpleTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/SimpleTransform.scala)
This creates an image of same type but with same structure where the same function is handling each color channel in parallel.
All it take to write one of those is a one function that take a number from an input channel and calculate the output channel. The problem is that the function need to be generic. Kind of like [Shapeless' Ploy function](https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#polymorphic-function-values)

## BaseOperation

This create an image with the same dimension, but with one output channel, but with the same buffer number type.

## ImageOperation

* Input and output buffer type can be different.
* Work on one color channel at a time, but the color channel can swap

## ChannelOperation

* Input and output buffer type is the same.
* Work on one color channel at a time, the channel are handled in parallel. So only one function is needed.


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

