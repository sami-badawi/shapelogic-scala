---
layout: page
title: Plugins
permalink: /plugins/
---

The idea is that ShapeLogic Scala should be as open as possible.
You should not have to know any detail about JavaFX and how the GUI is structured.

Now most of the image operations are added to the menu with this simple function.

```scala
package org.shapelogic.sc.operation.Transforms

  def makeImageTransformWithNameSeq(): Seq[ImageTransformWithName] = {
    Seq(
      ImageTransformWithName(inverseTransformByte, "Inverse"),
      ImageTransformWithName(ImageOperationBandSwap.redBlueImageOperationTransform, "Swap"),
      ImageTransformWithName(Color2GrayOperation.makeByteTransform, "To Gray"),
      ImageTransformWithName(blackTransformByte, "Make image Black"),
      ImageTransformWithName(whiteTransformByte, "Make image White"))
  }
```


You still have to call this in the GuiMenuBuilder, but it is just one line of code.

```scala
package org.shapelogic.sc.javafx.GuiMenuBuilder

  menuImage.getItems().addAll(thresholdItem, channelChoserItem)
  menuHelp.getItems().addAll(aboutItem)

  addAllImageTransformWithName()
  //Add your operations here
```

If you create your own image operations and add them to the GUI just define

```scala
package com.example.imageprocessing

  def makeImageTransformWithNameSeq(): Seq[ImageTransformWithName] = {
    Seq(
      ImageTransformWithName(coolTransformByte, "Awsome"),

```
