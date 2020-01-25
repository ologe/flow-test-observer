package dev.olog.flow.test.observer.impl

import dev.olog.flow.test.observer.interactors.InfiniteFlowUseCase
import dev.olog.flow.test.observer.test
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class InfiniteFlowTestObserverTest {

    private val sut = InfiniteFlowUseCase()

    // region getters

    @Test
    fun `test isFinite`() = runBlockingTest {
        assertFalse("should be infinite", sut().test().isFinite())
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
        val emptyFlow = callbackFlow<Int> { awaitClose() }
        emptyFlow.test()
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

    @Test(expected = AssertionError::class)
    fun `test assertTerminated`() = runBlockingTest {
        sut().test()
            .assertTerminated()
    }

    @Test
    fun `test assertNotTerminated`() = runBlockingTest {
        sut().test()
            .assertNotTerminated()
    }

    @Test(expected = AssertionError::class)
    fun `test assertIsFinite, success`() = runBlockingTest {
        sut().test().assertIsFinite()
    }

    @Test
    fun `test assertIsNotFinite, fail`() = runBlockingTest {
        sut().test().assertIsNotFinite()
    }

    // endregion

}