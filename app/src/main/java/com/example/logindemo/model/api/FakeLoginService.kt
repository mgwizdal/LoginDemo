package com.example.logindemo.model.api

import io.reactivex.Single
import retrofit2.Response
import java.util.concurrent.TimeUnit

class FakeLoginService : LoginService {
    override fun login(username: String, password: String): Single<Response<Boolean>> {
        return Single.just(
            Response.success(username == "login@applover.pl" && password == "password123"))
            .delay(2, TimeUnit.SECONDS)
    }
}