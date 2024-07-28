package com.example.walletapp.DataBase.DAO

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

    @Query("UPDATE TX SET status = :status WHERE unid = :unid")
    suspend fun updateTransactionStatus(unid: String, status: Int)

    @Query("UPDATE TX SET deny = :reason WHERE unid = :unid")
    suspend fun updateTransactionRejectReason(unid: String, reason: String)

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

    @Query("SELECT status FROM TX WHERE unid = :unid")
    suspend fun getStatus(unid: String): Int?
}