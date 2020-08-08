package com.example.logindemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.logindemo.model.LoginAction
import com.example.logindemo.model.LoginResult
import com.example.logindemo.model.LoginUseCase
import com.example.logindemo.utils.RxSchedulers
import com.example.logindemo.utils.include
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class LoginActivityViewModel(
    private val useCase: LoginUseCase,
    private val rxSchedulers: RxSchedulers
) : ViewModel() {
    private val clearedDisposables = CompositeDisposable()
    var successScreenState = false

    private val loginAction = BehaviorSubject.create<LoginUiState>()
    val loginUiState: Observable<LoginUiState> = loginAction
        .observeOn(rxSchedulers.ui)

    fun startLogin(username: String, password: String) {
        clearedDisposables include Observable.just(LoginAction(username, password))
            .observeOn(rxSchedulers.io)
            .compose(useCase)
            .startWith(LoginResult.Pending)
            .map {
                successScreenState = false
                when (it) {
                    LoginResult.Pending -> LoginUiState.Pending
                    is LoginResult.Success -> handleSuccess(it)
                    is LoginResult.Error -> LoginUiState.Error(it.errorType)
                }
            }
            .subscribe({ loginAction.onNext(it) },
                {
                    Log.e(TAG, it.message ?: "Unknown error!")
                    loginAction.onNext(LoginUiState.Error(ErrorType.UNKNOWN))
                }
            )
    }

    private fun handleSuccess(it: LoginResult.Success): LoginUiState {
        return if (it.isAuthorized) {
            successScreenState = true
            LoginUiState.Success
        } else LoginUiState.Error(
            ErrorType.WRONG_CREDENTIALS
        )
    }

    override fun onCleared() {
        clearedDisposables.dispose()
        super.onCleared()
    }

    companion object {
        private const val TAG = "LoginActivityViewModel"
    }
}

class EmptyCredentialsException() : Throwable()

sealed class LoginUiState {
    object Pending : LoginUiState()
    object Success : LoginUiState()
    data class Error(val errorType: ErrorType) : LoginUiState()
}

enum class ErrorType {
    EMPTY_CREDENTIALS, WRONG_CREDENTIALS, NETWORK_EXCEPTION, UNKNOWN
}