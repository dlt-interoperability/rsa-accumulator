package res.dlt.accumulator

import arrow.core.*
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
        private const val RSA_MOD_SIZE = 1024
        private const val RSA_PRIME_SIZE = RSA_MOD_SIZE / 2 - 1
        const val PRIME_CERTAINTY = 5

        fun newInstance(
                seed1: Long = 1234567890123456789,
                seed2: Long = 1098765432109876543,
                seed3: Long = 5647382910293847565,
        ): RSAAccumulator {
            val (p, q) = generateTwoDistinctSafePrimes(
                    bitLength = RSA_PRIME_SIZE,
                    seed1 = seed1,
                    seed2 = seed2,
                    certainty = PRIME_CERTAINTY)
            val n = p * q
            val phi = (p - BigInteger.ONE) * (q - BigInteger.ONE)

            // Select a quadratic residue modulo n to use as the generator for the accumulator.
            // An integer is a quadratic residue modulo n if there exists an integer x such that
            // x^2 is congruent to q mod n.
            val a0 = randomBigInteger(BigInteger.ZERO, n, seed3).pow(2).mod(n)
            val a = a0

            return RSAAccumulator(a, a0, p, q, n, phi, hashMapOf())
        }
    }
}

fun add(key: BigInteger) = State<RSAAccumulator, BigInteger> { accumulator ->
    if (accumulator.data.containsKey(key)) {
        Tuple2(accumulator, key)
    } else {
        val (keyPrime, nonce) = hashToPrime(key)
        val newAccumulatorVal = accumulator.a.modPow(keyPrime, accumulator.n)
        accumulator.data[key] = nonce
        val newAccumulator = accumulator.copy(a = newAccumulatorVal)
        Tuple2(newAccumulator, key)
    }
}

/**
 * The delete function uses auxiliary information about the RSA modulus to remove the element from the
 * accumulator in time independent of the accumulated set. The accumulator without the element is
 * z' = z^{x^-1 mod phi(N)} mod N. Phi(N) is the Euler totient function phi(N) = (p - 1)(q - 1).
 */
fun delete(key: BigInteger) = State<RSAAccumulator, BigInteger> { accumulator ->
    if (!accumulator.data.containsKey(key)) {
        Tuple2(accumulator, key)
    } else {
        val (keyPrime, _) = hashToPrime(key)
        val keyInverseModPhi = inverseModPhi(x = keyPrime, phi = accumulator.phi)
        val newAccumulatorVal = accumulator.a.modPow(keyInverseModPhi, accumulator.n)
        accumulator.data.remove(key)
        val newAccumulator = accumulator.copy(a = newAccumulatorVal)
        Tuple2(newAccumulator, key)
    }
}

/**
 * The createProof function creates a proof of membership of an element in the accumulator.
 * It uses auxiliary information about the accumulator to create the proof in time independent
 * of the accumulated set. The proof of membership is the accumulator without the accumulated
 * element, and is calculated the same way an element is deleted from the set. The proof (pi)
 * can be verified in constant time by checking that pi ^ x mod N = z.
 */
fun createProof(key: BigInteger) = State<RSAAccumulator, Either<Error, Proof>> { accumulator ->
    if (!accumulator.data.containsKey(key)) {
        Tuple2(accumulator, Left(Error("Accumulator does not contain $key")))
    } else {
        val (keyPrime, _) = hashToPrime(key)
        val keyInverseModPhi = inverseModPhi(x = keyPrime, phi = accumulator.phi)
        val proof = accumulator.a.modPow(keyInverseModPhi, accumulator.n)
        Tuple2(accumulator, Right(Proof(
                key = key,
                a = accumulator.a,
                proof = proof,
                n = accumulator.n
        )))
    }
}

fun verifyProof(proof: BigInteger, key: BigInteger, n: BigInteger, a: BigInteger): Boolean {
    val (keyPrime, _) = hashToPrime(key)
    return proof.modPow(keyPrime, n) == a
}

data class Proof(
        val key: BigInteger,
        val a: BigInteger,
        val proof: BigInteger,
        val n: BigInteger
)