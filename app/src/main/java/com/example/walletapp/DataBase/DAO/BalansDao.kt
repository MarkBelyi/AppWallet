package com.example.walletapp.DataBase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.Balans


@Dao
interface BalansDAO {

    @Query("SELECT COUNT(*) FROM Balans")
    suspend fun getCount(): Int

    @Query("SELECT * FROM Balans WHERE addr = :adr")
    suspend fun getAllByAddr(adr:String): List<Balans>

    @Delete
    suspend fun deleteItem(item: Balans)

    @Query("DELETE FROM Balans")
    suspend fun deleteAll()

    @Upsert(entity = Balans::class)
    suspend fun add(items: List<Balans>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Balans)

    @Query("SELECT name, network_id, SUM(amount) AS totalAmount FROM Balans WHERE network_id NOT IN (5010, 6010, 3310, 3040, 1010) GROUP BY name, network_id")
    suspend fun getCombinedBalances(): List<NetworkBalance>

    @Query("SELECT name, network_id, SUM(amount) AS totalAmount FROM Balans GROUP BY name, network_id")
    suspend fun getCombinedBalancesWithTest(): List<NetworkBalance>

    @Query("DELETE FROM Balans")
    fun clearBalans()
}

data class NetworkBalance(
    val name: String,
    val network_id: Int,
    val totalAmount: Double
)