---
layout: post
title:  "Welcome to Shapelogic Scala Blog"
date:   2017-01-22 08:57:03 -0500
categories: jekyll update
---

I wrote my master thesis on computer vision. It was called Robotic Vision and Visual Arts.

In 2007 I started work on an open source project: ShapeLogic. It was a Java plugin to ImageJ.
My thought was that I wanted to add declarative programming techniques to computer vision.
Turned out that functional programming techniques worked better with what I needed. 
Particularly lazy stream programming.

In 2009 I took a full time work and was not able to do more work on ShapeLogic, 
but late 2016 my company approved that I could resume work on ShapeLogic.

In the meantime functional programming had gone from academia to be a main stream programming.
It is an important aspect of Java 8 and Scala. Both have implementation of 
functional programming that were better than what I had in ShapeLogic.

When I resumed work I was thinking what could be useful to other people?

I have been annoyed by the amount of boilerplate that is involved with image processing.
Also that it is hard to write generic code that works for all number types:

* Byte
* Short
* Int
* Long
* Float
* Double

I thought that this is a good challange for Scala's sophisticated type system.

I wanted to come up with only one simple generic class as the main workhorse for algorithms.
What I came up with was BufferImage:

{% highlight scala %}
sealed class BufferImage[@specialized(Byte, Short, Int, Long, Float, Double) T: ClassTag](
    val width: Int,
    val height: Int,
    val numBands: Int,
    bufferInput: Array[T] = null,
    val rgbOffsetsOpt: Option[RGBOffsets] = None) extends WriteImage[T] with BufferImageTrait[T] {

{% endhighlight %}

I have now gotten to a point of testing it with several generic image algorithms.
BufferImage seems like it should be up for the task.

