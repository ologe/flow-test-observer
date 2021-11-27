package dev.olog.flow.test.observer.interactors

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

internal class InfiniteFlowUseCase {

    operator fun invoke(): Flow<Int> {
        return channelFlow {
            trySend(1)
            trySend(2)
            trySend(3)

            awaitClose()
        }
    }

}