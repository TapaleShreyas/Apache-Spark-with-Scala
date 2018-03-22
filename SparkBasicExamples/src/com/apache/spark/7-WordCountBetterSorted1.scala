package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object WordCountBetterSorted1 {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]","Sort word count result by key")
    
    val lines = context.textFile("../data/book.txt")
    
    val words = lines.flatMap(x => x.split("\\W+"))
    
    val lowerCase = words.map(x => x.toLowerCase())
    
    val result = lowerCase.countByValue().map(x => (x._2,x._1)).toSeq.sorted
    
    for(row <- result) {
      val word = row._2
      val count = row._1
      println(s"$word : $count")
    }
    
    //result.foreach(println)
    
  }
}