package com.cocobiz.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cocobiz.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM local_user WHERE id = 1")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT * FROM local_user WHERE id = 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("DELETE FROM local_user")
    suspend fun deleteAll()
}
