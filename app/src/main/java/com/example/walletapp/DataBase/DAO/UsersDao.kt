package com.example.walletapp.DataBase.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.walletapp.DataBase.Entities.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: Users)

    @Query("DELETE FROM Users where address = :address") //("DELETE FROM Users where address = :address")
    suspend fun deleteUsersByAddress(address: String)

    @Query("SELECT * FROM Users")//Лист с подписантами
     fun getAllUsers(): Flow<List<Users>>

    @Query("SELECT * FROM Users ORDER BY name ASC")//Лист с подписантами по имени
     fun getAllUsersByName(): Flow<List<Users>>
}