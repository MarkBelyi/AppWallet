package com.cri.wallet.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cri.wallet.database.entities.Networks
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworksDAO {
    @Query("SELECT COUNT(*) FROM Networks")
    fun getCount(): Int

    @Query("SELECT * FROM Networks")
    fun fetchNets(): Flow<List<Networks>>

    @Delete
    fun deleteNet(item: Networks)

    @Query("DELETE FROM Networks")
    suspend fun deleteAll()

    @Upsert(entity = Networks::class)
    suspend fun addNetworks(networks: List<Networks>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNet(item: Networks)
}