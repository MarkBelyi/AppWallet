package com.cri.wallet.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.TX


@Dao
interface TxDAO {
    @Query("SELECT * FROM TX")
    suspend fun getAll(): List<TX>
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

    @Upsert(entity = TX::class)
    suspend fun add(items: List<TX>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TX)
}