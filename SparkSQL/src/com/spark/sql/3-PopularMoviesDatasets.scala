package com.spark.sql

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.sql._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec
import org.apache.spark.sql.functions.desc

object PopularMoviesDatasets {
	
  final case class Movie(movieID: Int)  

	def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val session = SparkSession
      .builder()
      .appName("Find the movies with the most ratings.")
      .master("local[*]")
      .config("spark.sql.warehouse.dir","file:///C:/temp")
      .getOrCreate()
    
      
    val input = session.sparkContext.textFile("../data/UserMovieRatings.data")
    
    // get movieID's only as we are interested in popular movie
    val ratings = input.map(x => Movie(x.split("\t")(1).toInt))
    
    import session.implicits._
    val ratingsDS = ratings.toDS()
    val result = ratingsDS.groupBy("movieID").count().orderBy(desc("count")).cache()
    val top10 = result.take(10)
    
    val movies = loadMovies()
    
    for(movie <- top10) {
      val movieID = movie.get(0).asInstanceOf[Int]
      val movieName = movies(movieID)
      println(s" $movieID : $movieName")
    }
    
  }
  
  def loadMovies() : Map[Int, String] = {
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    
    //Map of movie IDs to movie names.
    var movies : Map[Int, String] = Map()
    val lines = Source.fromFile("../data/Movies.item").getLines()

    for(line <- lines) {
      val fields = line.split('|')
      movies += (fields(0).toInt -> fields(1))
    }
    return movies
  }
  
}