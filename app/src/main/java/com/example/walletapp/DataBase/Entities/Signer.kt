package com.example.walletapp.DataBase.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signers")
data class Signer(
    val name: String,
    val email: String,
    val telephone: String,
    val type: Int, // Техническое поле. принадлежность адреса владельцу
    @PrimaryKey
    val address: String // EC-aдрес подписанта
)
