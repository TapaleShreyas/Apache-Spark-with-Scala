package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object WordCountBetter {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]","Word Count program using RegularExpression")
    
    val lines = context.textFile("../data/book.txt")
    
    val words = lines.flatMap(x => x.split("\\W+"))
    
    val ignoreCase = words.map(x => x.toLowerCase())
    
    val result = ignoreCase.countByValue()
    
    result.foreach(println)
  }
}