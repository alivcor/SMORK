package com.iresium.ml

import org.apache.spark.ml.Estimator
import org.apache.spark.ml.feature.BucketedRandomProjectionLSH
import org.apache.spark.ml.param._
import org.apache.spark.ml.util._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset}

import scala.collection.JavaConversions._



class SMOTE(override val uid: String) extends Estimator[SMOTEModel] with SMOTEParams
    with DefaultParamsWritable {

    def setfeatureCol(value: String): this.type = set(featureCol, value)

    def setlabelCol(value: String): this.type = set(labelCol, value)

    def setbucketLength(value: Int): this.type = set(bucketLength, value)

    def setnumHashTables(value: Int): this.type = set(numHashTables, value)

    def settransformLabel(value: Any): this.type = set(transformLabel, value)

    def setrandomState(value: Int): this.type = set(randomState, value)

    def setstepSize(value: Int): this.type = set(stepSize, value)

    def setminorityLabels(value: Array[String]): this.type = set(minorityLabels, value)

    def this() = this(Identifiable.randomUID("SMOTE"))

    override def copy(extra: ParamMap): SMOTE = {
        defaultCopy(extra)
    }

    override def transformSchema(schema: StructType): StructType = {
        val idx = schema.fieldIndex($(featureCol))
        val field = schema.fields(idx)
        if(field.dataType != StringType) {
            throw new Exception(s"Input Type incorrect")
        }
        schema.add(StructField($(labelCol), IntegerType, false))
    }

    def KNNCalculation(
                      frame: DataFrame,
                      feature: String,
                      reqrows: Long,
                      BucketLength: Double,
                      NumHashTables: Int): DataFrame = {
        val indexedFrame = frame.withColumn("index", row_number().over(Window.partitionBy($(labelCol)).orderBy($(labelCol))))
        val randomProjectionLSH = new BucketedRandomProjectionLSH()
                .setBucketLength(BucketLength)
                .setNumHashTables(NumHashTables)
                .setInputCol(feature)
                .setOutputCol("values")

        val _lshModel = randomProjectionLSH.fit(indexedFrame)
        val similarityMatrix = _lshModel.approxSimilarityJoin(_lshModel.transform(indexedFrame),
            _lshModel.transform(indexedFrame), 2000000000.0)

        similarityMatrix.selectExpr("datasetA.index as id1",
            s"datasetA.${$(featureCol)} as k1",
            "datasetB.index as id2",
            s"datasetB.${$(featureCol)} as k2",
            "distCol").filter("distCol > 0.0").orderBy("id1", "distCol").dropDuplicates().limit(reqrows.toInt)
    }


    def _generate_sample(key1: org.apache.spark.ml.linalg.Vector, key2:
                        org.apache.spark.ml.linalg.Vector) = {
        val resArray = Array(key1, key2)
        val res = key1.toArray.zip(key2.toArray.zip(key1.toArray).map(x => x._1 - x._2).map(_*0.2)).map(x => x._1 + x._2)

        org.apache.spark.ml.linalg.Vectors.dense(res)
    }

    override def fit(dataset: Dataset[_]): SMOTEModel = {
        import dataset.sparkSession.implicits._

        val words = Array("")

        val groupedDataDF = dataset.groupBy($(labelCol)).count()
        val majorityClassCount = groupedDataDF.agg(max("count")).head().getLong(0)

        val minorityClasses = groupedDataDF.filter(s"count < ${majorityClassCount}").select($(labelCol)).collectAsList().map(_.toSeq.toList(0)).toList

        logInfo(s"Identified Minority Classes : ${minorityClasses}")

        val frames = dataset.select($(featureCol), $(labelCol)).filter(dataset.col($(labelCol)).isin(minorityClasses:_*))

        val rowCounts: scala.collection.immutable.Map[Any, Long] = minorityClasses.map(c => (c, frames.filter(frames.col($(labelCol)) === c).count())).toMap

        val reqRows = rowCounts map {case (k, v) => (k, majorityClassCount - v)}

        var _smotedDPs: scala.collection.immutable.Map[Any, DataFrame] = Map()

        logInfo(s"Required Rows : " + reqRows.toString())

        val createRandomDataPoint = udf(_generate_sample _)

        for((minorityClass, reqrows) <- reqRows){

            val r = new scala.util.Random(($(randomState)))
            val uniform_samples = UniformDist.getSamples(reqrows.toInt)
            val steps = (for (i <- 0 to uniform_samples.size - 1) yield uniform_samples(i)*$(stepSize)).toArray

            val frame = frames.filter(frames.col($(labelCol)) === minorityClass)


            val canGenerate = scala.math.pow(frame.count, 2) - frame.count


            if(canGenerate < reqrows) { logWarning(s"Number of datapoints in class ${minorityClass} is too less (${frame.count}) to generate ${reqrows.toString} datapoints. We can only generate ${canGenerate}   ")}
            val nnarray = KNNCalculation(frame, $(featureCol), reqrows, $(bucketLength), $(numHashTables))

            val syntheticRaw = nnarray.withColumn($(featureCol), createRandomDataPoint($"k1", $"k2")).select($(featureCol))
            val synethetic = syntheticRaw.withColumn($(labelCol), lit(minorityClass)).select($(labelCol), $(featureCol)).toDF()

            _smotedDPs += (minorityClass -> synethetic)

        }

        new SMOTEModel(uid, _smotedDPs, reqRows)
    }



}
