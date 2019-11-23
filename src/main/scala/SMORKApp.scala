package com.iresium.ml

import org.apache.spark.sql.SparkSession

object SMORKApp extends App {

    val spark = SparkSession.builder().master("local").appName("SparkSMOTE").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val df = spark.createDataFrame(Seq(
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(12, 1.1, 0.1)),
        (2, "W", org.apache.spark.ml.linalg.Vectors.dense(2.0, 101.1, -11.0)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(12, 1.3, 1.1)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(13, 1.2, -0.5)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(11, 1.2, -0.5)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(11.1, 1.1, -0.5)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(11.5, 2.2, -0.5)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(11, 1.2, -1.5)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(11.8, 1.3, -0.5)),
        (2, "W", org.apache.spark.ml.linalg.Vectors.dense(0.0, 101.2, -10.5)),
        (2, "W", org.apache.spark.ml.linalg.Vectors.dense(1.5, 100.1, -8.5)),
        (2, "W", org.apache.spark.ml.linalg.Vectors.dense(1.25, 103, -9.5)),
        (2, "W", org.apache.spark.ml.linalg.Vectors.dense(1.9, 104, -9.0)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(14, 1.2, -0.5)),
        (1, "W", org.apache.spark.ml.linalg.Vectors.dense(12, 1.1, -0.5)),
        (3, "W", org.apache.spark.ml.linalg.Vectors.dense(-22.5, 1.2, -0.5)),
        (3, "W", org.apache.spark.ml.linalg.Vectors.dense(-22.0, 2.2, -0.1)),
        (3, "W", org.apache.spark.ml.linalg.Vectors.dense(-22.1, 2.24, -1.1)),
        (3, "W", org.apache.spark.ml.linalg.Vectors.dense(-25, 3, -2.1))
    )).toDF("myLabel", "WCol", "myFeatures")

    df.groupBy("myLabel").count().orderBy("myLabel").show()

    val smote = new SMOTE()
    smote.setfeatureCol("myFeatures").setlabelCol("myLabel").setbucketLength(100)

    val smoteModel = smote.fit(df)

    val newDF = smoteModel.transform(df)

    newDF.groupBy("myLabel").count().orderBy("myLabel").show()
    spark.close()
}
