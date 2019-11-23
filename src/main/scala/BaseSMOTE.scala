package com.iresium.ml

import org.apache.spark.sql.DataFrame

class BaseSMOTE(val sampling_strategy: String = "auto",
                val random_state: Int = 0,
                val k_neighbors: Int = 5,
                val ratio: Float = 0
               ) {

    def _make_samples(dataX: DataFrame,
                      y_dtype: String = "int",
                      y_type: Integer,
                      nn_data: DataFrame,
                      nn_num: DataFrame,
                      n_samples: Integer,
                      step_size: Float = 1) = {

        val r = new scala.util.Random(random_state)
        val samples_indices = (for (i <- 0 to n_samples) yield r.nextInt(nn_num.count().toInt*nn_num.columns.size.toInt)).toList

        val uniform_samples = UniformDist.getSamples(n_samples)
        val steps = (for (i <- 0 to uniform_samples.size-1) yield uniform_samples(i)*step_size).toArray

        val rows = (for(i <- 0 to samples_indices.size-1) yield samples_indices(i)/nn_num.columns.size.toInt).toArray
        val cols = (for(i <- 0 to samples_indices.size-1) yield samples_indices(i)%nn_num.columns.size.toInt).toArray

        val y = (for(i <- 0 to samples_indices.size-1) yield y_type).toArray
    }
}
