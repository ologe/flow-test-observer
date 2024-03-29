package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.utils.channelBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Test
import java.util.concurrent.TimeUnit

internal class ChannelBuildersTest {

    private val delay = TimeUnit.SECONDS.toMillis(10)

    private suspend fun Channel<Int>.emitTestData() {
        delay(delay)
        yield()

        trySend(1)
        send(2)
        trySendBlocking(3)
        send(4)
        trySend(5)
    }

    @Test
    fun testRendezvousChannel() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelBuilder<Int>(Channel.RENDEZVOUS) {

            emitTestData()
            close()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    @Test
    fun testInfiniteRendezvousChannel() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelBuilder<Int>(Channel.RENDEZVOUS) {

            emitTestData()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }
    }

    // default buffer is 64 (kotlin 1.3.61)
    @Test
    fun `test BufferedChannel with default buffer`() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelBuilder<Int>(Channel.BUFFERED) {

            emitTestData()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }
    }

    // default buffer is 64 (kotlin 1.3.61)
    @Test
    fun `test infinite BufferedChannel with default buffer`() = runTest(UnconfinedTestDispatcher()) {
        val flow = channelBuilder<Int>(Channel.BUFFERED) {

            emitTestData()
            close()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    @Test
    fun `test BufferedChannel with small buffer`() = runTest(UnconfinedTestDispatcher()) {
        val buffer = 1

        val flow: Flow<Int> = channelBuilder<Int>(buffer) {

            emitTestData()
            close()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    @Test
    fun `test infinite BufferedChannel with small buffer`() = runTest(UnconfinedTestDispatcher()) {
        val buffer = 3

        val flow: Flow<Int> = channelBuilder<Int>(buffer) {

            emitTestData()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }
    }

    @Test
    fun `test ConflatedChannel`() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelBuilder<Int>(Channel.CONFLATED) {

            emitTestData()

            close()
        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    @Test
    fun `test infinite ConflatedChannel`() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelBuilder<Int>(Channel.CONFLATED) {

            emitTestData()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }

        // subscribe very late. should contains only the last item
//        flow.test(this) {
//            delay(delay * 2)
//
//            assertValue(5) TODO understand why fails
//            assertNotComplete()
//        }
    }

    @Test
    fun `test unlimited Channel`() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelBuilder<Int>(Channel.UNLIMITED) {

            emitTestData()
            close()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    @Test
    fun `test infinite unlimited Channel`() = runTest(UnconfinedTestDispatcher()) {
        val flow: Flow<Int> = channelBuilder<Int>(Channel.UNLIMITED) {

            emitTestData()

        }.consumeAsFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }
    }

}