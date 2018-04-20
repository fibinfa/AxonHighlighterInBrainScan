package edu.neu.cs6240.axontracer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Tranformation {
  def main(args: Array[String]) = {
    val conf = new SparkConf()
      .setAppName("model training")
      .setMaster("yarn")
    //initialise the Spark context
    val sc = new SparkContext(conf)
    //parsing the input file and converting to labelled point where
    //label is the flag and vector is the neighborhood
     val transformedFeatures = sc.textFile(args(0) + "/Training/*.csv")
      .map(row => row.split(",")).map(arr => arr.take(arr.length - 1))
//      .map(arr => )
     
     
  }
}