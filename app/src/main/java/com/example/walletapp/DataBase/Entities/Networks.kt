package com.example.walletapp.DataBase.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Networks(
    @PrimaryKey val network_id: Int,  // id блокчейна (например для биткоина это 1000)
    val network_name: String, // полное имя сети, например ETH Ropsten Test
    val link: String,  // сайт сети (https://ethereum.org)
    val address_explorer: String, // сайт где посмотреть инфу по адресам это сети (https://ropsten.etherscan.io/address/)
    val tx_explorer:String, // сайт где посмотреть инфу по транзакциям это сети (https://ropsten.etherscan.io/tx/)
    val block_explorer:String, // сайт где посмотреть инфу по блокам это сети (https://ropsten.etherscan.io/block/)
    val info: String="",  // Мош какая инфа будет в будущем, пока нуль
    val status:Int=0 // статус этой сети у нас в системе (тоже пока всегда 1)
)