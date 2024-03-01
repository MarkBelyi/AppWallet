package com.example.walletapp.DataBase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index(value = ["network_id", "name","addr"], unique = true)], primaryKeys = ["network_id", "name","addr"])
data class Tokens(
    val network_id: Int,  // id блокчейна (например для биткоина это 1000)
    val name: String, // имя токена, например TRX, Matic, BTC
    @ColumnInfo(collate = ColumnInfo.NOCASE) val addr: String,  //адрес контракта, породившего этот токен (или ноль)
    val myFlags: String, // какие-нить флаги, на будущее
    val decimals: Int, // на сколько сдвинуть влево запятую, чтобы привести количество токенов в божеский вид(например из САНов в ТРХ)
    val info: String,  // Мош какая инфа по токену будет в будущем
    val c:Float, // скока брать комиссию при переводе юзером этих токенов
    val cMin:Float, // Нижний порог комиссии
    val cMax:Float, //Верхний порог комиссии
    val cBase:Float //примерная комиссия СЕТИ в базовых токенах сети
)
