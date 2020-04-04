package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.interactors.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test

internal class FlowTestCollectorImplTest {

    // region getters

    @Test
    fun `test isCompleted on finite flow`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertTrue(isCompleted())
        }
    }

    @Test
    fun `test isCompleted on infinite flow`() = runBlockingTest {
        val sut = InfiniteFlowUseCase()

        sut().test(this) {
            assertFalse(isCompleted())
        }
    }

    @Test
    fun `test valueAt`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertEquals(1, valueAt(0))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test valueAt with wrong index, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            valueAt(3)
        }
    }

    @Test
    fun `test values`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertEquals(listOf(1, 2, 3), values())
        }
    }

    @Test
    fun `test valuesCount`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertEquals(3, valuesCount())
        }
    }

    @Test
    fun `test null error`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNull(error())
        }
    }

    @Test
    fun `test error`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertTrue(
                "Requested '${IllegalStateException::class.java.name}' but was '${error()}'",
                error() is IllegalStateException
            )
        }
    }

    // endregion

    // region assertions

    @Test
    fun `test assertComplete on finiteFlow`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertComplete()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertNotComplete on finiteFlow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNotComplete()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertComplete on infiniteFlow, should throw`() = runBlockingTest {
        val sut = InfiniteFlowUseCase()

        sut().test(this) {
            assertComplete()
        }
    }

    @Test
    fun `test assertNotComplete on infiniteFlow`() = runBlockingTest {
        val sut = InfiniteFlowUseCase()

        sut().test(this) {
            assertNotComplete()
        }
    }

    @Test
    fun `test assertNoErrors on error free flow`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNoErrors()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertNoErrors on flow with errors, should throw`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertNoErrors()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertError throwable on error free flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertError(Throwable::class.java)
        }
    }

    @Test
    fun `test assertError throwable on flow with errors`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertError(IllegalStateException::class.java)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertError predicate on error free flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertError { it is IllegalStateException }
        }
    }

    @Test
    fun `test assertError predicate on flow with errors`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertError { it is IllegalStateException }
        }
    }

    @Test
    fun `test assertErrorMessage`() = runBlockingTest {
        val errorMessage = "error"
        val sut = ErrorFlowUseCase()

        sut(errorMessage).test(this) {
            assertErrorMessage(errorMessage)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertErrorMessage, fail`() = runBlockingTest {
        val errorMessage = "error"
        val sut = ErrorFlowUseCase()

        sut("any").test(this) {
            assertErrorMessage(errorMessage)
        }
    }

    @Test
    fun `test assertValue on single value flow`() = runBlockingTest {
        val sut = SingleValueFlowUseCase()

        sut().test(this) {
            assertValue(1)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue on empty flow, should throw`() = runBlockingTest {
        val sut = EmptyFlowUseCase()

        sut().test(this) {
            assertValue(1)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue on multiple values flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValue(1)
        }
    }

    @Test
    fun `test assertValue predicate on single value flow`() = runBlockingTest {
        val sut = SingleValueFlowUseCase()

        sut().test(this) {
            assertValue { it == 1 }
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue predicate on empty flow, should throw`() = runBlockingTest {
        val sut = EmptyFlowUseCase()

        sut().test(this) {
            assertValue { it == 1 }
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue predicate on multiple values flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValue { it == 1 }
        }
    }


    @Test
    fun `test assertValueAt`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(1, 2)
        }
    }

    @Test(expected = java.lang.AssertionError::class)
    fun `test assertValueAt with wrong index, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(5, 6)
        }
    }

    @Test
    fun `test assertValueAt predicate`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(1) { it == 2 }
        }
    }

    @Test(expected = java.lang.AssertionError::class)
    fun `test assertValueAt predicate with wrong index, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(5) { it == 6 }
        }
    }

    @Test
    fun `test assertValues`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValues(1, 2, 3)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with less values, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValues(1, 2)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with more values, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValues(1, 2, 3, 4)
        }
    }

    @Test
    fun `test assertNoValues on emptyFlow`() = runBlockingTest {
        val sut = EmptyFlowUseCase()

        sut().test(this) {
            assertNoValues()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertNoValues on flow with values, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNoValues()
        }
    }

    @Test
    fun `test assertValueCount success`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueCount(3)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueCount fail, throws error`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueCount(2)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueIsNull, should fail for empty flow`() = runBlockingTest {
        val flow = EmptyFlowUseCase()

        flow().test(this) {
            assertValueIsNull()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueIsNull, should fail for multiple value flow`() = runBlockingTest {
        val flow = FlowUseCase()

        flow().test(this) {
            assertValueIsNull()
        }
    }

    @Test
    fun `test assertValueIsNull`() = runBlockingTest {
        val flow = flow<Int?> {
            emit(null)
        }

        flow.test(this) {
            assertValueIsNull()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueIsNull, but is not`() = runBlockingTest {
        val flow = flow<Int?> {
            emit(1)
        }

        flow.test(this) {
            assertValueIsNull()
        }
    }

    // endregion

} 