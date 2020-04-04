package dev.olog.flow.test.observer

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.yield
import org.junit.Test

/**
 * Tests that checks completition of different flow builders
 */
internal class FlowBuildersTest {

    @Test
    fun testEmptyFlow() = runBlockingTest {
        val flow: Flow<Int> = emptyFlow()

        flow.test(this) {
            assertNoValues()
            assertComplete()
        }
    }

    @Test
    fun testFlowOf() = runBlockingTest {
        val flow: Flow<Int> = flowOf(1, 2)

        flow.test(this) {
            assertValues(1, 2)
            assertComplete()
        }
    }

    @Test
    fun testFlow() = runBlockingTest {
        val flow: Flow<Int> = flow {
            delay(10_000)
            yield()

            emit(1)
            emit(2)
        }

        flow.test(this) {
            assertValues(1, 2)
            assertComplete()
        }
    }

    @Test
    fun testNestedFlow() = runBlockingTest {
        val nestedFlow: Flow<Int> = flow {
            emit(2)
            emit(3)
        }

        val flow: Flow<Int> = flow {
            delay(10_000)
            yield()

            emit(1)
            emitAll(nestedFlow)
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertComplete()
        }
    }

    @Test
    fun testChannelFlow() = runBlockingTest {
        val flow: Flow<Int> = channelFlow {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertComplete()
        }
    }

    @Test
    fun testInfiniteChannelFlow() = runBlockingTest {
        val flow: Flow<Int> = channelFlow {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)

            awaitClose()
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertNotComplete()
        }
    }

    @Test
    fun testCallbackFlow() = runBlockingTest {
        val flow: Flow<Int> = callbackFlow {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertComplete()
        }
    }

    @Test
    fun testInfiniteCallbackFlow() = runBlockingTest {
        val flow: Flow<Int> = callbackFlow {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)

            awaitClose()
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertNotComplete()
        }
    }


}