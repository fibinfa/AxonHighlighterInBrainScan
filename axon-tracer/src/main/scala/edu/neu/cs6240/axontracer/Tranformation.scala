package edu.neu.cs6240.axontracer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import edu.neu.cs6240.utils.TransformMatrix
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors

object Tranformation {
  def main(args: Array[String]) = {
    val conf = new SparkConf()
      .setAppName("model training")
      .setMaster("local")
    //initialise the Spark context
    val sc = new SparkContext(conf)
    //parsing the input file and converting to labelled point where
    //label is the flag and vector is the neighborhood
    val trainingData = sc.textFile(args(0) + "/Training/*.csv")
      .map(row => row.split(","))

    val trainingData90 = trainingData
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(rotate90(arr).
          map(str => str.toDouble))))

    val trainingData180 = trainingData
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(rotate180(arr).
          map(str => str.toDouble))))

    val trainingData270 = trainingData
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(rotate180(rotate90(arr)).
          map(str => str.toDouble))))

    trainingData90.saveAsTextFile(args(1) + "1")
    trainingData180.saveAsTextFile(args(1) + "2")
    trainingData270.saveAsTextFile(args(1) + "3")

  }

  def rotate90(arr: Array[String]): Array[String] = {
    return arr.take(arr.length - 1)
      .toList.grouped(441).toList
      .transpose
      .map(arr => arr.reverse).flatten.toArray

  }

  def rotate180(arr: Array[String]): Array[String] = {
    return arr.take(arr.length - 1).toList.reverse.toArray

  }
}