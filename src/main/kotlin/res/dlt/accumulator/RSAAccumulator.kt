package res.dlt.accumulator

import java.math.BigInteger

data class RSAAccumulator(
        val a: BigInteger,
        private val a0: BigInteger,
        private val p: BigInteger,
        private val q: BigInteger,
        private val n: BigInteger,
        private val data: Map<BigInteger, BigInteger>
) {

    companion object {
        private const val RSA_KEY_SIZE = 3072
        private const val RSA_PRIME_SIZE = RSA_KEY_SIZE / 2

        fun newInstance(
                seed1: Long = 1234567890123456789,
                seed2: Long = 1098765432109876543,
                seed3: Long = 5647382910293847565,
        ): RSAAccumulator {
            val (p, q) = generateTwoDistinctPrimes(
                    bitLength = RSA_PRIME_SIZE,
                    seed1 = seed1,
                    seed2 = seed2)
            val n = p * q

            // draw random number within range of [0,n-1]
            val a0 = randomBigInteger(BigInteger.ZERO, n, seed3)
            val a = a0

            return RSAAccumulator(a, a0, p, q, n, mapOf())
        }
    }
}

fun add(x: BigInteger): BigInteger = TODO()

fun delete(x: BigInteger): BigInteger = TODO()

fun batchUpdate(xs: List<BigInteger>): List<BigInteger> = TODO()

fun createProof(x: BigInteger): Boolean = TODO()

fun isMember(x: BigInteger): Boolean = TODO()