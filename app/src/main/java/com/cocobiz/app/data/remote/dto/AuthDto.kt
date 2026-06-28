package com.cocobiz.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("businessName") val businessName: String,
    @SerializedName("ownerName") val ownerName: String
)

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class SendOtpRequest(
    @SerializedName("identifier") val identifier: String
)

data class VerifyOtpRequest(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("newPassword") val newPassword: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserInfo
)

data class UserInfo(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("businessName") val businessName: String = "",
    @SerializedName("ownerName") val ownerName: String = "",
    @SerializedName("reminderChannel") val reminderChannel: String? = "EMAIL",
    @SerializedName("reminderFrequency") val reminderFrequency: String? = "DAILY"
)

data class OtpResponse(
    @SerializedName("message") val message: String,
    @SerializedName("masked") val masked: String = ""
)

data class UpdateReminderRequest(
    @SerializedName("reminderChannel") val reminderChannel: String,
    @SerializedName("reminderFrequency") val reminderFrequency: String
)

data class ChangeUsernameRequest(
    @SerializedName("newUsername") val newUsername: String,
    @SerializedName("password") val password: String
)
