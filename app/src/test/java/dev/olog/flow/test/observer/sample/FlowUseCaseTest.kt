package dev.olog.flow.test.observer.sample

import dev.olog.flow.test.observer.test
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

internal class FlowUseCaseTest {

    @Test
    fun testFinite() = runBlockingTest {
        FlowUseCase().invoke().test()
            .assertValues(1, 2, 3)
            .assertValueCount(3)
            .assertTerminated()
    }

    @Test
    fun testInfinite() = runBlockingTest {
        InfiniteFlowUseCase().invoke().test()
            .assertValues(1, 2, 3)
            .assertValueCount(3)
    }

}