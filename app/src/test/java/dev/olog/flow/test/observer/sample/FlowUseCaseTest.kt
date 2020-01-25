package dev.olog.flow.test.observer.sample

import dev.olog.flow.test.observer.test
import org.junit.Rule
import org.junit.Test

internal class FlowUseCaseTest {

    @get:Rule
    val coroutineRule =
        CoroutineRule()

    @Test
    fun testFinite() = coroutineRule.runBlocking {
        FlowUseCase().invoke().test()
            .assertValues(1, 2, 3)
            .assertValueCount(3)
            .assertTerminated()
    }

    @Test
    fun testInfinite() = coroutineRule.runBlocking {
        InfiniteFlowUseCase().invoke().test()
            .assertValues(1, 2, 3)
            .assertValueCount(3)
    }

}