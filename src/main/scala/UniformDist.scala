package com.iresium.ml

trait Distribution[A] {
    def get: A

    def sample(n: Int): List[A] = {
        List.fill(n)(this.get)
    }
}

object UniformDist {
    val uniform = new Distribution[Double] {
        private val rand = new java.util.Random()
        override def get = rand.nextDouble()
    }

    def getSamples(cnt: Int) = {
        uniform.sample(cnt).toList
    }
}