package dev.olog.flow.test.observer

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.yield
import org.junit.Ignore
import org.junit.Test

/**
 * Tests that checks completition of different flow builders
 */
@FlowPreview
internal class FlowBuildersTest {

    @Test
    fun testEmptyFlow() = runBlockingTest {
        val flow: Flow<Int> = emptyFlow()

        flow.test()
            .assertNoValues()
            .assertNotComplete()
    }

    @Test
    fun testFlowOf() = runBlockingTest {
        val flow: Flow<Int> = flowOf(1, 2)

        flow.test()
            .assertValues(1, 2)
            .assertComplete()
    }

    @Test
    fun testFlow() = runBlockingTest {
        val flow: Flow<Int> = flow {
            delay(10_000)
            yield()

            emit(1)
            emit(2)
        }

        flow.test()
            .assertValues(1, 2)
            .assertComplete()
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

        flow.test()
            .assertValues(1, 2, 3)
            .assertComplete()
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

        flow.test()
            .assertValues(1, 2, 3)
            .assertComplete()
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

        flow.test()
            .assertValues(1, 2, 3)
            .assertNotComplete()
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

        flow.test()
            .assertValues(1, 2, 3)
            .assertComplete()
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

        flow.test()
            .assertValues(1, 2, 3)
            .assertNotComplete()
    }

    @Test
    @Ignore(value = "throws java.lang.IllegalStateException: This job has not completed yet")
    fun testRendezvousChannel() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.RENDEZVOUS).apply {
            delay(10_000)
            yield()

            offer(1) // returns always false
            send(2)
            sendBlocking(3) // blocks thread
            send(4)
            offer(5)

            close()
        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertComplete()

    }

    @Test
    @Ignore(value = "throws java.lang.IllegalStateException: This job has not completed yet")
    fun testInfiniteRendezvousChannel() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.RENDEZVOUS).apply {
            delay(10_000)
            yield()

            offer(1) // returns always false
            send(2)
            sendBlocking(3) // blocks thread
            send(4)
            offer(5)

        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertNotComplete()

    }

    // default buffer is 64 (kotlin 1.3.61)
    @Test
    fun `test BufferedChannel with default buffer`() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.BUFFERED).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
            send(4)
            offer(5)

        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertNotComplete()
    }

    // default buffer is 64 (kotlin 1.3.61)
    @Test
    fun `test infinite BufferedChannel with default buffer`() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.BUFFERED).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
            send(4)
            offer(5)

            close()
        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertComplete()
    }

    @Test
    @Ignore(value = "Flaky")
    fun `test BufferedChannel with small buffer`() = runBlockingTest {
        val buffer = 1

        val flow: Flow<Int> = Channel<Int>(buffer).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
//            sendBlocking(3) TODO throws java.lang.IllegalStateException: This job has not completed yet
//            send(4) // TODO multiple send don't work
            offer(5)

            close()
        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertComplete()
    }

    @Test
    @Ignore(value = "Flaky")
    fun `test infinite BufferedChannel with small buffer`() = runBlockingTest {
        val buffer = 3

        val flow: Flow<Int> = Channel<Int>(buffer).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
//            sendBlocking(3) TODO throws java.lang.IllegalStateException: This job has not completed yet
//            send(4) // TODO multiple send don't work
            offer(5)

        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertNotComplete()
    }

    @Test
    fun `test ConflatedChannel`() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.CONFLATED).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
            send(4)
            offer(5)

            close()
        }.consumeAsFlow()

        flow.test()
            .assertValue(5)
            .assertComplete()
    }

    @Test
    fun `test infinite ConflatedChannel`() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.CONFLATED).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
            send(4)
            offer(5)

        }.consumeAsFlow()

        flow.test()
            .assertValue(5)
            .assertNotComplete()
    }

    @Test
    fun `test unlimited Channel`() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.UNLIMITED).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
            send(4)
            offer(5)

            close()
        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertComplete()
    }

    @Test
    fun `test infinite unlimited Channel`() = runBlockingTest {
        val flow: Flow<Int> = Channel<Int>(Channel.UNLIMITED).apply {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)
            send(4)
            offer(5)

        }.consumeAsFlow()

        flow.test()
            .assertValues(1, 2, 3, 4, 5)
            .assertNotComplete()
    }

}