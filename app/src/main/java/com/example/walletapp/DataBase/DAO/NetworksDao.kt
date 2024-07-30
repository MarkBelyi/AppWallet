package com.example.walletapp.DataBase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.Networks
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworksDAO {
    @Query("SELECT COUNT(*) FROM Networks")
    fun getCount(): Int

    @Query("SELECT * FROM Networks")
    fun getNetworks(): Flow<List<Networks>>

    @Delete
    fun deleteNetwork(item: Networks)

    @Query("DELETE FROM Networks")
    suspend fun deleteAll()

    @Upsert(entity = Networks::class)
    suspend fun addNetworks(networks: List<Networks>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetwork(item: Networks)

    @Query("SELECT * FROM Networks Where network_id IN (1000, 3000, 5000)")
    fun getMainNetworks(): Flow<List<Networks>>

    @Query("SELECT * FROM Networks Where network_id IN (1000, 1010, 3000, 3040, 5000, 5010)")
    fun getMainWithTestNetworks(): Flow<List<Networks>>

    @Query("DELETE FROM Networks")
    fun clearNetworks()

}