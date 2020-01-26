package dev.olog.flow.test.observer.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ErrorFlowUseCase {

    operator fun invoke(): Flow<Int> {
        return flow {
            throw IllegalStateException()
        }
    }

}