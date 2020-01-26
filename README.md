# Kotlin Flow test observer

Adaptation of `TestSubscriber` interface from [RxJava 2](https://github.com/ReactiveX/RxJava) 
to work with Kotlin `Flow` API. The library works with both *cold*/*finite* and 
*hot*/*infinite* flow.

## Getting started

### Setting up the dependency
The first step is to include RxJava 3 into your project, for example, as a Gradle compile dependency:

```groovy
testImplementation "com.github.ologe:flow-test-observer:1.0.0"
```

### Usage

Given a flow:

```kotlin
class FlowUseCase {

    operator fun invoke(): Flow<Int> {
        return channelFlow<Int> {
            offer(1)
            offer(2)
            offer(3)
        }   
    }

}
```

Then somewhere in you test directory:

```kotlin
class FlowUseCaseTest {
    
    private val sut = FlowUseCase()
    
    @Test
    fun `use case test`() = runBlockingTest {
        sut().test()
            .assertValues(1, 2, 3)
            .assertValueCount(3)
            .assertComplete()
    }
    
    // works as well with infinite flows 👍
    @Test
    fun `test infinite flow`() = runBlockingTest {
        val flow = channelFlow<Int> {
            offer(1)
            offer(2)
            
            awaitClose()
        }
        
        flow.test()
            .assertValues(1, 2)
            .assertValueCount(2)
    }
    
}

```



You can see all available assertions [here](https://github.com/ologe/flow-test-observer/blob/master/lib/src/main/java/dev/olog/flow/test/observer/FlowObserver.kt)