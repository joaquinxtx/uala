package com.joaquin.uala.utils

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T): Resource<T>()
    data class Error(val message: String?, val cause: Throwable? = null): Resource<Nothing>()
}
