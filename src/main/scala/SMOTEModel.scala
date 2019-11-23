package com.iresium.ml

import org.apache.spark.ml.Model
import org.apache.spark.ml.param._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset}


class SMOTEModel(
                override val uid: String,
                _smotedDPs: scala.collection.immutable.Map[Any, DataFrame],
                reqRows: scala.collection.immutable.Map[Any, Long]
                ) extends Model[SMOTEModel] with SMOTEParams {

    override def copy(extra: ParamMap): SMOTEModel = {
        val copied = new SMOTEModel(uid, _smotedDPs, reqRows)
        copyValues(copied, extra)
    }


    def setfeatureCol(value: String): this.type = set(featureCol, value)

    def setlabelCol(value: String): this.type = set(labelCol, value)

    def setbucketLength(value: Int): this.type = set(bucketLength, value)

    def setnumHashTables(value: Int): this.type = set(numHashTables, value)

    def settransformLabel(value: Any): this.type = set(transformLabel, value)

    def setrandomState(value: Int): this.type = set(randomState, value)

    def setstepSize(value: Int): this.type = set(stepSize, value)

    def setminorityLabels(value: Array[String]): this.type = set(minorityLabels, value)

    override def transformSchema(schema: StructType): StructType = {
        val idx = schema.fieldIndex($(featureCol))
        val field = schema.fields(idx)
        if(field.dataType != StringType) {
            throw new Exception(s"Input type ${field.dataType} did not match input type StringType")

        }

        schema.add(StructField($(labelCol), IntegerType, false))

    }


    def expr(myCols: Set[String], allCols: Set[String]) = {
        allCols.toList.map(x => x match {
            case x if myCols.contains(x) => col(x)
            case _ => lit(null).as(x)
        })
    }

    override def transform(dataset: Dataset[_]): DataFrame = {
        var df = dataset.toDF()
        val cols2 = df.columns.toSet

        if($(transformLabel) != None){
            val cols1 = _smotedDPs($(transformLabel)).columns.toSet
            val total = cols1 ++ cols2
            _smotedDPs($(transformLabel)).select(expr(cols1, total): _*).unionAll(df.select(expr(cols2, total):_*)).dropDuplicates()

        } else {
            for((minorityClass, reqrows) <- reqRows) {
                val cols1 = _smotedDPs(minorityClass).columns.toSet
                val total = cols1 ++ cols2
                df = _smotedDPs(minorityClass).select(expr(cols1, total):_*).unionAll(df.select(expr(cols2, total):_*)).dropDuplicates()

            }
            df
        }
    }
}
