package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext
import scala.collection.Map
import scala.io.Codec
import java.nio.charset.CodingErrorAction

object PopularMoviesWithoutBroadcast {
  def main(args: Array[String]) {
	 
    Logger.getLogger("org").setLevel(Level.ERROR)
    val context = new SparkContext("local[*]", "Find popular movies")
    
   // Load data from file (movieID | movieName | date | link | ...|...)
    val movies = context.textFile("../data/Movies.item")
    
    // Creating movie ID to Name dictionary using RDD
    val movieNameDir = movies.flatMap(parseMovieNames)
    
    // Load data (userID movieID ratings timestamp)
    val ratings = context.textFile("../data/UserMovieRatings.data")
    val movie = ratings.map(x => (x.split("\t")(1).toInt, 1)).reduceByKey((x,y) => x+y)
    
    // Flip (movieID, count) to (count, movieID) for sorting against count
    val flip = movie.map(x => (x._2, x._1.toInt)).sortByKey(false)
    
    // Now map each movieID to its corresponding name from movieNameDir RDD
    val result = flip.map(x => (movieNameDir.lookup(x._2)(0), x._1) )
    
    // Final result with (movieName, ratingCount)
    result.collect().foreach(println)
  }
  
  
  def parseMovieNames(line: String) : Option[(Int, String)] = {
    // Code to handle character encoding issue
	  implicit val codec = Codec("UTF-8")
	  codec.onMalformedInput(CodingErrorAction.REPLACE)
	  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    val fields = line.split('|')
    if(fields.length > 1) {
      Some(fields(0).toInt, fields(1))
    }else {
      None
    }
  }
}