package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.interactors.EmptyFlowUseCase
import dev.olog.flow.test.observer.interactors.ErrorFlowUseCase
import dev.olog.flow.test.observer.interactors.FlowUseCase
import dev.olog.flow.test.observer.interactors.InfiniteFlowUseCase
import dev.olog.flow.test.observer.interactors.SingleValueFlowUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FlowTestCollectorImplTest {

    // region getters

    @Test
    fun `test isCompleted on finite flow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertTrue(isCompleted())
        }
    }

    @Test
    fun `test isCompleted on infinite flow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = InfiniteFlowUseCase()

        sut().test(this) {
            assertFalse(isCompleted())
        }
    }

    @Test
    fun `test valueAt`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertEquals(1, valueAt(0))
        }
    }

    @Test(expected = AssertionError::class)
    fun `test valueAt with lower out of bound index, should fail`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            valueAt(-1)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test valueAt with upper out of bound index, should fail`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            valueAt(3)
        }
    }

    @Test
    fun `test values`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertEquals(listOf(1, 2, 3), values())
        }
    }

    @Test
    fun `test valuesCount`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertEquals(3, valuesCount())
        }
    }

    @Test
    fun `test null error`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNull(error())
        }
    }

    @Test
    fun `test error`() = runTest(UnconfinedTestDispatcher()) {
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
    fun `test assertComplete on finiteFlow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertComplete()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertNotComplete on finiteFlow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNotComplete()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertComplete on infiniteFlow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = InfiniteFlowUseCase()

        sut().test(this) {
            assertComplete()
        }
    }

    @Test
    fun `test assertNotComplete on infiniteFlow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = InfiniteFlowUseCase()

        sut().test(this) {
            assertNotComplete()
        }
    }

    @Test
    fun `test assertNoErrors on error free flow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNoErrors()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertNoErrors on flow with errors, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertNoErrors()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertError (java) throwable on error free flow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertError(Throwable::class.java)
        }
    }

    @Test
    fun `test assertError (java) throwable on flow with errors`() = runTest(UnconfinedTestDispatcher()) {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertError(IllegalStateException::class.java)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertError (kotlin) throwable on error free flow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertError(Throwable::class)
        }
    }

    @Test
    fun `test assertError (kotlin) throwable on flow with errors`() = runTest(UnconfinedTestDispatcher()) {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertError(IllegalStateException::class)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertError predicate on error free flow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertError { it is IllegalStateException }
        }
    }

    @Test
    fun `test assertError predicate on flow with errors`() = runTest(UnconfinedTestDispatcher()) {
        val sut = ErrorFlowUseCase()

        sut().test(this) {
            assertError { it is IllegalStateException }
        }
    }

    @Test
    fun `test assertErrorMessage`() = runTest(UnconfinedTestDispatcher()) {
        val errorMessage = "error"
        val sut = ErrorFlowUseCase()

        sut(errorMessage).test(this) {
            assertErrorMessage(errorMessage)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertErrorMessage, fail`() = runTest(UnconfinedTestDispatcher()) {
        val errorMessage = "error"
        val sut = ErrorFlowUseCase()

        sut("any").test(this) {
            assertErrorMessage(errorMessage)
        }
    }

    @Test
    fun `test assertValue on single value flow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = SingleValueFlowUseCase()

        sut().test(this) {
            assertValue(1)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue on empty flow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = EmptyFlowUseCase()

        sut().test(this) {
            assertValue(1)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue on multiple values flow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValue(1)
        }
    }

    @Test
    fun `test assertValue predicate on single value flow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = SingleValueFlowUseCase()

        sut().test(this) {
            assertValue { it == 1 }
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue predicate on empty flow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = EmptyFlowUseCase()

        sut().test(this) {
            assertValue { it == 1 }
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue predicate on multiple values flow, should throw`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValue { it == 1 }
        }
    }


    @Test
    fun `test assertValueAt`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(1, 2)
        }
    }

    @Test(expected = java.lang.AssertionError::class)
    fun `test assertValueAt with wrong index, should fail`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(5, 6)
        }
    }

    @Test
    fun `test assertValueAt predicate`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(1) { it == 2 }
        }
    }

    @Test(expected = java.lang.AssertionError::class)
    fun `test assertValueAt predicate with wrong index, should fail`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueAt(5) { it == 6 }
        }
    }

    @Test
    fun `test assertValues`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValues(1, 2, 3)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with less values, should fail`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValues(1, 2)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with more values, should fail`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValues(1, 2, 3, 4)
        }
    }

    @Test
    fun `test assertNoValues on emptyFlow`() = runTest(UnconfinedTestDispatcher()) {
        val sut = EmptyFlowUseCase()

        sut().test(this) {
            assertNoValues()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertNoValues on flow with values, should fail`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertNoValues()
        }
    }

    @Test
    fun `test assertValueCount success`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueCount(3)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueCount fail, throws error`() = runTest(UnconfinedTestDispatcher()) {
        val sut = FlowUseCase()

        sut().test(this) {
            assertValueCount(2)
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueIsNull, should fail for empty flow`() = runTest(UnconfinedTestDispatcher()) {
        val flow = EmptyFlowUseCase()

        flow().test(this) {
            assertValueIsNull()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueIsNull, should fail for multiple value flow`() = runTest(UnconfinedTestDispatcher()) {
        val flow = FlowUseCase()

        flow().test(this) {
            assertValueIsNull()
        }
    }

    @Test
    fun `test assertValueIsNull`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow<Int?> {
            emit(null)
        }

        flow.test(this) {
            assertValueIsNull()
        }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueIsNull, but is not`() = runTest(UnconfinedTestDispatcher()) {
        val flow = flow<Int?> {
            emit(1)
        }

        flow.test(this) {
            assertValueIsNull()
        }
    }

    // endregion

} 