package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.interactors.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test

internal class FiniteFlowTestObserverTest {

    // region getters

    @Test
    fun `test isCompleted on finite flow`() = runBlockingTest {
        val sut = FlowUseCase()

        assertTrue(sut().test().isCompleted())
    }

    @Test
    fun `test isCompleted on infinite flow`() = runBlockingTest {
        val sut = InfiniteFlowUseCase()

        assertFalse(sut().test().isCompleted())
    }

    @Test
    fun `test valueAt`() = runBlockingTest {
        val sut = FlowUseCase()

        assertEquals(1, sut().test().valueAt(0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test valueAt with wrong index, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test().valueAt(3)
    }

    @Test
    fun `test values`() = runBlockingTest {
        val sut = FlowUseCase()

        assertEquals(listOf(1, 2, 3), sut().test().values())
    }

    @Test
    fun `test valuesCount`() = runBlockingTest {
        val sut = FlowUseCase()

        assertEquals(3, sut().test().valuesCount())
    }

    @Test
    fun `test null error`() = runBlockingTest {
        val sut = FlowUseCase()

        assertNull(sut().test().error())
    }

    @Test
    fun `test error`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        val error = sut().test().error()

        assertTrue(
            "Requested '${IllegalStateException::class.java.name}' but was '${error}'",
            error is IllegalStateException
        )
    }

    // endregion

    // region assertions

    @Test
    fun `test assertComplete on finiteFlow`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertComplete()
    }

    @Test(expected = AssertionError::class)
    fun `test assertNotComplete on finiteFlow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertNotComplete()
    }

    @Test(expected = AssertionError::class)
    fun `test assertComplete on infiniteFlow, should throw`() = runBlockingTest {
        val sut = InfiniteFlowUseCase()

        sut().test()
            .assertComplete()
    }

    @Test
    fun `test assertNotComplete on infiniteFlow`() = runBlockingTest {
        val sut = InfiniteFlowUseCase()

        sut().test()
            .assertNotComplete()
    }

    @Test
    fun `test assertNoErrors on error free flow`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test().assertNoErrors()
    }

    @Test(expected = AssertionError::class)
    fun `test assertNoErrors on flow with errors, should throw`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        sut().test().assertNoErrors()
    }

    @Test(expected = AssertionError::class)
    fun `test assertError throwable on error free flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertError(Throwable::class.java)
    }

    @Test
    fun `test assertError throwable on flow with errors`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        sut().test()
            .assertError(IllegalStateException::class.java)
    }

    @Test(expected = AssertionError::class)
    fun `test assertError predicate on error free flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertError { it is IllegalStateException }
    }

    @Test
    fun `test assertError predicate on flow with errors`() = runBlockingTest {
        val sut = ErrorFlowUseCase()

        sut().test()
            .assertError { it is IllegalStateException }
    }

    @Test
    fun `test assertValue on single value flow`() = runBlockingTest {
        val sut = SingleValueFlowUseCase()

        sut().test()
            .assertValue(1)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue on empty flow, should throw`() = runBlockingTest {
        val sut = EmptyFlowUseCase()

        sut().test()
            .assertValue(1)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue on multiple values flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertValue(1)
    }

    @Test
    fun `test assertValue predicate on single value flow`() = runBlockingTest {
        val sut = SingleValueFlowUseCase()

        sut().test()
            .assertValue { it == 1 }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue predicate on empty flow, should throw`() = runBlockingTest {
        val sut = EmptyFlowUseCase()

        sut().test()
            .assertValue { it == 1 }
    }

    @Test(expected = AssertionError::class)
    fun `test assertValue predicate on multiple values flow, should throw`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertValue { it == 1 }
    }

    @Test
    fun `test assertValueAt`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test().assertValueAt(1, 2)
    }

    @Test(expected = java.lang.AssertionError::class)
    fun `test assertValueAt with wrong index, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test().assertValueAt(5, 6)
    }

    @Test
    fun `test assertValueAt predicate`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test().assertValueAt(1) { it == 2 }
    }

    @Test(expected = java.lang.AssertionError::class)
    fun `test assertValueAt predicate with wrong index, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test().assertValueAt(5) { it == 6 }
    }

    @Test
    fun `test assertValues`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertValues(1, 2, 3)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with less values, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertValues(1, 2)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with more values, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertValues(1, 2, 3, 4)
    }

    @Test
    fun `test assertNoValues on emptyFlow`() = runBlockingTest {
        val sut = EmptyFlowUseCase()

        sut().test()
            .assertNoValues()
    }

    @Test(expected = AssertionError::class)
    fun `test assertNoValues on flow with values, should fail`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertNoValues()
    }

    @Test
    fun `test assertValueCount success`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertValueCount(3)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueCount fail, throws error`() = runBlockingTest {
        val sut = FlowUseCase()

        sut().test()
            .assertValueCount(2)
    }

    // endregion

} 