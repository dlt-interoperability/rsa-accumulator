package res.dlt.accumulator

import arrow.core.*
import com.google.common.hash.Hashing
import com.google.common.io.BaseEncoding
import java.math.BigInteger

/**
 * This function generates two distinct safe primes with the specified
   bitlength. A safe prime, p, is one that fulfills p = 2p' + 1, where p' is
   also a prime.
 */
tailrec fun generateTwoDistinctSafePrimes(
        bitLength: Int,
        candidates: Tuple2<BigInteger, BigInteger>? = null,
        seed1: Long,
        seed2: Long,
        certainty: Int
): Tuple2<BigInteger, BigInteger> = when {
    // Generate two candidate safe primes
    candidates == null -> {
        val (newSeed1, safePrime1) = generateSafePrime(bitLength, seed1, certainty)
        val (newSeed2, safePrime2) = generateSafePrime(bitLength, seed2, certainty)
        val newCandidates = Tuple2(safePrime1, safePrime2)
        generateTwoDistinctSafePrimes(bitLength, newCandidates, newSeed1, newSeed2, certainty)
    }
    // If they are distinct, return them
    // TODO: make this a check that their diff is within a certain range - not too close, not too far.
    candidates.a != candidates.b -> candidates
    // If they are equal, find another candidate safe prime.
    else -> {
        val (newSeed2, newSafePrime) = generateSafePrime(bitLength, seed2+1, certainty)
        val newCandidates = Tuple2(candidates.a, newSafePrime)
        generateTwoDistinctSafePrimes(bitLength, newCandidates, seed1, newSeed2, certainty)
    }
}

/**
 * A safe prime, p, is a prime that fulfils p = 2p' + 1, where p' is also a prime. Generating a safe
 * prime is done by checking that a candidate p created from a random prime p' is prime. A probabilistic
 * primality check is used - isProbablePrime uses the Miller-Rabin primality test. The Miller-Rabin
 * primality test is quite expensive, so performing a sieve step to quickly filter out non-primes is
 * done first. The sieve checks whether p' and p are congruent to 2 modulo 3.
 */
tailrec fun generateSafePrime(bitLength: Int, seed: Long, certainty: Int): Pair<Long, BigInteger> {
    val prime = BigInteger.probablePrime(bitLength, java.util.Random(seed))
    return if (prime.mod(BigInteger("3")) == BigInteger("2")) {
        val safePrimeCandidate = prime * BigInteger("2") + BigInteger.ONE
        if (safePrimeCandidate.mod(BigInteger("3")) == BigInteger("2")) {
            if (safePrimeCandidate.isProbablePrime(certainty)) {
                Pair(seed, safePrimeCandidate)
            } else {
                generateSafePrime(bitLength, seed + 1, certainty)
            }
        } else {
            generateSafePrime(bitLength, seed + 1, certainty)
        }
    } else {
        generateSafePrime(bitLength, seed + 1, certainty)
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
    return if (candidatePrime > BigInteger.ZERO && candidatePrime.isProbablePrime(RSAAccumulator.PRIME_CERTAINTY)) {
        Tuple2(candidatePrime, candidateNonce)
    } else {
        hashToPrime(x, candidateNonce + BigInteger.ONE)
    }
}

/**
 * The inverseModPhi function calculates the modular inverse of x mod phi(n),
 * which is the Euler totient function: phi(n) = (p - 1)(q - 1). When the accumulator
 * is raised to the power of the inverse mod phi, this effectively removes element
 * from the accumulator. This is used when deleting an element from the set
 * and when creating a proof of membership.
 *
 * @param x This is the prime representation of a key.
 * @param n This is the RSA modulus for the accumulator.
 * @param phi This is the value of the Euler totient function phi(n) = (p - 1)(q - 1).
 */
fun inverseModPhi(x: BigInteger, phi: BigInteger): BigInteger {
    val (d, _, xPrimeInverse) = extendedEuclid(phi, x)
    if (d != BigInteger.ONE) {
        println("$x is not coprime to $phi! Greatest common divisor is $d")
    }
    return xPrimeInverse.mod(phi)
}

/**
 * Given integers a and b where a >= b >= 0, the extended Euclid algorithm
 * can be used to find their greatest common divisor, d, and integers s and t
 * that fulfill as + bt = d. When a and b are coprime, meaning their greatest
 * common divisor is 1, the integer t is the inverse of b modulo a.
 */
tailrec fun extendedEuclid(
        a: BigInteger,
        b: BigInteger,
        s: BigInteger = BigInteger.ONE,
        sPrime: BigInteger = BigInteger.ZERO,
        t: BigInteger = BigInteger.ZERO,
        tPrime: BigInteger = BigInteger.ONE
): Tuple3<BigInteger, BigInteger, BigInteger> =
        if (b == BigInteger.ZERO) {
            Tuple3(a, s, t)
        } else {
            val q = a / b // this is the floor of a / b
            extendedEuclid(
                    a = b,
                    s = sPrime,
                    t = tPrime,
                    b = a % b,
                    sPrime = s - sPrime * q,
                    tPrime = t - tPrime * q
            )
        }

/**
 * hashStringToBigInt is used to create a fixed-length BigInteger
 * representation of an arbitrarily long message string.
 */
fun hashStringToBigInt(message: String): BigInteger {
    val hashHexString = BaseEncoding
            .base16()
            .lowerCase()
            .encode(Hashing
                    .sha256()
                    .hashBytes(message.toByteArray())
                    .asBytes())
    return BigInteger(hashHexString, 16)
}