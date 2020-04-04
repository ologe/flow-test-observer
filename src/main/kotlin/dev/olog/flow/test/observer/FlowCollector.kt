package dev.olog.flow.test.observer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Allows hot and cold [Flow] streams testing.
 * [Hot vs Cold steams](https://medium.com/@luukgruijs/understanding-hot-vs-cold-observables-62d04cf92e03)
 *
 * Usage example (using a cold flow here for simplicity):
 * ```
 * val flow = flow {
 *      emit(1)
 *      emit(2)
 *  }
 *
 * flow.test()
 *      .assertValues(1, 2, 3)
 *      .assertValueCount(3)
 *
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
    message = """
        Please use the new version [test(CoroutineScope, FlowTestCollector.() -> Unit)]. This version has problems with 
        broadcast channels because it consumes all the items synchronously. The new version launches the [FlowTestCollector]
        inside a new coroutine to solve the problem.
        (Sorry but I cannot provide a replaceWith helper because of [this] keyword)
    """
)
fun <T> Flow<T>.test(): FlowTestCollector<T> {
    return FlowTestCollectorImpl(this)
}

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
): Job = coroutineScope {
    scope.launch {
        FlowTestCollectorImpl(this@test).block()
    }
}