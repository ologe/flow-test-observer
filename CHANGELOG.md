## 1.5.1
- Kotlin update to `1.4.21`
- Coroutines update to `1.4.2`

## 1.5.0
- Kotlin update to `1.4.10`
- Coroutines update to `1.4.1`
- Bump gradle wrapper to `6.7`
- Deleted deprecated `Flow<T>.test(): FlowTestCollector<T>`
- Improved assertion messages

## 1.4.1
- Kotlin update to `1.3.72`
- Coroutine update to `1.3.7`
- Fixed https://github.com/ologe/flow-test-observer/issues/3
- Added tests for the new [StateFlow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) introduced in coroutines 1.3.7

## 1.4.0
- Deprecated blocking `Flow<T>.test(): FlowTestCollector<T>`
- Added `Flow<T>.test(CoroutineScope, FlowTestCollector<T>.() -> Unit)`
    - Creates the FlowTestCollector inside a new coroutine to avoid blocking current thread
    - Works correctly with broadcast channels and rendezvous channels

## 1.3.0
- Converted to JVM project

## 1.2.1
- Improved error messages description

## 1.2.0
- Breaking changes:
    - Renamed `TestFlowObserver` to `TestFlowCollector` to match coroutines naming convention 

## 1.1.0
- Added a new assertion
    - `assertValueIsNull`: useful for libraries like [Room](https://developer.android.com/topic/libraries/architecture/room),
    because when requesting a `Flow<List<T>`, returns `null` instead of `emptyList` when the table empty

## 1.0.1
- Added missing assertErrorMessage tests

## 1.0.0
- Initial release