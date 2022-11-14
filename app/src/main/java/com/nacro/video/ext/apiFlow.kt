package com.nacro.video.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import retrofit2.Response

fun <T> apiFlow(block: suspend FlowCollector<Response<T>>.() -> Unit): Flow<T> {
    return flow(block).map { response ->
        if (response.isSuccessful) {
            response.body() ?: throw NullPointerException("response.body is null")
        } else {
            throw HttpException(response)
        }
    }
}