package res.dlt.accumulator

import com.google.common.hash.Hashing
import arrow.core.Tuple2
import com.google.common.io.BaseEncoding
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
                BigInteger.probablePrime(bitLength, java.util.Random(seed1)),
                BigInteger.probablePrime(bitLength, java.util.Random(seed2)))
        generateTwoDistinctPrimes(bitLength, newCandidates, seed1, seed2)
    }
    candidates.a == candidates.b -> {
        val newCandidates = Tuple2(
                candidates.a,
                BigInteger.probablePrime(bitLength, java.util.Random(seed2)))
        generateTwoDistinctPrimes(bitLength, newCandidates, seed1, seed2 + 1)
    }
    (candidates.a - candidates.b).toDouble() < bitLength.toDouble().pow(0.25) -> {
        candidates
    }
    else -> {
        val newCandidates = Tuple2(
                candidates.a,
                BigInteger.probablePrime(bitLength, java.util.Random(seed2)))
        generateTwoDistinctPrimes(bitLength, newCandidates, seed1, seed2 + 1)
    }
}

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

/**
 * The hashToPrime function takes a BigInteger representation of an element
 * to be added to the accumulator and hashes it together with a nonce until
 * the resulting Big Integer is a prime number with a certain likelihood. This
 * function uses the BigInteger.isProbablePrime method to test for primality,
 * which under the hood uses the Miller–Rabin primality test.
 */
tailrec fun hashToPrime(
        x: BigInteger,
        candidateNonce: BigInteger = BigInteger.ZERO
): Tuple2<BigInteger, BigInteger> {
    val candidatePrime = BigInteger(Hashing.sha256().hashBytes((x + candidateNonce).toByteArray()).asBytes())
    return if (candidatePrime.isProbablePrime(RSAAccumulator.PRIME_CERTAINTY)) {
        Tuple2(candidatePrime, candidateNonce)
    } else {
        hashToPrime(x, candidateNonce + BigInteger.ONE)
    }
}
