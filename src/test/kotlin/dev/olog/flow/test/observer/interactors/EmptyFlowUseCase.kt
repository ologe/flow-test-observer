package dev.olog.flow.test.observer.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal class EmptyFlowUseCase {

    operator fun invoke(): Flow<Int> {
        return emptyFlow()
    }

}