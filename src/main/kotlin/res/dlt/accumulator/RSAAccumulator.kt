package res.dlt.accumulator

import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.mtl.State
import java.math.BigInteger

data class RSAAccumulator(
        val a: BigInteger,
        val a0: BigInteger,
        val p: BigInteger,
        val q: BigInteger,
        val n: BigInteger,
        val phi: BigInteger,
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
            val phi = (p - BigInteger.ONE) * (q - BigInteger.ONE)

            // Select a quadratic residue modulo n to use as the generator for the accumulator.
            // An integer is a quadratic residue modulo n if there exists an integer x such that
            // x^2 = q mod n (where the "=" is congruence).
            val a0 = randomBigInteger(BigInteger.ZERO, n, seed3).pow(2).mod(n)
            val a = a0

            return RSAAccumulator(a, a0, p, q, n, phi, hashMapOf())
        }
    }
}

fun add(x: BigInteger) = State<RSAAccumulator, BigInteger> { accumulator ->
    if (accumulator.data.containsKey(x)) {
        Tuple2(accumulator, x)
    } else {
        val (hashPrime, nonce) = hashToPrime(x)
        val newAccumulatorVal = accumulator.a.modPow(hashPrime, accumulator.n)
        accumulator.data[x] = nonce
        val newAccumulator = accumulator.copy(a = newAccumulatorVal)
        Tuple2(newAccumulator, x)
    }
}

/**
 * The delete function uses auxiliary information about the RSA modulus to remove the element from the
 * accumulator in time independent of the accumulated set. The accumulator without the element is
 * z' = z^{x^-1 mod phi(N)} mod N. Phi(N) is the Euler totient function phi(N) = (p - 1)(q - 1).
 * The inverse of x is calculated using the extended Euclid algorithm.
 */
fun delete(x: BigInteger) = State<RSAAccumulator, BigInteger> { accumulator ->
    val (hashPrime, _) = hashToPrime(x)
    val (_, _, xPrimeInverse) = extendedEuclid(accumulator.n, hashPrime)
    val xInverseModPhi = xPrimeInverse.mod(accumulator.phi)
    val newAccumulatorVal = accumulator.a.modPow(xInverseModPhi, accumulator.n)
    accumulator.data.remove(x)
    val newAccumulator = accumulator.copy(a = newAccumulatorVal)
    Tuple2(newAccumulator, x)
}

fun createProof(x: BigInteger): Boolean = TODO()

fun isMember(x: BigInteger) = State<RSAAccumulator, Boolean> { accumulator ->
    Tuple2(accumulator, accumulator.data.containsKey(x))
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