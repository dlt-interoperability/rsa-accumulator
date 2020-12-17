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
        val elementToAdd = "a"
        val primeElementToAdd = hashStringToBigInt(elementToAdd)
        val notAddedElement = "b"
        val primeElementNotAdded = hashStringToBigInt(notAddedElement)
        val (newAccumulator, addedElement) = add(primeElementToAdd).run(accumulator)
        assertFalse(newAccumulator.data.contains(primeElementNotAdded), "Element not added should not be member")
        assertNotEquals(accumulator, newAccumulator,
                "Initial accumulator and accumulator after addition should be different.")
        assertEquals(primeElementToAdd, addedElement, "Function should return added element.")
        assertTrue(newAccumulator.data.contains(primeElementToAdd), "New accumulator should contain added element.")
    }

    @Test fun deleteElement() {
        val accumulator = RSAAccumulator.newInstance()

        listOf("a","b","c","d","e","f","g","h")
                .zipWithNext { element1, element2 ->
                    Pair(hashStringToBigInt(element1), hashStringToBigInt(element2)) }
                .fold(accumulator, ::addAndDelete)
    }

    @Test fun createAndVerifyProof() {
        val accumulator = RSAAccumulator.newInstance()

        listOf("a","b","c","d","e","f","g","h")
                .map { hashStringToBigInt(it) }
                .fold(accumulator, ::addCreateProofAndVerify)
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