package org.shapelogic.sc.imagej

import ij.ImageJ
import ij.ImagePlus
import ij.io.Opener

import java.io.File

/**
 * Very simple to start ImageJ based GUI
 *
 * sbt stage
 * 
 * This will build everything
 * 
 * target/universal/stage/bin/shapelogic -main "org.shapelogic.sc.imagej.ImageJGui"
 * 
 * or to start with image run:
 * 
 * target/universal/stage/bin/shapelogic -main "org.shapelogic.sc.imagej.ImageJGui" image/440px-Lenna.png 
 */
object ImageJGui {

  def main(args: Array[String]): Unit = {
    new ImageJ()
    if (!args.isEmpty) {
      val filename = "image/440px-Lenna.png"
      val file = new File(filename)

      // open a file with ImageJ
      val imp = new Opener().openImage(file.getAbsolutePath())

      // display it via ImageJ
      imp.show()
    }
  }

}