package dev.olog.flow.test.observer.impl.interactors

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.yield

/**
 * Cold stream test use case
 */
internal class FiniteFlowUseCase {

    suspend operator fun invoke(): Flow<Int> {
        return callbackFlow {
            offer(1)
            delay(10_000)
            offer(2)
            yield()
            offer(3)
        }
    }
}