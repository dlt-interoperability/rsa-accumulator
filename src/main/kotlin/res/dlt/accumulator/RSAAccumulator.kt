package res.dlt.accumulator

import arrow.core.Tuple2
import java.math.BigInteger

data class RSAAccumulator(
        val a: BigInteger,
        val a0: BigInteger,
        val p: BigInteger,
        val q: BigInteger,
        val n: BigInteger,
        val data: HashMap<BigInteger, BigInteger>
) {
    companion object {
        private const val RSA_MOD_SIZE = 3072
        private const val RSA_PRIME_SIZE = RSA_MOD_SIZE / 2
        const val PRIME_CERTAINTY = 5

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

            return RSAAccumulator(a, a0, p, q, n, hashMapOf())
        }
    }
}

fun add(
        accumulator: RSAAccumulator,
        x: BigInteger
): Tuple2<RSAAccumulator, BigInteger> = if (accumulator.data.containsKey(x)) {
    Tuple2(accumulator, x)
} else {
    val (hashPrime, nonce) = hashToPrime(x)
    val accumulatorValue = accumulator.a.modPow(hashPrime, accumulator.n)
    accumulator.data[hashPrime] = nonce
    val newAccumulator = accumulator.copy(a = accumulatorValue)
    Tuple2(newAccumulator, x)
}

fun delete(x: BigInteger): BigInteger = TODO()

fun createProof(x: BigInteger): Boolean = TODO()

fun isMember(accumulator: RSAAccumulator, x: BigInteger): Boolean = accumulator.data.containsKey(hashToPrime(x).a)