package com.example.logindemo.model.api

data class AuthorizeResponseDto(
    val status: String
) {
    companion object {
        const val SUCCESS_RESPONSE = "success"
    }
}
