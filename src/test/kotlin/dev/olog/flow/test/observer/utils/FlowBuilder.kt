package dev.olog.flow.test.observer.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal fun <T> CoroutineScope.stateFlowBuilder(
    initialValue: T,
    block: suspend MutableStateFlow<T>.() -> Unit
): MutableStateFlow<T> {
    val flow = MutableStateFlow(initialValue)

    launch {
        flow.block()
    }

    return flow
}