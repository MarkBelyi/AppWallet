package com.example.walletapp.DataBase.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.Tokens


@Dao
interface TokensDAO {

    @Query("SELECT * FROM Tokens")
    fun getLiveTokens(): LiveData<List<Tokens>>
    @Query("SELECT * FROM Tokens WHERE network_id IN (:network_id)")
    suspend fun getAllForNets(network_id:List<Int>): List<Tokens>

    @Query("SELECT * FROM Tokens WHERE network_id = :network_id")
    suspend fun getAllForNet(network_id:Int): List<Tokens>

    @Query("SELECT COUNT(*) FROM Tokens")
    suspend fun getCount(): Int

    @Query("SELECT * FROM Tokens")
    suspend fun getAll(): List<Tokens>

    @Delete
    suspend fun deleteItem(item: Tokens)

    @Query("DELETE FROM Tokens")
    suspend fun deleteAll()

    @Upsert(entity = Tokens::class)
    suspend fun add(tokens: List<Tokens>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Tokens)

    @Query("DELETE FROM Tokens")
    fun clearTokens()
}