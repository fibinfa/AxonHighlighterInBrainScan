package edu.neu.cs6240.axontracer
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD


object AxonPixelPrediction {

  def main(args: Array[String]) = {
    //initialise config - "spark master = yarn for EMR exec, local for local execution"
    val conf = new SparkConf()
      .setAppName("model training")
      .setMaster("local")
    //initialise the Spark context
    val sc = new SparkContext(conf)
    val jobStartTime = System.currentTimeMillis()
    println("Job started at" + jobStartTime)
    //parsing the input file and converting to labelled point where
    //label is the flag and vector is the neighborhood
   
    val trainingData = sc.textFile(args(0) + "/*.csv")
      .map(row => row.split(","))
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(arr.take(arr.length - 1).
          map(str => str.toDouble))))
          
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 3 // Use more in practice.
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val impurity = "gini" // for classification
    val maxDepth = 4
    val maxBins = 32

     //model is created and stored as an rdd     
    val trainedModel =RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    
    // traing is done
     
      
     //validation 
      val validationData = sc.textFile(args(1) + "/*.csv")
      .map(row => row.split(","))
      .map(arr => new LabeledPoint(
        arr.last.toDouble,
        Vectors.dense(arr.take(arr.length - 1).
          map(str => str.toDouble))))
      val predictions =validationData.map(row => trainedModel.predict(row.features).toInt)
      predictions.coalesce(1).saveAsTextFile(args(2))
      
    

  }

//  def randomForestTraining(sc: SparkContext, path: String, trainingData: RDD[LabeledPoint]) = {
//    // Train a RandomForest model.
//    // Empty categoricalFeaturesInfo indicates all features are continuous.
//    val numClasses = 2
//    val categoricalFeaturesInfo = Map[Int, Int]()
//    val numTrees = 3 // Use more in practice.
//    val featureSubsetStrategy = "auto" // Let the algorithm choose.
//    val impurity = "gini" // for classification
//    val maxDepth = 4
//    val maxBins = 32
//
//    val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
//      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
//      
//    //model.save(sc, path)
//      return model
//
//  }
}