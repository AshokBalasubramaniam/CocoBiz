package com.cocobiz.app.data.repository

import com.cocobiz.app.data.local.dao.UserDao
import com.cocobiz.app.data.local.entity.UserEntity
import com.cocobiz.app.domain.repository.AuthRepository
import com.cocobiz.app.domain.repository.LocalUser
import com.cocobiz.app.util.PasswordHasher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun register(
        username: String,
        password: String,
        email: String,
        phone: String,
        businessName: String,
        ownerName: String
    ): LocalUser {
        if (userDao.getUserSync() != null) {
            throw IllegalStateException("An account already exists on this device. Please sign in instead.")
        }
        val salt = PasswordHasher.generateSalt()
        val entity = UserEntity(
            username = username,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
            email = email,
            phone = phone,
            businessName = businessName,
            ownerName = ownerName
        )
        userDao.insertOrUpdate(entity)
        return entity.toLocalUser()
    }

    override suspend fun login(username: String, password: String): LocalUser {
        val user = userDao.getUserSync()
            ?: throw IllegalStateException("No account found. Please create an account first.")
        if (!user.username.equals(username, ignoreCase = true) ||
            !PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)
        ) {
            throw IllegalStateException("Invalid username or password")
        }
        return user.toLocalUser()
    }

    override suspend fun resetPassword(username: String, newPassword: String) {
        val user = userDao.getUserSync()
            ?: throw IllegalStateException("No account found on this device")
        if (!user.username.equals(username, ignoreCase = true)) {
            throw IllegalStateException("No account found for this username")
        }
        val salt = PasswordHasher.generateSalt()
        userDao.insertOrUpdate(
            user.copy(passwordHash = PasswordHasher.hash(newPassword, salt), passwordSalt = salt)
        )
    }

    override suspend fun updateReminderPrefs(channel: String, frequency: String) {
        val user = userDao.getUserSync() ?: return
        userDao.insertOrUpdate(user.copy(reminderChannel = channel, reminderFrequency = frequency))
    }

    private fun UserEntity.toLocalUser() = LocalUser(
        username = username, email = email, phone = phone,
        businessName = businessName, ownerName = ownerName,
        reminderChannel = reminderChannel, reminderFrequency = reminderFrequency
    )
}
