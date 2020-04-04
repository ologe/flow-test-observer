package dev.olog.flow.test.observer.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

internal fun <T> CoroutineScope.channelBuilder(
    capacity: Int,
    block: suspend Channel<T>.() -> Any
): Channel<T> {
    val channel = Channel<T>(capacity)

    launch {
        channel.block()
    }

    return channel
}

internal fun <T> CoroutineScope.broadcastChannelBuilder(
    capacity: Int,
    block: suspend BroadcastChannel<T>.() -> Any
): BroadcastChannel<T> {
    val channel = BroadcastChannel<T>(capacity)

    launch {
        channel.block()
    }

    return channel
}