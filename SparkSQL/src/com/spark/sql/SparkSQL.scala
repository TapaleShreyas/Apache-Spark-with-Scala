package com.spark.sql

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

object SparkSQL {
  
  case class Person(ID: Int, name: String, age: Int, friends: Int)
  
  def main(args: Array[String]) {
	  Logger.getLogger("org").setLevel(Level.ERROR)
     
    val session = SparkSession
      .builder()
      .appName("Basics of SparkSQL")
      .master("local[*]")
      // Below line is necessary to work around a Windows bug in Spark 2.0.0
      .config("spark.sql.warehouse.dir","file:///C:/temp")
      .getOrCreate()
      
      
    val lines = session.sparkContext.textFile("../data/Friends.csv")
    val peoples = lines.map(mapper)
    
    
    // Infer the schema, and register the DataSet as a table.
    import session.implicits._
    
    // to convert RDD into Datasets i.e from RDD[SparkSQL.Person] into Dataset[SparkSQL.Person]
    val peopleSchema = peoples.toDS
    
    peopleSchema.printSchema()
    
    peopleSchema.createOrReplaceTempView("people")
    
    // SQL can be run over DataFrames that have been registered as a table
    val teenAgers = session.sql("SELECT * FROM people WHERE age > 15 AND age < 20")
    
    val result = teenAgers.collect()
    
    result.foreach(println)
   
    session.stop()
  }
  
  def mapper(line: String): Person = {
    val fields = line.split(',')
    val person: Person = Person(fields(0).toInt, fields(1), fields(2).toInt, fields(3).toInt)
    return person
  }
}