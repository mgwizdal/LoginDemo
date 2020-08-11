package com.example.logindemo.model

import com.example.logindemo.model.api.AuthorizeDto
import com.example.logindemo.model.api.AuthorizeResponseDto.Companion.SUCCESS_RESPONSE
import com.example.logindemo.model.api.LoginService
import com.example.logindemo.utils.toBodyOrError
import com.example.logindemo.viewmodel.EmptyCredentialsException
import com.example.logindemo.viewmodel.ErrorType
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import retrofit2.HttpException

class LoginUseCase(
    private val loginService: LoginService
) : ObservableTransformer<LoginAction, LoginResult> {
    override fun apply(upstream: Observable<LoginAction>): ObservableSource<LoginResult> {
        return upstream
            .map(::checkCredentials)
            .flatMap { action ->
                loginService.login(AuthorizeDto(action.username, action.password))
                    .map { it.toBodyOrError() }
                    .toObservable()
                    .map { LoginResult.Success(it.status == SUCCESS_RESPONSE) as LoginResult }
            }
            .onErrorReturn(::handleError)
    }

    private fun handleError(it: Throwable): LoginResult {
        return LoginResult.Error(
            when {
                it is EmptyCredentialsException -> ErrorType.EMPTY_CREDENTIALS
                it is HttpException && it.code() == WRONG_CREDENTIALS_CODE -> ErrorType.WRONG_CREDENTIALS
                it.message != null -> ErrorType.NETWORK_EXCEPTION
                else -> ErrorType.UNKNOWN
            }
        ) as LoginResult
    }

    private fun checkCredentials(it: LoginAction): LoginAction {
        if (it.username.isEmpty() || it.password.isEmpty()) throw EmptyCredentialsException()
        return it
    }
    companion object {
        private const val WRONG_CREDENTIALS_CODE = 422
    }
}

data class LoginAction(val username: String, val password: String)
sealed class LoginResult {
    object Pending : LoginResult()
    data class Success(val isAuthorized: Boolean) : LoginResult()
    data class Error(val errorType: ErrorType) : LoginResult()
}
