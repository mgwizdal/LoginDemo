package com.example.logindemo.model

import com.example.logindemo.model.api.LoginService
import com.example.logindemo.viewmodel.ErrorType
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

class LoginUseCaseTest {

    @Mock
    lateinit var service: LoginService

    lateinit var useCase: LoginUseCase

    private val username = "app@lover.com"
    private val password = "password"
    private val response = true

    private val action = PublishSubject.create<LoginAction>()
    private lateinit var observer: TestObserver<LoginResult>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        useCase = LoginUseCase(service)
        observer = action.compose(useCase).test()
    }

    @Test
    fun `Test success - is authorized`() {
        //given
        `when`(
            service.login(
                username,
                password
            )
        ).thenReturn(Single.just(Response.success(response)))

        //then
        action.onNext(LoginAction(username, password))

        //then
        observer.assertValue(LoginResult.Success(response))
    }

    @Test
    fun `Test success - is not authorized`() {
        //given
        `when`(service.login(username, password)).thenReturn(Single.just(Response.success(false)))

        //then
        action.onNext(LoginAction(username, password))

        //then
        observer.assertValue(LoginResult.Success(false))
    }

    @Test
    fun `Test error - call failed`() {
        //given
        `when`(service.login(username, password)).thenReturn(
            Single.just(
                Response.error(
                    500, ResponseBody.create(
                        MediaType.parse("json"), "authorization failed"
                    )
                )
            )
        )

        //then
        action.onNext(LoginAction(username, password))

        //then
        observer.assertOf { LoginResult.Error(ErrorType.NETWORK_EXCEPTION) }
    }

    @Test
    fun `Test error - empty credentials`() {
        //given
        val username = ""

        //then
        action.onNext(LoginAction(username, password))

        //then
        observer.assertOf { LoginResult.Error(ErrorType.EMPTY_CREDENTIALS) }
    }
}