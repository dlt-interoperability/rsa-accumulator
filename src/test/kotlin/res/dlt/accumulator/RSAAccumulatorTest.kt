package res.dlt.accumulator

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
        val (newAccumulator, addedElement) = add(elementToAdd).run(accumulator)
        assertEquals(elementToAdd, addedElement, "Function should return added element")
        assertTrue(isMember(elementToAdd).run(newAccumulator).b, "New accumulator should contain added element")
    }
}
