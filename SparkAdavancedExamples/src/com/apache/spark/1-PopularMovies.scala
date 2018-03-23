package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext
import scala.collection.Map
import scala.io.Codec
import java.nio.charset.CodingErrorAction
import scala.io.Source

object PopularMovies {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val context = new SparkContext("local[*]","Find out the most popular movies")
    
    // Creating movie ID to Name dictionary and broadcast it to every executor
    val movieNameDir = context.broadcast(loadMovieNames)
    
    // Load data (userID movieID ratings timestamp)
    val lines = context.textFile("../data/UserMovieRatings.data")
    
    // Take movieID only as we are interested in that only to check how many time it is rated
    // Forming tuple (movieID,1) to get count of each movie
    val movies = lines.map(x => (x.split("\t")(1).toInt,1)).reduceByKey((x,y) => x+y)
    
    // Flip (movieID, count) to (count, movieID) for sorting against count 
    val sorted = movies.map(x => ( x._2, x._1 ) ).sortByKey(false)
    
    // Now map each movieID to its corresponding name from broadcasted map
    val result = sorted.map(x => (movieNameDir.value(x._2), x._1))
    
    // Final result with (movieName, ratingCount)
    result.collect()foreach(println)
  }
  
  // Get movie names loaded from file and convert it to ID->name Map
  def loadMovieNames() : Map[Int,String] = {
    
    // Code to handle character encoding issue
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    
    // Creating map of ID -> movieName
    var movieNames : Map[Int, String] = Map()
    
    // Load data from file (movieID | movieName | date | link | ...|...)
    val lines = Source.fromFile("../data/u.item").getLines()
    for(line <- lines) {
      
      // Here | in single quote is very much important don't enclose it in double quote.
      val fields = line.split('|')
      if(fields.length > 1) {
        movieNames += (fields(0).toInt -> fields(1))
      }
    }
    movieNames
  }
}