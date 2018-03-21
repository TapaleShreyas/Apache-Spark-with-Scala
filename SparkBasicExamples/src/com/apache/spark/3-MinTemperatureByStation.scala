package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext
import scala.math.min


object MinTemperatureByStation {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]", "Find Min Temperature for each Station")
    
    // Load data (stationID, date, tempType, temp, ..., ..)
    val lines = context.textFile("../data/MinMaxTemperatures.csv")
    
    // parse each line and take required fields from it
    val rdd = lines.map(parseLine)
    
    // Filter out temperature with type TMIN
    val minTempratures = rdd.filter(x => x._2 == "TMIN")
    
    // Now as we know, we are having only min temperatures record so form tuple with station and temp 
    val stationAndTemp = minTempratures.map(x => (x._1, x._3.toFloat))
    
    
    // For each station , take only min temp record entry
    val result = stationAndTemp.reduceByKey((x,y) => min(x,y))
    
    // Print the result
    result.collect().sorted.foreach(println)
    
    val sortedResult = result.collect.sorted
    
    // Print in different style
    for(result <- sortedResult) {
      val station = result._1
      val minTemp = result._2
      println(f"Weather station with stationID $station is having min temperature $minTemp%.2f F")
    }
    
    
  }
  
  def parseLine(line: String) = {
    val fields = line.split(",")
    val stationID = fields(0)
    val temperatureType = fields(2)
    val temp = fields(3).toFloat * 0.1 * (9f/5f) + 32f
    (stationID, temperatureType, temp)
  }
}