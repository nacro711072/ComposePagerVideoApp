package com.nacro.video.ext

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun timer(delayTime: Long, periodTime: Long): Flow<Any> {
    return flow<Unit> {
        delay(delayTime)
        emit(Unit)

        while (true) {
            delay(periodTime)
            emit(Unit)
        }
    }
}