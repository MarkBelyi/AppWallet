package com.example.walletapp.DataBase.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "users")
data class Users(
     @PrimaryKey
     @ColumnInfo(collate = ColumnInfo.NOCASE) // шоп EC-aдрес подписанта сравнивался по любому регистру
     val address: String,  // EC-aдрес подписанта
     val name: String,
     val email: String,
     val telephone: String,
     val type: Int  // Техническое поле. принадлежность адреса владельцу (0 - не мой адрес, 1 - мой адрес, 2 - мой текущий адрес)
)
