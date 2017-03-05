![Logo](https://github.com/sami-badawi/shapelogic/blob/master/docs/image/shapelogicsmallgradient.png)

# ShapeLogic Scala #

ShapeLogic Scala is a generic computer vision library with cross-platform [GUI](http://shapelogicscala.org/gui). You write your image processing algorithm once, and it will work for images based on byte, short, float and double.

It has standard image processing algorithms like: Invert, threshold, edge detection, segmentation, skeletonize, edge tracer, vectorizer, point and line annotation all written in generic idiomatic Scala.

# Getting Started #

## Include ShapeLogic as library in your SBT project ##

```scala
"org.shapelogicscala" %% "shapelogic" % "0.9.0"
```

Versions available for Scala 2.11 and 2.12

## Work with ShapeLogic source locally ##

```
clone git https://github.com/sami-badawi/shapelogic-scala.git
cd shapelogic-scala
sbt compile
sbt test
```

## Start the ShapeLogic JavaFX GUI ##

```
sbt stage
target/universal/stage/bin/shapelogic
or on Windows
target/universal/stage/bin/shapelogic.bat
```
ShapeLogic Scala has a simple JavaFX GUI, it can:

  * Load and save images
  * Undo and image info
  * Invert
  * Threshold, background and foreground selection
  * Sobel edge detection
  * Edge crawler with vectorizer and feature extraction for points and lines
  * Segmentation
  * Morphology: Skeletonize, outline, dilate, erode, open and close
  * To gray scale, fill black and white
  * Color channel chooser, channel swapper

# Status #

* Version 0.9.0
* It is pretty simple to write image operations and add them to GUI
* In alpha, but getting more stable
* ShapeLogic Scala [project site](http://shapeLogicscala.org)
* [GitHub project](https://github.com/sami-badawi/shapelogic-scala)
* Unit test using ScalaTest
* [Google group](https://groups.google.com/forum/#!forum/shapelogic)

## Short Term Goals ##

* Particle analyzer and line finder as start of OCR system
* Output annotated points, lines and polygons in json format
* Work well with Java image processing libraries like: ImageJ, BoofCV and OpenCV Java

## Long Term Goals ##

ShapeLogic Scala goal is be a framework for object recognition
using a hybrid approach to A.I. combining machine learning and symbolic A.I.
Using some of the following techniques:
 
* Machine learning
  * Logistic regression, Naive Bayes or random forest
  * Neural network
  * Baysian network / graphical model
* Symbolic artificial intelligence
  * Lisp like tree search / lazy stream
  * Logic programming
  * RDF or knowledge graph
  * Database

First example will be OCR, optical character recognition for a page of text.
It is a solved but nontrivial problem.

## BufferImage ##

ShapeLogic Scala has a unified generic image class [BufferImage](https://github.com/sami-badawi/shapelogic-scala/blob/master/src/main/scala/org/shapelogic/sc/image/BufferImage.scala) that is mainly a buffer. 

If you want to program your own image operations here are 5 base [image operations](http://shapelogicscala.org/image-operations/) you can start from:

* [SimpleTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/SimpleTransform.scala)
* [ChannelTransform](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelTransform.scala)
* [BaseOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/BaseOperation.scala)
* [ChannelOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ChannelOperation.scala)
* [ImageOperation](https://github.com/sami-badawi/shapelogic/blob/master/src/main/scala/org/shapelogic/sc/operation/ImageOperation.scala)


## Generic Image What is the Big Deal ##

There are a lot of challenges with creating a generic image class like BufferImage:

* Bytes are signed in Scala and Java but unsigned for images
* Primitive numeric types are not a subclass of anything
* Byte needs to be promoted to integers, while float do not, normal generic classes will not do this
* Some sort of dependent types are needed which can be accomplished using type level programming
* [Type classes](http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html) can be used to define number, but they do not play well type level programming
* The image class need to be specialized to avoid boxing of primitive operation

## ShapeLogic History ##

[ShapeLogic Java](http://shapelogic.org) was started in 2007 as a Java image processing library
and library for functional programming techniques in Java.
Functional programming now has better implementations in Java 8 and Scala. ShapeLogic Scala was started in 2016 and ports parts of ShapeLogic Java.

## Image IO and Dependencies ##

The goal is to keep library dependencies for ShapeLogic low.
Currently the images loaders are using javax.imageio and JavaFX. They are only part of Oracle JDK not on OpenJDK.

* Stardard Git and SBT Scala project
* Dependencies on 
  * [Spire](https://github.com/non/spire) for generic math
  * [javax.imageio](http://docs.oracle.com/javase/8/docs/api/javax/imageio/ImageIO.html) for images IO
  * [JavaFX](http://docs.oracle.com/javafx/) for GUI
  * [Breeze](https://github.com/scalanlp/breeze) for linear algebra and machine learning

## Example of Running Command Line Scripts

```
Threshold:
sbt 'run-main org.shapelogic.sc.script.Threshold -i "image/rgbbmwpng.png" -t 10 -o "image/out.png"'
or
target/universal/stage/bin/shapelogic -main "org.shapelogic.sc.script.Threshold" -- -i image/440px-Lenna.png
```

### Who Do I Talk to? ###

* Repo owner: [Sami Badawi](http://blog.samibadawi.com/) / [@Sami_Badawi](https://twitter.com/Sami_Badawi)
