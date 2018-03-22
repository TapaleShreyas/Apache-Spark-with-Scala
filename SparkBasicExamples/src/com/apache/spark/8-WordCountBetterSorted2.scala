package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object WordCountBetterSorted2 {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    // We are not using every core of processor here as because it will yields to different result than expected
    val context = new SparkContext("local","Without using CountByValue")
    
    val lines = context.textFile("../data/book.txt")
    
    val words = lines.flatMap(x => x.split("\\W+"))
    
    val lowerCase = words.map(x => x.toLowerCase())
    
    val result = lowerCase.map(x => (x,1) ).reduceByKey((x,y) => x + y)
    
    val sortedResult = result.map(x => (x._2,x._1) ).sortByKey()
    
    for(result <- sortedResult) {
      val word = result._2
      val count = result._1
      println(s"$word : $count")
    }
  }
}