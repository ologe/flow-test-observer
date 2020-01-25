package dev.olog.flow.test.observer.impl

import dev.olog.flow.test.observer.impl.interactors.FiniteFlowUseCase
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test

internal class FiniteFlowTestObserverTest {

    private val sut =
        FiniteFlowUseCase()

    // region getters

    @Test
    fun `test isFinite`() = runBlockingTest {
        assertTrue("should be finite", sut().test().isFinite())
    }

    @Test
    fun `test values`() = runBlockingTest {
        assertEquals(listOf(1, 2, 3), sut().test().values())
    }

    @Test
    fun `test valuesCount`() = runBlockingTest {
        assertEquals(3, sut().test().valuesCount())
    }

    // endregion

    // region assertions

    @Test
    fun `test assertValues success`() = runBlockingTest {
        sut().test()
            .assertValues(1, 2, 3)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with less values, should fail`() = runBlockingTest {
        sut().test()
            .assertValues(1, 2)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValues with more values, should fail`() = runBlockingTest {
        sut().test()
            .assertValues(1, 2, 3, 4)
    }

    @Test
    fun `test assertNoValues success`() = runBlockingTest {
        emptyFlow<Int>().test()
            .assertNoValues()
    }

    @Test(expected = AssertionError::class)
    fun `test assertNoValues fail`() = runBlockingTest {
        sut().test()
            .assertNoValues()
    }

    @Test
    fun `test assertValueCount success`() = runBlockingTest {
        sut().test()
            .assertValueCount(3)
    }

    @Test(expected = AssertionError::class)
    fun `test assertValueCount fail, throws error`() = runBlockingTest {
        sut().test()
            .assertValueCount(2)
    }

    @Test
    fun `test assertTerminated`() = runBlockingTest {
        sut().test()
            .assertComplete()
    }

    @Test(expected = AssertionError::class)
    fun `test assertNotTerminated`() = runBlockingTest {
        sut().test()
            .assertNotComplete()
    }

    @Test
    fun `test assertIsFinite, success`() = runBlockingTest {
        sut().test().assertIsFinite()
    }

    @Test(expected = AssertionError::class)
    fun `test assertIsNotFinite, fail`() = runBlockingTest {
        sut().test().assertIsNotFinite()
    }

    // endregion

} 