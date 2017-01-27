---
# You don't need to edit this file, it's empty on purpose.
# Edit theme's home layout instead if you wanna make some changes
# See: https://jekyllrb.com/docs/themes/#overriding-theme-defaults
layout: home
---
![Logo](/image/shapelogicsmallgradient.png)

# ShapeLogic Scala #

ShapeLogic Scala is a simple generic computer vision library with cross-platform [GUI mode](https://github.com/sami-badawi/shapelogic-scala/wiki/GUI-for-ShapeLogic).

You can write generic image processing code, so the same code will work for images based on numbers of type byte, short, float and double in idiomatic Scala.

It has a unified generic image class [BufferImage](https://github.com/sami-badawi/shapelogic-scala/blob/master/src/main/scala/org/shapelogic/sc/image/BufferImage.scala) that is mainly a buffer. There are a [few traits and helpers](https://github.com/sami-badawi/shapelogic-scala/wiki/Image-Classes-and-Traits). There are 5 base [image operations](http://shapelogicscala.org/image-operations/): [SimpleTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/SimpleTransform.scala),
[ChannelTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelTransform.scala),
[BaseOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/BaseOperation.scala),
[ChannelOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelOperation.scala) and
[ImageOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ImageOperation.scala).


## Current Goals

* Writing generic image processing algorithm based on [generic image operations](http://shapelogicscala.org/image-operations/) is pretty easy but there is still too much boiler plate
* Re-factor again and get ShapeLogic to beta
* Work well with Java image processing libraries like: ImageJ, BoofCV and OpenCV Java
* Implement more image processing algorithms and 
* Port some algorithms from ShapeLogic Java
  * Vectorization 
  * Feature extraction
* Combine with machine learning to do some image classification


## Getting Started ##

```
clone git https://github.com/sami-badawi/shapelogic-scala.git
cd shapelogic-scala
sbt compile
sbt test
```

### Start the JavaFX GUI

```
sbt stage
target/universal/stage/bin/shapelogic
or on Windows
target/universal/stage/bin/shapelogic.bat
```

## Status ##

* Version 0.5.0
* Useful for experimenting
* In alpha, still getting refactored
* Documentation in Wiki and [ShapeLogic Scala project site](http://shapeLogicscala.org)
* It is pretty simple to write image operations and add them to GUI, but writing generic code is harder than writing code that only works with byte images
* Unit tests
* ShapeLogic Scala has a simple JavaFX GUI, it can
  * Load and Save
  * Undo and image info
  * Invert, threshold, edge detection, to gray scale, color channel chooser, channel swapper, fill black and white

## Generic Image What is the Big Deal ##

There are a lot of challenges with creating a generic image class:

* Bytes are signed in Scala and Java but unsigned for images
* Primitive numeric types are not a subclass of anything
* Byte needs to be promoted to integers, while float do not, normal generic classes will not do this
* Some sort of dependent types are needed which can be accomplished using type level programming
* [Type classes](http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html) can be used to define number, but they do not play well type level programming
* The image class need to be specialized to avoid boxing of primitive operation


## Image Processing in Java ##

Doing image processing in Java is harder than it should be.
Java Abstract Window Toolkit (AWT) have had image functionality since Java 1.0.
This feels dated and has many problems:

* Java does not have the unsigned integer that are prevalent in image processing.
* AWT was made for the purpose of making GUIs and 2D graphics.
* AWT has many layers of encapsulation and a lot of dependencies.
* It feels clumsy and dated.

There are great new image processing libraries for Java

* [BoofCV](http://boofcv.org)
* [JavaCV (OpenCV Java bindings)](https://github.com/bytedeco/javacv)
* [ImageJ](https://imagej.nih.gov/ij/features.html)
* [JavaFX](http://docs.oracle.com/javafx/2/get_started/jfxpub-get_started.htm)

ShapeLogic should work well with these.  Import and export should be as simple as possible. Ideally you should be able to use several image processing libraries together, they have different strengths.

## ShapeLogic History ##

[ShapeLogic Java](http://shapelogic.org) was started in 2007 as a Java image processing library.
The primary purpose was add functional programming techniques to Java.
Functional programming ideas have made it into Java 8 and Scala, so much of this work is obsolete and ShapeLogic Java is now bit rotted.

ShapeLogic Scala was started in 2016. 

## Image IO and Dependencies ##

The goal is to keep library dependencies for ShapeLogic low.
Currently the images loaders are using javax.imageio and JavaFX. They are only part of Oracle JDK not on OpenJDK.

* Stardard Git and SBT Scala project
* Dependencies on 
  * [Spire](https://github.com/non/spire) 
  * [Simulacrum](https://github.com/mpilquist/simulacrum) 
  * [javax.imageio](http://docs.oracle.com/javase/8/docs/api/javax/imageio/ImageIO.html)

## Example of Running Command Line Scripts

```
Threshold:
sbt 'run-main org.shapelogic.sc.script.Threshold -i "image/rgbbmwpng.png" -t 10 -o "image/out.png"'
or
target/universal/stage/bin/shapelogic -main "org.shapelogic.sc.script.Threshold" -- -i image/440px-Lenna.png

```

### Who Do I Talk to? ###

* Repo owner: [Sami Badawi](http://blog.samibadawi.com/) / [@Sami_Badawi](https://twitter.com/Sami_Badawi)