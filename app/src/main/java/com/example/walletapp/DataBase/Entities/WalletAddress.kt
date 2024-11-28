package com.example.walletapp.DataBase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WalletAddress(
    val ownerName: String,
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val address: String,
    val blockchain: String,
    val token: String
)