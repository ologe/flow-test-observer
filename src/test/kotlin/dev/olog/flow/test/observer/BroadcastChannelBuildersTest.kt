package dev.olog.flow.test.observer

import dev.olog.flow.test.observer.utils.broadcastChannelBuilder
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.yield
import org.junit.Test
import java.util.concurrent.TimeUnit

internal class BroadcastChannelBuildersTest {

    private val delay = TimeUnit.SECONDS.toMillis(10)

    private suspend fun BroadcastChannel<Int>.emitTestData() {
        delay(delay)
        yield()

        offer(1)
        send(2)
        sendBlocking(3)
        send(4)
        offer(5)
    }

    // broadcast channel don't support unlimited buffers
    @Test(expected = IllegalArgumentException::class)
    fun `test unlimited buffer`() = runBlockingTest {
        val capacity = Channel.UNLIMITED

        val flow = broadcastChannelBuilder<Int>(capacity) {

            emitTestData()
            close()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    // broadcast channel don't support empty buffers
    @Test(expected = IllegalArgumentException::class)
    fun `test empty buffer`() = runBlockingTest {
        val capacity = 0
        val flow = broadcastChannelBuilder<Int>(capacity) {

            emitTestData()
            close()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    @Test
    fun `test conflated`() = runBlockingTest {
        val flow = broadcastChannelBuilder<Int>(Channel.CONFLATED) {

            emitTestData()
            close()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }

    }

    @Test
    fun `test conflated infinite`() = runBlockingTest {

        val flow = broadcastChannelBuilder<Int>(Channel.CONFLATED) {

            emitTestData()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }

        // subscribe very late. should contains only the last item
        flow.test(this) {
            delay(delay * 2)

            assertValue(5)
            assertNotComplete()
        }
    }

    // default buffer is 64 (kotlin 1.3.61)
    @Test
    fun `test buffered`() = runBlockingTest {
        val flow = broadcastChannelBuilder<Int>(Channel.BUFFERED) {

            emitTestData()
            close()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }
    }

    // default buffer is 64 (kotlin 1.3.61)
    @Test
    fun `test buffered infinite`() = runBlockingTest {
        val flow = broadcastChannelBuilder<Int>(Channel.BUFFERED) {

            emitTestData()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }
    }

    @Test
    fun `test small buffer`() = runBlockingTest {
        val buffer = 3

        val flow = broadcastChannelBuilder<Int>(buffer) {

            emitTestData()
            close()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertComplete()
        }

    }

    @Test
    fun `test small buffer infinite`() = runBlockingTest {
        val buffer = 3

        val flow = broadcastChannelBuilder<Int>(buffer) {

            emitTestData()

        }.asFlow()

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }

        flow.test(this) {
            assertValues(1, 2, 3, 4, 5)
            assertNotComplete()
        }

        // subscribe very late. no items since they was never consumed.
        // this is not a replay channel (doesn't even exist at the moment)
        flow.test(this) {
            delay(delay * 2)

            assertNoValues()
            assertNotComplete()
        }
    }

}