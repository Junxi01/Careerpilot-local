package com.careerpilot.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String,
    val message: String,
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
) {
    companion object {
        fun <T> ok(data: T): ApiResponse<T> = ApiResponse(success = true, data = data)
        fun fail(code: String, message: String): ApiResponse<Unit> =
            ApiResponse(success = false, error = ApiError(code = code, message = message))
    }
}

