package com.example.walletapp.DataBase.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.Signer
import kotlinx.coroutines.flow.Flow

@Dao
interface SignerDao{
    @Upsert
    suspend fun upsertSigner(signer: Signer)

    @Delete
    suspend fun deleteSigner(signer: Signer)

    @Query("SELECT * FROM signers ORDER BY address ASC")
    fun getSignersByAddress(): Flow<List<Signer>>

    @Query("SELECT * FROM signers ORDER BY name ASC")
    fun getSignersByName(): Flow<List<Signer>>

    @Query("SELECT * FROM signers ORDER BY telephone ASC")
    fun getSignersByTelephone(): Flow<List<Signer>>
}