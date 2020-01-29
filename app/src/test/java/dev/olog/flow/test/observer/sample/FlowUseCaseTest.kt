package dev.olog.flow.test.observer.sample

import dev.olog.flow.test.observer.test
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

internal class FlowUseCaseTest {

    @Test
    fun testFinite() = runBlockingTest {
        val flow = channelFlow {
            offer(1)
            offer(2)
            offer(3)
        }

        flow.test()
            .assertValues(1, 2, 3)
            .assertValueCount(3)
            .assertComplete()
    }

    @Test
    fun testInfinite() = runBlockingTest {
        val flow = channelFlow {
            offer(1)
            offer(2)
            offer(3)

            awaitClose()
        }

        flow.test()
            .assertValues(1, 2, 3)
            .assertValueCount(3)
    }

}