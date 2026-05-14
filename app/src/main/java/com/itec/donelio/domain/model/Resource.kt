package com.itec.donelio.domain.model

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

fun <T> Resource<T>.onError(action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) action(message)
    return this
}

fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success

fun <T> Resource<T>.isError(): Boolean = this is Resource.Error
