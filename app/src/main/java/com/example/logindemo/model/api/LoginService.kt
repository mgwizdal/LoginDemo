package com.example.logindemo.model.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface LoginService {

    @POST("login")
    fun login(@Body username: AuthorizeDto): Single<Response<AuthorizeResponseDto>>
}