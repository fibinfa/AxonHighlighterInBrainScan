package edu.neu.cs6240.axontracer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD

//args
//0 - input folder -> Training & validation

object ModelTraining {

  def main(args: Array[String]) = {
    //initialise config - "spark master = yarn for EMR exec, local for local execution"
    val conf = new SparkConf()
      .setAppName("model training")
      .setMaster("yarn")
    //initialise the Spark context
    val sc = new SparkContext(conf)
    //parsing the input file and converting to labelled point where
    //label is the flag and vector is the neighborhood

    val trainingStartTime = System.currentTimeMillis()

    val trainingData = sc.textFile(args(0) + "/Training/*.csv")
      .map(row => row.split(","))
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(arr.take(arr.length - 1).
          map(str => str.toDouble))))

    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 30
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val impurity = "gini" // for classification
    val maxDepth = 20
    val maxBins = 100

    //model is created and stored as an rdd
    val trainingModel = RandomForest.trainClassifier(trainingData, numClasses,
      categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    //save in spark context
    //      trainingModel.save(sc, args(1))
    //      trainingData.unpersist()

    // training is done

    val trainingEndTime = System.currentTimeMillis()

    val predictStartTime = System.currentTimeMillis()

    //load the model from spark context
    //    val trainedModel = RandomForestModel.load(sc, args(1))

    //validation
    val validationData = sc.textFile(args(0) + "/Validation/*.csv")

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

}