package com.example.logindemo.model

import com.example.logindemo.model.api.LoginService
import com.example.logindemo.utils.toBodyOrError
import com.example.logindemo.viewmodel.EmptyCredentialsException
import com.example.logindemo.viewmodel.ErrorType
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class LoginUseCase(
    private val loginService: LoginService
) : ObservableTransformer<LoginAction, LoginResult> {
    override fun apply(upstream: Observable<LoginAction>): ObservableSource<LoginResult> {
        return upstream
            .map(::checkCredentials)
            .flatMap { action ->
                loginService.login(action.username, action.password)
                    .map { it.toBodyOrError() }
                    .toObservable()
                    .map { LoginResult.Success(it) as LoginResult }
            }
            .onErrorReturn(::handleError)
    }

    private fun handleError(it: Throwable): LoginResult {
        return LoginResult.Error(
            when {
                it is EmptyCredentialsException -> ErrorType.EMPTY_CREDENTIALS
                it.message != null -> ErrorType.NETWORK_EXCEPTION
                else -> ErrorType.UNKNOWN
            }
        ) as LoginResult
    }

    private fun checkCredentials(it: LoginAction): LoginAction {
        if (it.username.isEmpty() || it.password.isEmpty()) throw EmptyCredentialsException()
        return it
    }
}

data class LoginAction(val username: String, val password: String)
sealed class LoginResult {
    object Pending : LoginResult()
    data class Success(val isAuthorized: Boolean) : LoginResult()
    data class Error(val errorType: ErrorType) : LoginResult()
}
