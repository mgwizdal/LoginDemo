package com.example.logindemo.model.api

import io.reactivex.Single
import retrofit2.Response
import java.util.concurrent.TimeUnit

class FakeLoginService : LoginService {
    override fun login(username: AuthorizeDto): Single<Response<AuthorizeResponseDto>> {
        return Single.just(
            Response.success(AuthorizeResponseDto("success")))
            .delay(2, TimeUnit.SECONDS)
    }
}