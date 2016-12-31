package org.shapelogic.sc.util

import scopt._

case class Args(
  input: String = "",
  output: String = "",
  x: Int = 0,
  y: Int = 0)

object Args {

  lazy val parser = new OptionParser[Args]("shapelogic") {
    head("shapelogic", "NA")
    opt[String]('i', "input") action { (src, c) =>
      c.copy(input = src)
    } text ("input csv file. E.g.: --input image/red.jpg ")
    opt[String]('o', "output") action { (src, c) =>
      c.copy(output = src)
    } text ("input csv file. E.g.: --input image/red.jpg")
    opt[Int]('x', "x") action { (src, c) =>
      c.copy(x = src)
    } text ("image coordinate: E.g. -x 100 or --x 100")
    opt[Int]('y', "y") action { (src, c) =>
      c.copy(y = src)
    } text ("image coordinate: E.g. -y 50")
    note("""Scala image processing
""")
    help("help") text ("shapelogic command line parser help")
  }

}