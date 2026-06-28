package com.cocobiz.app.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cocobiz.app.data.remote.dto.UserInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context, "cocobiz_auth",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(v) = prefs.edit().putString(KEY_TOKEN, v).apply()

    var userId: String
        get() = prefs.getString(KEY_USER_ID, "") ?: ""
        set(v) = prefs.edit().putString(KEY_USER_ID, v).apply()

    var username: String
        get() = prefs.getString(KEY_USERNAME, "") ?: ""
        set(v) = prefs.edit().putString(KEY_USERNAME, v).apply()

    var email: String
        get() = prefs.getString(KEY_EMAIL, "") ?: ""
        set(v) = prefs.edit().putString(KEY_EMAIL, v).apply()

    var phone: String
        get() = prefs.getString(KEY_PHONE, "") ?: ""
        set(v) = prefs.edit().putString(KEY_PHONE, v).apply()

    var businessName: String
        get() = prefs.getString(KEY_BIZ_NAME, "") ?: ""
        set(v) = prefs.edit().putString(KEY_BIZ_NAME, v).apply()

    var ownerName: String
        get() = prefs.getString(KEY_OWNER, "") ?: ""
        set(v) = prefs.edit().putString(KEY_OWNER, v).apply()

    var reminderChannel: String
        get() = prefs.getString(KEY_R_CHANNEL, "EMAIL") ?: "EMAIL"
        set(v) = prefs.edit().putString(KEY_R_CHANNEL, v).apply()

    var reminderFrequency: String
        get() = prefs.getString(KEY_R_FREQ, "DAILY") ?: "DAILY"
        set(v) = prefs.edit().putString(KEY_R_FREQ, v).apply()

    val isLoggedIn: Boolean get() = !token.isNullOrBlank()

    fun saveAuth(token: String, user: UserInfo) {
        this.token = token
        this.userId = user.id
        this.username = user.username
        this.email = user.email
        this.phone = user.phone
        this.businessName = user.businessName
        this.ownerName = user.ownerName
        this.reminderChannel = user.reminderChannel
        this.reminderFrequency = user.reminderFrequency
    }

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_BIZ_NAME = "biz_name"
        private const val KEY_OWNER = "owner"
        private const val KEY_R_CHANNEL = "r_channel"
        private const val KEY_R_FREQ = "r_freq"
    }
}
