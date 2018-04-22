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
 * Scala object which loads a model and 
 * Classifies a pixel as background or foreground by taking the input
 * as neighbouring matrix
 * args(0) - path to input folder which has  data
 * for which classification needs to be performed 
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

    //model is loaded as an rdd
    val trainingModel = RandomForestModel.load(sc, args(0)+"/Model")
      

    val predictStartTime = System.currentTimeMillis()

    //prediction
    val predictioninput = sc.textFile(args(0) + "/Prediction/*.csv")

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

    predictions.coalesce(1).saveAsTextFile(args(1)+"Final")

    val predictEndTime = System.currentTimeMillis()

    println("Prediction took " + (predictEndTime - predictStartTime))

  }

}

