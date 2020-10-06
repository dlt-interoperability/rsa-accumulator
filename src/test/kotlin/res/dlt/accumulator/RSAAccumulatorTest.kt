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

    @Test fun deleteElement() {
        val accumulator = RSAAccumulator.newInstance()
        val elementOne = BigInteger.ONE
        val (newAccumulator, _) = add(elementOne).run(accumulator)
        val elementToDelete = BigInteger.TEN
        val (newAccumulatorTwo, _) = add(elementToDelete).run(newAccumulator)
        val (newAccumulatorAfterDelete, deletedElement) =
                delete(elementToDelete).run(newAccumulatorTwo)
        val isDeletedElementMember = isMember(elementToDelete).run(newAccumulatorAfterDelete).b
        val isInitialElementMember = isMember(elementOne).run(newAccumulatorAfterDelete).b

        assertEquals(elementToDelete, deletedElement, "Function should return deleted element")
        assertFalse(isDeletedElementMember, "Accumulator should not contain deleted element")
        assertTrue(isInitialElementMember, "Accumulator should still contain initial element")
    }
}
