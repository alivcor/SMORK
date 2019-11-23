package com.iresium.ml

import org.apache.spark.ml.param._




trait SMOTEParams extends Params {
  final val featureCol = new Param[String](this, "featureCol", "The features Column - should be a Dense Vector")
  final val labelCol = new Param[String](this, "labelCol", "The labels column.")
  final val bucketLength = new Param[Int](this, "bucketLength", "Bucket Length for LSH - The length of each hash bucket, a larger bucket lowers the false negative rate")
  final val numHashTables = new Param[Int](this, "numHashTables", "The number of hash tables for LSH - used in OR-amplification")
  final val randomState = new Param[Int](this, "randomState", "Optional Random State for step")
  final val stepSize = new Param[Int](this, "stepSize", "Optional step size")
  final val transformLabel = new Param[Any](this, "transformLabel", "Optional minority Label to transform if not all")
  final val minorityLabels: StringArrayParam = new StringArrayParam(this, "minorityLabels", "Optional minorty labels")


  setDefault(transformLabel, None)
  setDefault(featureCol, "features")
  setDefault(labelCol, "label")
  setDefault(bucketLength, 1)
  setDefault(numHashTables, 1)
  setDefault(randomState, 0)
  setDefault(stepSize, 1)


  final def getfeatureCol() = $(featureCol)
  final def gettransformLabel() = $(transformLabel)
  final def getlabelCol() = $(labelCol)
  final def getbucketLength() = $(bucketLength)
  final def getnumHashTables() = $(numHashTables)
  final def getrandomState() = $(randomState)
  final def getstepSize() = $(stepSize)


}
