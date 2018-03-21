package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext
import scala.math.max

object MaxTemperatureByStation {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]","Find max temperature for each weather station")
    
    val rdd = context.textFile("../data/MinMaxTemperatures.csv")
    
    val lines = rdd.map(parseLine)
    
    val maxTemperatures = lines.filter(x => x._2 == "TMAX")
    
    val stationAndTemp = maxTemperatures.map(x => (x._1,x._3.toFloat))
    
    val result = stationAndTemp.reduceByKey((x,y) => max(x,y))
    
    val sortedResult = result.collect.sorted
    
    for(result <- sortedResult) {
      val station = result._1
      val maxTemperature = result._2
      println(f"Weather station with ID $station having max temperature $maxTemperature%.2f F")
    }
    
  }
  
  def parseLine(line: String) = {
    val fields = line.split(",")
    val stationID = fields(0)
    val temperatureType = fields(2)
    val temperature = fields(3).toFloat * 0.1 * (9f/5f) + 32
    (stationID, temperatureType, temperature)
  }
}