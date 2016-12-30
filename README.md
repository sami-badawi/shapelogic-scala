![Logo](https://github.com/sami-badawi/shapelogic/blob/master/docs/image/shapelogicsmallgradient.png)

# Shapelogic Scala #

The purpose of Shapelogic Scala is to make a simple generic image processing / computer vision in Scala.

## Background ##

Doing image processing in Java is harder than it should be.
Java Abstract Window Toolkit (AWT) have had image functionality since Java 1.0.
This feels dated and has many problems:
* Java does not have the unsigned integer that are prevalent in image processing.
* AWT was made for the purpose of making GUIs and 2D graphics.
* There are a lot of encapsulation and dependencies.
* It feels clumsy.

[BoofCV](http://boofcv.org) and [ImageJ](https://imagej.nih.gov/ij/features.html)
are a good new image processing libraries for Java, but they don't use Scala's advanced language features.

## Goals ##

* Make minimal uniform classes to for images in ideomatic Scala
* Make loaders and savers for these
* Port some algorithms from Shapelogic Java 
  * Vectorization 
  * Feature extraction
* Combine with machine learning to do some image classification

## Shapelogic History ##

[Shapelogic Java](http://shapelogic.org) was started in 2007 as a Java image processing library.
The primary purpose was add functional programming techniques to Java.
Functional programming ideas have made it into Java 8 and Scala.
Shapelogic Java is now bit rotted. 

Shapelogic Scala was started in 2016. 

## Status ##

* Version 0.0.3
* Early experimentation
* Pre alpha

## How do I get set up? ##

* Stardard Git and SBT Scala project
* Currently no configuration
* Dependencies on Spire
* No database is used
* How to run tests: ```sbt test```
* Currently no GUI all command line

### Who do I talk to? ###

* Repo owner: Sami Badawi
