package com.spark.sql

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object DataFrames {
  
  case class Order(customerID: Int, orderID: Int, price: Float)
  
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val session = SparkSession
      .builder()
      .appName("Spark Dataframes")
      .master("local[*]")
      .config("spark.sql.warehourse.dir","file:///C:/temp")
      .getOrCreate()
    
    val input = session.sparkContext.textFile("../data/CustomerOrders.csv")
    
    import session.implicits._
    
    val customerOrders = input.map(mapOrder).toDS.cache()
    
    println("Showing orderID's using select")
    val orderIDs = customerOrders.select("orderID").show()
    
    println("Order's of 1 to 10 customers")
    val orders = customerOrders.filter(customerOrders("customerID") < 10).show
    
    println("Group order's by customer")
    val groupOrders = customerOrders.groupBy(customerOrders("customerID")).count.orderBy(desc("count")).show()
    
  }
  
    def mapOrder(line: String): Order = {
      val fields = line.split(",")
      val order: Order = Order(fields(0).toInt, fields(1).toInt, fields(2).toFloat)
      return order
    } 
}