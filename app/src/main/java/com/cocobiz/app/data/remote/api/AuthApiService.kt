package com.cocobiz.app.data.remote.api

import com.cocobiz.app.data.remote.dto.AuthResponse
import com.cocobiz.app.data.remote.dto.ChangeUsernameRequest
import com.cocobiz.app.data.remote.dto.LoginRequest
import com.cocobiz.app.data.remote.dto.OtpResponse
import com.cocobiz.app.data.remote.dto.RegisterRequest
import com.cocobiz.app.data.remote.dto.SendOtpRequest
import com.cocobiz.app.data.remote.dto.UpdateReminderRequest
import com.cocobiz.app.data.remote.dto.VerifyOtpRequest
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): OtpResponse

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): AuthResponse

    @PATCH("api/auth/update-reminder")
    suspend fun updateReminder(@Body request: UpdateReminderRequest): Map<String, String>

    @PATCH("api/auth/change-username")
    suspend fun changeUsername(@Body request: ChangeUsernameRequest): Map<String, String>
}
