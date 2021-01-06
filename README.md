# Kotlin Flow test observer
[![Build Status](https://travis-ci.org/ologe/flow-test-observer.svg?branch=master)](https://travis-ci.org/ologe/flow-test-observer)
[![](https://jitpack.io/v/ologe/flow-test-observer.svg)](https://jitpack.io/#flow-test-observer)

Library inspired by `TestSubscriber` from [RxJava](https://github.com/ReactiveX/RxJava). 
Works with both *cold*/*finite* and *hot*/*infinite* flow.
Find in this [Medium post](https://medium.com/@eugeniu.olog/unit-testing-asynchronous-kotlin-flow-code-acddb38bf7ec)
some more information about the motivation of the library.

## Getting started

### Setting up the dependency
Step 1. Add the JitPack repository to your build file
Add it in your root `build.gradle` at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency

```groovy
testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$latest"
testImplementation "com.github.ologe:flow-test-observer:1.x.y"
```
(Please replace `x` and `y` with the latest version numbers: [![](https://jitpack.io/v/ologe/flow-test-observer.svg)](https://jitpack.io/#flow-test-observer)

### Usage

```kotlin
@Test
fun `finite flow test`() = runBlockingTest {
    val flow = flowOf(1, 2, 3)   
      
    flow.test(this) {
        assertValues(1, 2, 3)
        assertValueCount(3)
        assertComplete()
    }   
}

// works as well with infinite flows 👍
@Test
fun `infinite flow test`() = runBlockingTest {
    val flow = channelFlow<Int> {
        offer(1)
        offer(2)
        
        awaitClose()
    }
    
    flow.test(this) {
        assertValues(1, 2)
        assertValueCount(2)
        assertNotComplete()
    }
}
```

All available assertions can be found [here](https://github.com/ologe/flow-test-observer/blob/master/src/main/kotlin/dev/olog/flow/test/observer/FlowTestCollector.kt)

Here are some simple examples on how to test different channel:
- [Channels](https://github.com/ologe/flow-test-observer/blob/master/src/test/kotlin/dev/olog/flow/test/observer/ChannelBuildersTest.kt) 
- [Broadcast channels](https://github.com/ologe/flow-test-observer/blob/master/src/test/kotlin/dev/olog/flow/test/observer/BroadcastChannelBuildersTest.kt)
- [Flow builders](https://github.com/ologe/flow-test-observer/blob/master/src/test/kotlin/dev/olog/flow/test/observer/FlowBuildersTest.kt) 

## Bugs and Feedback
For bugs, questions and discussions please use the [Github Issues](https://github.com/ologe/flow-test-observer/issues).