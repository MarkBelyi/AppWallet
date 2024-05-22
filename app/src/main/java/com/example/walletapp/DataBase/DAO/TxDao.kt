package com.cri.wallet.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.walletapp.DataBase.Entities.TX
import kotlinx.coroutines.flow.Flow


@Dao
interface TxDAO {
    @Query("SELECT * FROM TX")
    fun getAll(): Flow<List<TX>>
    @Query("SELECT COUNT(*) FROM TX")
    suspend fun getCount(): Int

    @Query("SELECT * FROM TX WHERE tx != '' ")
    suspend fun getWhoHasTX(): List<TX>

    @Query("SELECT * FROM TX WHERE tx = '' ")
    suspend fun getWhoHasNotTX(): List<TX>

    @Delete
    suspend fun deleteItem(item: TX)

    @Query("DELETE FROM TX")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(items: List<TX>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TX)
}