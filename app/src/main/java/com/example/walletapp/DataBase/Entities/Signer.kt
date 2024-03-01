package com.example.walletapp.DataBase.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signers")
data class Signer(
    val name: String,//Имя владельца
    val email: String,//Почта по необходимости
    val telephone: String,//Телефон также не является необходимостью
    val type: Int, // Техническое поле. принадлежность адреса владельцу
    @PrimaryKey
    val address: String // EC-aдрес подписанта
)
