package com.cri.wallet.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cri.wallet.database.entities.Networks
import com.cri.wallet.database.entities.Wallets
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletsDAO {
    @Query("SELECT COUNT(*) FROM Wallets")
    suspend fun getCount(): Int

    @Query("SELECT * FROM Wallets")
    suspend fun fetchWallets(): List<Wallets>

    @Query("SELECT * FROM Wallets")
     fun getLiveWallets(): LiveData<List<Wallets>>

    @Delete
    suspend fun deleteWallet(item: Wallets)

    @Query("DELETE FROM Wallets")
    suspend fun deleteAll()

    @Upsert(entity = Wallets::class)
    suspend fun addWallets(wallets: List<Wallets>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(item: Wallets)
}