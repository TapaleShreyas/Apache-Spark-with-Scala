package com.apache.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext

object PopularSuperhero {
  def main(args: Array[String]) {
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    val context = new SparkContext("local[*]","Find the most popular superhero")
    
    // Load data from file (heroID "heroName")
    val superHeros = context.textFile("../data/marvel-names.txt")
    
    // Note here the use of flatMap
    val superHeroDir = superHeros.flatMap(parseLines)
    
    // Load data from file (heroID anotherID anotherID anotherID anotherID)
    val graphRDD = context.textFile("../data/marvel-graph.txt")
    
    // Form (heroID, NoOfCoAppearances) pairing
    val coAppearances = graphRDD.map(getCoAppearances)
   
    // flip to (NoOfCoAppearances, heroID) to form most popular
    val reduceByKey = coAppearances.reduceByKey((x,y) => x+y).map(x => (x._2,x._1))
    val result = reduceByKey.max()
    
    // Lookup superHero name in superHeroDir against ID
    val superHero = superHeroDir.lookup(result._2)(0)
    println(s"$superHero is the most popular superHero with ${result._1} co appearances")
  }
  
  def getCoAppearances(line: String) = {
    val fields = line.split("\\s+")
    (fields(0).toInt, fields.length -1)
  }
  
  def parseLines(line: String) : Option[(Int, String)] = {
    val fields = line.split('\"')
    if(fields.length > 1) {
    	val heroID = fields(0).trim().toInt
    	val heroName = fields(1) 
        Some(heroID, heroName)
    }else {
        // flatmap will just discard None results, and extract data from Some results.
        None
    }
  }
}