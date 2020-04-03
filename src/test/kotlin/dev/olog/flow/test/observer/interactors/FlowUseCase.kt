package dev.olog.flow.test.observer.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class FlowUseCase {

    operator fun invoke(): Flow<Int> {
        return flow {
            emit(1)
            emit(2)
            emit(3)
        }
    }

}