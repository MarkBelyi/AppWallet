package com.example.walletapp.DataBase.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.Wallets

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

    @Query("UPDATE Wallets SET slist = :slist, minSignersCount = :minSigns WHERE wallet_id = :walletId")
    suspend fun updateWalletSlistAndMinSigns(walletId: Int, slist: String, minSigns: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallets)
}