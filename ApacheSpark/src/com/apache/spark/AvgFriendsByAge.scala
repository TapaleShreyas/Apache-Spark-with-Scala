package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object AvgFriendsByAge {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]","Average Friends By Age")
    
    // Load data into RDD (ID, Name, Age, FriendsCount)
    val lines = context.textFile("../data/Friends.csv")
    
    val rdd = lines.map(parseLines)
    
    // Now we are having result of parseLine in (age, friendsCount) format
    // lets make it to (age, (friendsCount, 1)) format to count no of friends plus how many with same age
    
    // map value will convert (33, 385) to (33, (385,1))
    val mapValue = rdd.mapValues(x => (x, 1))
    
    // Now suppose we are having 2 entries for age 33
    // (47, (312,1))
    // (47, (5,1))
    
    // Then reduceByKey will convert it to (47, (317, 2))
    // i.e (age, (friendsCount, noOfpeopleOfsameAge))
    val sameAge = mapValue.reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2)) 
    
    // Now mapValues will take only value part i.e (317,2) for each row and divide first value of tuple by second
    // to get average number of friends.
    val avgFriendsPerAge = sameAge.mapValues(x => x._1/x._2)
    
    val sortResult = avgFriendsPerAge.collect
    
    sortResult.sorted.foreach(println)
    
    
  }
  
  def parseLines(line: String) = {
    
    // Split all the fields by ,
    val fields = line.split(",")
    
    // Take only required fields (age, friendsCount)
    val age = fields(2).toInt
    val friends = fields(3).toInt
    
    //return tuple of (age, friends)
    (age, friends)
    
  }
}