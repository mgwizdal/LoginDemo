package com.example.logindemo.utils

import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.toBodyOrError(): T{
    return if (isSuccessful) {
        body()!!
    } else {
        throw HttpException(Response.error<T>(errorBody()!!, raw()))
    }
}