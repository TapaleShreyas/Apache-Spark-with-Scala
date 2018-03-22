package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object WordCountSimple {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]","Simple Word Count Program")
    
    val lines = context.textFile("../data/book.txt")
    
    val words = lines.flatMap(x => x.split(" "))
    
    val result = words.countByValue()
    
    result.foreach(println)
  }
}