package dev.olog.flow.test.observer.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Cold stream test use case
 */
internal class FiniteFlowUseCase {

    operator fun invoke(): Flow<Int> {
        return flowOf(1, 2, 3)
    }
}