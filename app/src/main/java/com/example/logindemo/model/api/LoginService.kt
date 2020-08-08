package com.example.logindemo.model.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.POST


interface LoginService {
    @POST("login")
    fun login(username: String, password: String): Single<Response<Boolean>>
}