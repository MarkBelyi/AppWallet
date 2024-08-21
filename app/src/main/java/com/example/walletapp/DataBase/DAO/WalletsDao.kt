package com.example.walletapp.DataBase.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.Wallets
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletsDAO {
    @Query("SELECT COUNT(*) FROM Wallets")
    suspend fun getCount(): Int

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

    @Query("SELECT * FROM Wallets WHERE network = :network OR network = :testNetwork ORDER BY info")
    suspend fun getWalletsByNetwork(network: Int, testNetwork: Int): List<Wallets>

    @Query("SELECT * FROM Wallets WHERE info LIKE '%' || :name || '%' ORDER BY info")
    suspend fun getWalletsByName(name: String): List<Wallets>

    @Query("SELECT * FROM Wallets WHERE addr = :address LIMIT 1")
    suspend fun getWalletByAddress(address: String): Wallets?

    @Query("SELECT * FROM Wallets")
    suspend fun fetchAllWallets(): List<Wallets>

    @Query("UPDATE wallets SET myFlags = :newFlags WHERE myUNID = :unid")
    suspend fun updateWalletFlags(unid: String, newFlags: String)

    @Query("SELECT * FROM Wallets WHERE myUNID = :unid")
    suspend fun getWalletByUNID(unid: String): Wallets?

    @Query("SELECT * FROM wallets WHERE wallet_id = :walletId LIMIT 1")
    fun getWalletById(walletId: Int): Flow<Wallets>

    @Query("DELETE FROM Wallets")
    fun clearWallets()

}