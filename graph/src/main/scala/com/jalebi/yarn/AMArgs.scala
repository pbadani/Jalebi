package com.jalebi.yarn

case class AMArgs(args: Map[String, Any]) {

}

object AMArgs {
  def apply(args: Array[String]): AMArgs = {
    val usage =
      """
    Usage: mmlaln [--min-size num] [--max-size num] filename
  """

    if (args.isEmpty) {
      println(usage)
      System.exit(1)
    }

    val arglist = args.toList

    def nextOption(map: Map[String, Any], list: List[String]): Map[String, Any] = {
      def isSwitch(s: String) = s(0) == '-'

      list match {
        case Nil => map
        case "--max-size" :: value :: tail =>
          nextOption(map ++ Map("maxsize" -> value.toInt), tail)
        case "--min-size" :: value :: tail =>
          nextOption(map ++ Map("minsize" -> value.toInt), tail)
        case string :: opt2 :: tail if isSwitch(opt2) =>
          nextOption(map ++ Map("infile" -> string), list.tail)
        //        case string :: Nil => nextOption(map ++ Map('infile -> string), list.tail)
        case option :: tail => println("Unknown option " + option)
          //          System.exit(1)
          Map.empty
      }
    }

    AMArgs(nextOption(Map(), arglist))
  }
}
