package dev.olog.flow.test.observer.interactors

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Hot stream test use case
 */
internal class InfiniteFlowUseCase {

    suspend operator fun invoke(): Flow<Int> {
        return callbackFlow {
            offer(1)
            offer(2)
            offer(3)

            // can potentially emit other values

            awaitClose()
        }
    }
}