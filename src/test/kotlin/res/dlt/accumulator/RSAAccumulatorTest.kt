package res.dlt.accumulator

import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import arrow.mtl.run
import java.math.BigInteger
import kotlin.test.*

class RSAAccumulatorTest {
    @Test fun createRSAAccumulator() {
        val accumulator = RSAAccumulator.newInstance()
        assertNotNull(accumulator.a, "accumulator should have property a")
    }

    @Test fun addElement() {
        val accumulator = RSAAccumulator.newInstance()
        val elementToAdd = BigInteger.ONE
        val notAddedElement = BigInteger.TEN
        val (newAccumulator, addedElement) = add(elementToAdd).run(accumulator)
        assertFalse(newAccumulator.data.contains(notAddedElement), "Element not added should not be member")
        assertNotEquals(accumulator, newAccumulator,
                "Initial accumulator and accumulator after addition should be different.")
        assertEquals(elementToAdd, addedElement, "Function should return added element.")
        assertTrue(newAccumulator.data.contains(elementToAdd), "New accumulator should contain added element.")
    }

    @Test fun deleteElement() {
        val accumulator = RSAAccumulator.newInstance()

        listOf(BigInteger("2"),
                BigInteger("3"),
                BigInteger("4"),
                BigInteger("5"),
                BigInteger("6"),
                BigInteger("7"),
                BigInteger("8"))
                .zipWithNext { element1, element2 -> Pair(element1, element2) }
                .fold(accumulator, ::addAndDelete)
    }

    @Test fun createAndVerifyProof() {
        val accumulator = RSAAccumulator.newInstance()

        listOf(BigInteger("2"),
                BigInteger("3"),
                BigInteger("4"),
                BigInteger("5"),
                BigInteger("6"),
                BigInteger("7"),
                BigInteger("8")).fold(accumulator, ::addCreateProofAndVerify)
    }
}

fun addCreateProofAndVerify(accumulator: RSAAccumulator, element: BigInteger): RSAAccumulator {
    val newAccumulator = add(element).run(accumulator).a
    createProof(element).run(newAccumulator).b.map {
        assertTrue(verifyProof(it.proof, it.key, it.n, it.a), "Proof should be valid")
    }
    return newAccumulator
}

fun addAndDelete(accumulator: RSAAccumulator, elementPair: Pair<BigInteger, BigInteger>): RSAAccumulator {
    val newAcc1 = add(elementPair.first).run(accumulator).a
    val newAcc2 = add(elementPair.second).run(newAcc1).a
    assertNotEquals(newAcc1.a, newAcc2.a, "The accumulators with elements 1 and 1 and 2 should be different")
    assertTrue(newAcc2.data.contains(elementPair.second), "Accumulator 2 should contain second element")
    val newAcc3 = delete(elementPair.second).run(newAcc2).a
    assertEquals(newAcc1.a, newAcc3.a,
            "The accumulator with element 2 deleted should be the same as the one before it was added")
    assertFalse(newAcc3.data.contains(elementPair.second), "Accumulator 3 should not contain second element")
    return newAcc3
}