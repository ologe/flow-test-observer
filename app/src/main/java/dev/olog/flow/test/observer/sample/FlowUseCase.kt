package dev.olog.flow.test.observer.sample

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class FlowUseCase {

    operator fun invoke(): Flow<Int> {
        return flow {
            emit(1)
            emit(2)
            emit(3)
        }
    }

}

class InfiniteFlowUseCase {

    operator fun invoke(): Flow<Int> {
        return callbackFlow {
            offer(1)
            offer(2)
            offer(3)
            awaitClose()
        }
    }

}