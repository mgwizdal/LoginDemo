package com.example.logindemo.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.logindemo.R
import com.example.logindemo.utils.*
import com.example.logindemo.viewmodel.ErrorType
import com.example.logindemo.viewmodel.LoginActivityViewModel
import com.example.logindemo.viewmodel.LoginUiState
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : AppCompatActivity() {

    private val destroyDisposable = CompositeDisposable()
    private val viewModel: LoginActivityViewModel by viewModel<LoginActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        destroyDisposable include viewModel.loginUiState
            .subscribe {
                when (it) {
                    LoginUiState.Pending -> handlePendingUi()
                    is LoginUiState.Success -> handleSuccessUi()
                    is LoginUiState.Error -> handleErrorUi(it)
                }
            }


        setupButton()
    }

    private fun handlePendingUi() {
        loginButton.hide()
        errorTextView.hide()
        progressBarLogin.show()
    }

    private fun handleSuccessUi() {
        progressBarLogin.makeInvisible()
        loginButton.show()
        errorTextView.hide()
    }

    private fun handleErrorUi(it: LoginUiState.Error) {
        progressBarLogin.makeInvisible()
        loginButton.show()
        errorTextView.show()
        errorTextView.text = getErrorText(it.errorType)
    }

    private fun getErrorText(errorType: ErrorType): CharSequence? {
        return when (errorType) {
            ErrorType.EMPTY_CREDENTIALS -> getString(R.string.empty_credentials)
            ErrorType.WRONG_CREDENTIALS -> getString(R.string.wrong_credentials)
            ErrorType.NETWORK_EXCEPTION -> getString(R.string.network_exception)
            ErrorType.UNKNOWN -> getString(R.string.unknown_error)
        }
    }

    private fun setupButton() {
        loginButton.setOnClickListener {
            viewModel.startLogin(etUsername.text.toString(), etPassword.text.toString())
        }
    }

    override fun onDestroy() {
        destroyDisposable.dispose()
        super.onDestroy()
    }
}