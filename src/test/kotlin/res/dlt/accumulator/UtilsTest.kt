package res.dlt.accumulator

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UtilsTest {

    @Test fun extendedEuclidTest() {
        val primes = listOf(37,41,47,53,59,61,67,71,73,79,83,89,97)
        val safePrimes = listOf(5,7,11,23,47,59,83,107,167,179,227,263,347)

        safePrimes.zipWithNext { prime1, prime2 ->
            val p = BigInteger.valueOf(prime1.toLong())
            val q = BigInteger.valueOf(prime2.toLong())
            val n = p * q
            primes.map { element ->
                val elementAsBigInt = BigInteger.valueOf(element.toLong())
                if (elementAsBigInt != p && elementAsBigInt != q) {
                    val (d, _, t) = extendedEuclid(n, elementAsBigInt)
//                    println("d: $d, s: $s, t: $t")
//                    println("t is inverse of element $element: ${(t * elementAsBigInt).mod(n)} ")
                    assertEquals(BigInteger.ONE, d)
                    assertEquals(BigInteger.ONE, (t * elementAsBigInt).mod(n))
                }
            }
        }
    }

    /**
     * This function fails when 83 is included in the safe prime list. This is because
     * when p and 1 are 59 and 83, phi is 4,756 and 41 is a factor of phi.
     */
    @Test fun modularFactorizationWithPhi() {
        val primes = listOf(37,41,47,53,59,61,67,71,73,79,83,89,97)
        val safePrimes = listOf(5,7,11,23,47,59)

        safePrimes.zipWithNext { prime1, prime2 ->
            val p = BigInteger.valueOf(prime1.toLong())
            val q = BigInteger.valueOf(prime2.toLong())
            val n = p * q
            val phi = (p - BigInteger.ONE) * (q - BigInteger.ONE)
            val g = BigInteger("100").pow(2).mod(n)
            val g1 = g.modPow(BigInteger.valueOf(primes[0].toLong()), n)
//            println("n: $n, phi: $phi, g: $g, g1: $g1")
            primes.map { element ->
                val elementAsBigInt = BigInteger.valueOf(element.toLong())
                if (elementAsBigInt != p && elementAsBigInt != q) {
//                    println("g1 without $element is $g1")
                    val g2 = g1.modPow(elementAsBigInt, n)
                    val (_, _, t) = extendedEuclid(phi, elementAsBigInt)
//                    println("g2 with $element is $g2")
//                    println("d: $d, s: $s, t: $t")
//                    println("t is the modular inverse (where mod is phi $phi) of element $element if this is 1: ${(t * elementAsBigInt).mod(phi)}")
                    val g2Prime = g2.modPow(t.mod(phi), n)
//                    println("g2 ^ t mod n should be $g1: $g2 ^ $t mod $n = $g2Prime")
                    assertEquals(g1, g2Prime)
                }
            }
        }
    }
}