package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object MovieRatingsCounter {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    // Using all available cores of local machine
    val context = new SparkContext("local[*]","Movie ratings counter")
    
    // Load data (userID, movieID, ratings, timestamp)
    val lines = context.textFile("../data/UserMovieRatings.data")
    
    // Take only required field(ratings) from data.
    val ratings = lines.map(x => x.toString().split("\\t")(2))
    
    // Count up how many times each value (rating) occurs 
    val ratingsCount = ratings.countByValue()
    
    // Sort result like rating 1, rating 2...
    val sortByRatings = ratingsCount.toSeq.sortBy(_._1)
    
    // Print final result
    sortByRatings.foreach(println)
  }
}