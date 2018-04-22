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
 * Does validation on one of the inputs and generates the accuracy
 * args(0) - path to input folder which has two subfolders
 * having Training data inside Training folder and validation data
 * inside Valodation folder
 *
 */
object ModelTraining {

  def main(args: Array[String]) = {
    //initialise config - "spark master = yarn for EMR exec,
    //local for local execution"
    val conf = new SparkConf()
      .setAppName("model training")
      .setMaster("yarn")
    //initialise the Spark context
    val sc = new SparkContext(conf)
    

    val trainingStartTime = System.currentTimeMillis()
    
    //parsing the input file and converting to labelled point where
    //label is the flag and vector is the neighborhood
    val trainingData = sc.textFile(args(0) + "/Training/*.csv")
      .map(row => row.split(","))
      .flatMap(arr => {
        var listBuffer = new ListBuffer[LabeledPoint]
        //original
        listBuffer += new LabeledPoint(
          arr.last.toDouble,
          Vectors.dense(arr.take(arr.length - 1).
            map(str => str.toDouble)))
        //uncomment this part to get transformed data
        //      //90 rotated
        //      listBuffer += new LabeledPoint(
        //        arr.last.toDouble,
        //        Vectors.dense(rotate90(arr).
        //          map(str => str.toDouble)))
        //
        //      //180 rotated
        //      listBuffer += new LabeledPoint(
        //        arr.last.toDouble,
        //        Vectors.dense(rotate180(arr).
        //          map(str => str.toDouble)))
        //
        //      //270 rotated
        //      listBuffer += new LabeledPoint(
        //        arr.last.toDouble,
        //        Vectors.dense(rotate270(arr).
        //          map(str => str.toDouble)))

        listBuffer.map(x => x)

      })

    //parameter tuning
    // change each of these values to tune the accuracy
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

    //validation starts

    val predictStartTime = System.currentTimeMillis()

    //load the model from spark context
    //    val trainedModel = RandomForestModel.load(sc, args(1))

    //validation
    val validationData = sc.textFile(args(0) + "/Validation/*.csv")

    //validation data is processed to form RDD of labeled point
    val processedValData = validationData.map(row => row.split(","))
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(arr.take(arr.length - 1).
          map(str => str.toDouble))))

    //calculate accuracy = correct/count

    val predictions = processedValData.filter(row =>
      trainingModel.predict(row.features) == row.label)

    val predictEndTime = System.currentTimeMillis()

    val correct = predictions.count().toFloat
    val count = validationData.count()

    println("Accuracy = " + correct / count)
    println("Training took " + (trainingEndTime - trainingStartTime))
    println("Validation took " + (predictEndTime - predictStartTime))

  }

  /**
   * logic to rotate 3-d matrix by 90
   * @param arr - 1d array to be rotated
   * @return - 1d array after rotation
   */
  def rotate90(arr: Array[String]): Array[String] = {
    return arr.take(arr.length - 1)
      .toList.grouped(441).toList
      .transpose
      .map(arr => arr.reverse).flatten.toArray

  }

  /**
   * logic to rotate 3-d matrix by 180
   * @param arr - 1d array to be rotated
   * @return - 1d array after rotation
   */
  def rotate180(arr: Array[String]): Array[String] = {
    return arr.take(arr.length - 1).toList
      .grouped(441).toList.toArray
      .map(arr => arr.reverse).flatten
  }

  /**
   * logic to rotate 3-d matrix by 270
   * @param arr - 1d array to be rotated
   * @return - 1d array after rotation
   */
  def rotate270(arr: Array[String]): Array[String] = {
    return rotate90(arr).toList
      .grouped(441).toList.toArray
      .map(arr => arr.reverse).flatten

  }

}