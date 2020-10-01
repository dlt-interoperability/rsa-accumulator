package res.dlt.accumulator

import arrow.core.Tuple2
import arrow.mtl.State
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

            // Select a quadratic residue modulo n to use as the generator for the accumulator.
            // An integer is a quadratic residue modulo n if there exists an integer x such that
            // x^2 = q mod n (where the "=" is congruence).
            val a0 = randomBigInteger(BigInteger.ZERO, n, seed3).pow(2).mod(n)
            val a = a0

            return RSAAccumulator(a, a0, p, q, n, hashMapOf())
        }
    }
}

fun add(x: BigInteger) = State<RSAAccumulator, BigInteger> { accumulator ->
    if (accumulator.data.containsKey(x)) {
        Tuple2(accumulator, x)
    } else {
        val (hashPrime, nonce) = hashToPrime(x)
        val accumulatorValue = accumulator.a.modPow(hashPrime, accumulator.n)
        accumulator.data[hashPrime] = nonce
        val newAccumulator = accumulator.copy(a = accumulatorValue)
        Tuple2(newAccumulator, x)
    }
}

fun delete(x: BigInteger) = State<RSAAccumulator, BigInteger> { accumulator ->
    Tuple2(accumulator, x)
}

fun createProof(x: BigInteger): Boolean = TODO()

fun isMember(x: BigInteger) = State<RSAAccumulator, Boolean> { accumulator ->
    Tuple2(accumulator, accumulator.data.containsKey(hashToPrime(x).a))
}