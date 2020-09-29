package res.dlt.accumulator

import arrow.core.Tuple2
import java.math.BigInteger
import kotlin.math.pow

/**
 * This function generates two distinct primes with the specified bitlength.
 */
tailrec fun generateTwoDistinctPrimes(
        bitLength: Int,
        candidates: Tuple2<BigInteger, BigInteger>? = null,
        seed1: Long,
        seed2: Long
): Tuple2<BigInteger, BigInteger> = when {
    candidates == null -> {
        val newCandidates = Tuple2(
            generateProbablePrime(bitLength, seed1),
            generateProbablePrime(bitLength, seed2))
        generateTwoDistinctPrimes(bitLength, newCandidates, seed1, seed2)
    }
    candidates.a == candidates.b -> {
        val newCandidates = Tuple2(candidates.a, generateProbablePrime(bitLength, seed2))
        generateTwoDistinctPrimes(bitLength, newCandidates, seed1, seed2 + 1)
    }
    (candidates.a - candidates.b).toDouble() < bitLength.toDouble().pow(0.25) -> {
        candidates
    }
    else -> {
        val newCandidates = Tuple2(candidates.a, generateProbablePrime(bitLength, seed2))
        generateTwoDistinctPrimes(bitLength, newCandidates, seed1, seed2 + 1)
    }
}

fun generateProbablePrime(bitLength: Int, seed: Long): BigInteger =
        BigInteger.probablePrime(bitLength, java.util.Random(seed))

tailrec fun randomBigInteger(
        from: BigInteger,
        until: BigInteger,
        seed: Long
): BigInteger =  if (from >= until) {
    println("'from' ($from) must be smaller than 'until' ($until). Using 0 as lower bound instead.")
    randomBigInteger(BigInteger.ZERO, until, seed)
} else {
    val randomNumber = BigInteger(until.bitLength(), java.util.Random(seed))
    if (randomNumber > from && randomNumber < until) {
        randomNumber
    } else {
        randomBigInteger(from, until, seed + 1)
    }
}