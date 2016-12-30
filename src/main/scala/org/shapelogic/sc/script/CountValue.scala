package org.shapelogic.sc.script

import org.shapelogic.sc.util.Args

/**
 * Example displaying creation of
 */
object CountValue {

  def countZero(filename: String): Long = {
    0
  }

  def errorMessage(): Unit = {
    Args.parser
  }

  def main(args: Array[String]): Unit = {
    val paramOpt = Args.parser.parse(args, Args())
    paramOpt match {
      case Some(param) => {
        val filename = param.input
        if (!filename.isEmpty) {
          val zeros = countZero(filename)
          println(s"zero count: $zeros in $filename")
        } else {
          errorMessage()
        }
      }
      case None => errorMessage()
    }
  }
}