---
layout: page
title: GUI
permalink: /gui/
---

The primary focus for ShapeLogic is to be an image processing library. It can also be started in GUI mode. The main GUI is written in JavaFX, but there is an experimental one in ImgaeJ in a branch called feature/imagej, this branch also can run the JavaFX GUI so they are not mutually exclusive.

## JavaFX GUI
ShapeLogic has a simple JavaFX GUI. See image below:

## ImageJ GUI
There is also an experimental branch, feature/imagej, that has a ImageJ GUI. 

It just starts up the ImageJ GUI. But this might be a good way to integrate ShapeLogic into ImageJ.

## Build and Start Instructions

```scala
sbt stage

This will build everything.

To start JavaFX GUI:
target/universal/stage/bin/shapelogic

To start with ImageJ GUI:

target/universal/stage/bin/shapelogic -main "org.shapelogic.sc.imagej.ImageJGui" image/440px-Lenna.png 

target/universal/stage/bin/shapelogic -main "org.shapelogic.sc.imagej.ImageJGui"
```

![ShapeLogic GUI](https://raw.githubusercontent.com/sami-badawi/shapelogic-scala/master/image/JavaFX_ShapeLogic_GUI.png)

