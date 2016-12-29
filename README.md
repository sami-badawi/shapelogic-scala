# README for Shapelogic Scala #

The purpose of Shapelogic Scala is to make image processing more generic 
and to port some of the algorithm from Shapelogic Java.

## Goals ##

Java Abstract Window Toolkit (AWT) have had image functionality since Java 1.0.
Image processing in Java has problems.
Java does not have the unsigned integer that are prevalent in image processing.
There are workarounds but it feels clumsy.

[BoofCV](http://boofcv.org) and [ImageJ](https://imagej.nih.gov/ij/features.html)
are a good new image processing library for Java.
Scala has a lot of advanced type machinery. 
It should be possible to use that for a more generic image definition.

## Shapelogic History ##

Shapelogic was started in 2007 as a Java image processing library.
The primary purpose was add functional programming techniques to Java.
Functional programming ideas have made it into Java 8 and Scala.
Shapelogic Java is now bit rotted. 

This incarnation of Shapelogic was started in 2016. 

## Status ##

* Version 0.0.2
* Early expermimentation

## How do I get set up? ##

* Stardard Git and SBT Scala project
* Currently no configuration
* Dependencies on Spire
* No database is used
* How to run tests: ```sbt test```
* Currently no GUI all command line

### Who do I talk to? ###

* Repo owner: Sami Badawi
