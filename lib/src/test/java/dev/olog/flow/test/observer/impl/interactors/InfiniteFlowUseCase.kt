package dev.olog.flow.test.observer.impl.interactors

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.yield

/**
 * Hot stream test use case
 */
internal class InfiniteFlowUseCase {

    suspend operator fun invoke(): Flow<Int> {
        return callbackFlow {
            offer(1)
            delay(10_0000)
            offer(2)
            yield()
            offer(3)

            // can potentially emit other values

            awaitClose()
        }
    }
}