package com.example.walletapp.DataBase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.WalletAddress
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletAddressDao {
    @Query("SELECT * FROM WalletAddress")
    fun getAllAddresses(): Flow<List<WalletAddress>>

    @Upsert
    fun insertWalletAddress(item: WalletAddress)

    @Delete
    fun deleteWalletAddresses(item: WalletAddress)

    @Upsert
    fun insertAllWalletAddress(items: List<WalletAddress>)

    @Query("SELECT COUNT(*) FROM WalletAddress")
    fun getCountOfWalletAddresses(): Int
}