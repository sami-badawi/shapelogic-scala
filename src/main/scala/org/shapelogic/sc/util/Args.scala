package org.shapelogic.sc.util

import scopt._

case class Args(
  input: String,
  output: String)

object Args {

  lazy val parser = new OptionParser[Args]("shapelogic") {
    head("shapelogic", "NA")
    opt[String]('i', "input") action { (src, c) =>
      c.copy(input = src)
    } text ("input csv file. E.g.: --input image/red.jpg ")
    opt[String]('o', "output") action { (src, c) =>
      c.copy(output = src)
    } text ("input csv file. E.g.: --input image/red.jpg")
    note("""Scala image processing
""")
    help("help") text ("shapelogic command line parser help")
  }

}