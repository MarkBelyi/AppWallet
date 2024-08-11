package com.example.walletapp.DataBase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.walletapp.DataBase.Entities.AllTX
import kotlinx.coroutines.flow.Flow

@Dao
interface AllTxDAO {
    @Query("SELECT * FROM AllTX")
    fun getAll(): Flow<List<AllTX>>

    @Query("SELECT COUNT(*) FROM AllTX")
    suspend fun getCount(): Int

    @Query("SELECT * FROM AllTX WHERE tx != '' ")
    suspend fun getWhoHasTX(): List<AllTX>

    @Query("UPDATE AllTX SET status = :status WHERE unid = :unid")
    suspend fun updateTransactionStatus(unid: String, status: Int)

    @Query("UPDATE AllTX SET deny = :reason WHERE unid = :unid")
    suspend fun updateTransactionRejectReason(unid: String, reason: String)

    @Query("SELECT * FROM AllTX WHERE tx = '' ")
    suspend fun getWhoHasNotTX(): List<AllTX>

    @Query("SELECT * FROM AllTX WHERE info LIKE '%' || :name || '%' ORDER BY info")
    suspend fun getTransactionsByName(name: String): List<AllTX>

    @Delete
    suspend fun deleteItem(item: AllTX)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(items: List<AllTX>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AllTX)

    @Query("SELECT status FROM AllTX WHERE unid = :unid")
    suspend fun getStatus(unid: String): Int?

    @Query("DELETE FROM AllTX")
    fun clearTXs()
}