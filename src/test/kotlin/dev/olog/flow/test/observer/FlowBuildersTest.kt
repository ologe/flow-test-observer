package dev.olog.flow.test.observer

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Test

/**
 * Tests that checks completition of different flow builders
 */
internal class FlowBuildersTest {

    @Test
    fun testEmptyFlow() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = emptyFlow()

        flow.test(this) {
            assertNoValues()
            assertComplete()
        }
    }

    @Test
    fun testFlowOf() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = flowOf(1, 2)

        flow.test(this) {
            assertValues(1, 2)
            assertComplete()
        }
    }

    @Test
    fun testFlow() = runTest(UnconfinedTestDispatcher()) {
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
    fun testNestedFlow() = runTest(UnconfinedTestDispatcher()) {
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
    fun testChannelFlow() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelFlow {
            delay(10_000)
            yield()

            trySend(1)
            send(2)
            trySendBlocking(3)
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertComplete()
        }
    }

    @Test
    fun testInfiniteChannelFlow() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelFlow {
            delay(10_000)
            yield()

            trySend(1)
            send(2)
            trySendBlocking(3)

            awaitClose()
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertNotComplete()
        }
    }

    @Test
    fun testCallbackFlow() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = callbackFlow {
            delay(10_000)
            yield()

            trySend(1)
            send(2)
            trySendBlocking(3)
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertComplete()
        }
    }

    @Test
    fun testInfiniteCallbackFlow() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = callbackFlow {
            delay(10_000)
            yield()

            trySend(1)
            send(2)
            trySendBlocking(3)

            awaitClose()
        }

        flow.test(this) {
            assertValues(1, 2, 3)
            assertNotComplete()
        }
    }


}