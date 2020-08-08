package com.example.logindemo.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.logindemo.R
import com.example.logindemo.utils.hide
import com.example.logindemo.utils.include
import com.example.logindemo.utils.makeInvisible
import com.example.logindemo.utils.show
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
        val fadeLoginContainer = loginContainer.createFade(Fade.OUT)
        val fadeLoginLabel = loginLabel.createFade(Fade.OUT)
        val fadeSuccess = successTextView.createFade(Fade.IN, FADE_DURATION + FADE_DELAY)
        val set = TransitionSet().apply {
            addTransition(fadeLoginContainer)
            addTransition(fadeLoginLabel)
            addTransition(fadeSuccess)
        }
        TransitionManager.beginDelayedTransition(mainContainer, set)
        loginContainer.hide()
        loginLabel.hide()
        successTextView.show()
        progressBarLogin.hide()
        loginButton.show()
        icon.animate().setStartDelay(FADE_DURATION).translationY(150f).setDuration(1500L)
            .scaleX(1.7f)
            .scaleY(1.7f)
    }

    private fun View.createFade(mode: Int, startDelay: Long? = null): Transition {
        val fade = Fade(mode)
        fade.duration = FADE_DURATION
        fade.startDelay = startDelay ?: FADE_DELAY
        fade.addTarget(this)
        return fade
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

    override fun onBackPressed() {
        if (viewModel.successScreenState) {
            icon.animate().setDuration(1000).scaleY(1f).scaleX(1f)
                .withEndAction {
                    successTextView.hide()
                    loginContainer.show()
                    loginLabel.show()
                    viewModel.successScreenState = false
                }
        } else super.onBackPressed()
    }

    override fun onDestroy() {
        destroyDisposable.dispose()
        super.onDestroy()
    }

    companion object {
        private const val FADE_DURATION = 1000L
        private const val FADE_DELAY = 500L
    }
}