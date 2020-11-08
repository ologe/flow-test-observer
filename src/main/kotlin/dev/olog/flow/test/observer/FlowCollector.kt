package dev.olog.flow.test.observer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

// since 1.4.0
/**
 * Allows hot and cold [Flow] streams testing.
 * [Hot vs Cold steams](https://medium.com/@luukgruijs/understanding-hot-vs-cold-observables-62d04cf92e03)
 *
 * Usage example (using a cold flow here for simplicity):
 * ```kotlin
 * val flow = flow {
 *      emit(1)
 *      emit(2)
 *  }
 *
 * flow.test(this) {
 *      assertValues(1, 2, 3)
 *      assertValueCount(3)
 * }
 */
suspend fun <T> Flow<T>.test(
    scope: CoroutineScope,
    block: suspend FlowTestCollector<T>.() -> Unit
): Job {
    return scope.launch(coroutineContext) {
        FlowTestCollectorImpl(this@test).block()
    }
}