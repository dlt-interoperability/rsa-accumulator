package res.dlt.accumulator

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
        val (newAccumulator, addedElement) = add(accumulator, elementToAdd)
        assertEquals(elementToAdd, addedElement, "Function should return added element")
        assertTrue(isMember(accumulator, elementToAdd), "New accumulator should contain added element")
    }
}
