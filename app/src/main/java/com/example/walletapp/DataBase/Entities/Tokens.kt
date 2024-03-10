package com.example.walletapp.DataBase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index(value = ["network_id", "name","addr"], unique = true)], primaryKeys = ["network_id", "name","addr"])
data class Tokens(
    val network_id: Int,  // id блокчейна (например для биткоина это 1000)
    val name: String, // имя токена, например TRX, Matic, BTC
    @ColumnInfo(collate = ColumnInfo.NOCASE) val addr: String="",  //адрес контракта, породившего этот токен (или ноль)
    val myFlags: String="", // какие-нить флаги, на будущее
    val decimals: Int=6, // на сколько сдвинуть влево запятую, чтобы привести количество токенов в божеский вид(например из САНов в ТРХ)
    val info: String="",  // Мош какая инфа по токену будет в будущем
    val c:Float=0f, // скока брать комиссию при переводе юзером этих токенов
    val cMin:Float=0f, // Нижний порог комиссии
    val cMax:Float=0f, //Верхний порог комиссии
    val cBase:Float=0f //примерная комиссия СЕТИ в базовых токенах сети
)
