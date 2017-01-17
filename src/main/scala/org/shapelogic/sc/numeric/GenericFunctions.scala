package org.shapelogic.sc.numeric

object GenericFunctions {

  /**
   * Constant functions returning 0
   * There is probably a more elegant way to this
   */
  object DirectBlack {

    implicit lazy val byteInverse: TransFunction[Byte] = new TransFunction[Byte] {
      def transform(input: Byte): Byte = 0
    }

    implicit lazy val shortInverse: TransFunction[Short] = new TransFunction[Short] {
      def transform(input: Short): Short = 0
    }

    implicit lazy val intInverse: TransFunction[Int] = new TransFunction[Int] {
      def transform(input: Int): Int = 0
    }

    implicit lazy val floatInverse: TransFunction[Float] = new TransFunction[Float] {
      def transform(input: Float): Float = 0
    }

    implicit lazy val doubleInverse: TransFunction[Double] = new TransFunction[Double] {
      def transform(input: Double): Double = 0
    }
  }

  object DirectWhite {

    implicit lazy val byteInverse: TransFunction[Byte] = new TransFunction[Byte] {
      def transform(input: Byte): Byte = -1
    }

    implicit lazy val shortInverse: TransFunction[Short] = new TransFunction[Short] {
      def transform(input: Short): Short = -1
    }

    implicit lazy val intInverse: TransFunction[Int] = new TransFunction[Int] {
      def transform(input: Int): Int = -1
    }

    implicit lazy val floatInverse: TransFunction[Float] = new TransFunction[Float] {
      def transform(input: Float): Float = 1
    }

    implicit lazy val doubleInverse: TransFunction[Double] = new TransFunction[Double] {
      def transform(input: Double): Double = 1
    }
  }
}