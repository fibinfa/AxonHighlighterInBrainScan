# CS6240_FinalProject
Final Project for the Course Parallel Data Processing - CS6240



1) Local execution :
Files:
Source Code : src

Training -> ModelTraining.scala
Prediction -> AxonPixelPrediction.scala


Execution using Eclipse

Steps to run

   1. Create a maven project.
   2. Create input directory and put training data inside Training folder
   3. Put validation data inside Validation folder
   4. Update the pom.xml files with dependencies and rebuild the maven project.
   5. Go to Run Configuration and provide the input file path and output filepath as argument.
   6. Run the program (ModelTraining.scala)
   7. Check the accuracy in console
   8. Tune the parameters and run the program again until you get the desired accuracy
   9. Once desired accuracy is achieved and you fixed the model, run AxonPixelPrediction.scala
   10. Give the input parameter as input folder and output folder location
   11. Output will be generated in the output folder	


NOTE: Transformation code is written for 90,180 and 270 rotation of neighboring vector, you just
need to uncomment that piece


_________________________________________________________________________________________________________________________________


2)AWS Execution

Steps to Run PageRank Program in AWS
   1. Create s3 bucket and upload input files into input folder(subfolders: Training, Validation and Prediction)
   2. Mention the number of nodes required in the make file
   3. Give the main class name and subnet id correctly
   4. Run make cloud command
   5. Check the output in the output directory mentioned

Logs of AWS execution is provided inside AWS folder
	 AWS -> syslog

Output is given in  Output folder



**Using Makefile**

* Copy the input files in the "input" folder present in the same directory of the Makefile.
* To clean the local output folder (present in the same folder as of the Makefile):
  * make clean-local-output
* To generate the jar file from source code:
  * make jar
* To run the job
  * make alone

**Steps for Running on Cloud (AWS-EMR):**
* Make S3 Bucket
  * make make-bucket
* Upload input to S3 bucket
  * make upload-input-aws
* Running Program
  * make cloud
* Download output
  * make download-output
* To delete local output folder content
  * make clean-local-output
* Remove output from aws S3 Bucket
  * make delete-output-aws

_________________________________________________________________________________________________________________________________
