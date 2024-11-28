package com.example.walletapp.DataBase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    indices = [Index(value = ["name", "addr", "network_id"], unique = true)],
    primaryKeys = ["name", "addr", "network_id"]
)
data class Balans(
    val name: String, // имя токена, например TRX, Matic, BTC, USDT
    @ColumnInfo(collate = ColumnInfo.NOCASE) val contract: String,  //адрес контракта, породившего этот токен (или ноль)
    @ColumnInfo(collate = ColumnInfo.NOCASE) val addr: String,  //адрес кошелька имеющего этот токен
    val network_id: Int,  // id блокчейна (например для биткоина это 1000)
    val amount: Double = 0.0, // Количество токенов в нормальном дробном виде (получается перегонкой точки влево на Токенс.decimals)
    val price: Double = 0.0, // Скока стоит 1 такой токен в долларах
)
