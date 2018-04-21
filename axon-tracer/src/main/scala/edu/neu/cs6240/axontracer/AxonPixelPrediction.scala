package edu.neu.cs6240.axontracer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD
import scala.collection.mutable.ListBuffer

/**
 * @author fibinfrancis, asimkhan
 *
 * Scala object which takes CSV files as input which have brain scanning data.
 * Then it trains a Random Forest on these input files.
 * Classifies a pixel as background or foreground by taking the input
 * as neighbouring matrix
 * args(0) - path to input folder which has two subfolders
 * having Training data inside Training folder and  data
 * for which classification needs to be performed in Prediction folder
 *
 */
object AxonPixelPrediction {

  def main(args: Array[String]) = {
    //initialise config - "spark master = yarn for EMR exec, local for local execution"
    val conf = new SparkConf()
      .setAppName("prediction")
      .setMaster("yarn")
    //initialise the Spark context
    val sc = new SparkContext(conf)

    val trainingStartTime = System.currentTimeMillis()

    //parsing the input file and converting to labelled point where
    //label is the flag and vector is the neighborhood
    val trainingData = sc.textFile(args(0) + "/Training/*.csv")
      .map(row => row.split(","))
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(arr.take(arr.length - 1).
          map(str => str.toDouble))))

    //model parameters
    //these can be tuned to obtain desired accuracy
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 50
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val impurity = "gini" // for classification
    val maxDepth = 10
    val maxBins = 100

    //model is created and stored as an rdd
    val trainingModel = RandomForest.trainClassifier(trainingData, numClasses,
      categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

    // training is done

    val trainingEndTime = System.currentTimeMillis()

    val predictStartTime = System.currentTimeMillis()

    //validation
    val predictioninput = sc.textFile(args(0) + "/Validation/*.csv")

    //input data is processed to form an rdd of labeled point
    val processedValData = predictioninput.map(row => row.split(","))
      .map(arr => new LabeledPoint(
        1.0,
        Vectors.dense(arr.take(arr.length - 1).
          map(str => str.toDouble))))

    //predictions are done based on the 21*21*7 block neighborhood
    //pixel intensities and stored in a single file
    val predictions = processedValData.map(row =>
      trainingModel.predict(row.features).toInt)

    predictions.coalesce(1).saveAsTextFile(args(1))

    val predictEndTime = System.currentTimeMillis()

    println("Training took " + (trainingEndTime - trainingStartTime))
    println("Prediction took " + (predictEndTime - predictStartTime))

  }

}

