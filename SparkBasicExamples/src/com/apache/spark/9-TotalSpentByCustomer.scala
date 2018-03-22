package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object TotalSpentByCustomer {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]","Total amount spent by each Customer")
    
    // load Data (customerID, orderID, amount)
    val rdd = context.textFile("../data/CustomerOrders.csv")
    
    val lines = rdd.map(parseLine)
    
    val result = lines.reduceByKey((x,y) => x+y)
    
    val sortedResult = result.map(x => (x._2,x._1)).sortByKey()
    
    for(row <- result) {
      val ID = row._1
      val spent = row._2
      println(f"$ID : $spent%.2f")
    }
  }
  
  def parseLine(line: String) = {
    val fields = line.split(",")
    val customerID = fields(0).toInt
    val amount = fields(2).toFloat
    (customerID, amount)
  }
}