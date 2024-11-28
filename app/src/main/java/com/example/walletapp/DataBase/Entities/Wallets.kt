package com.example.walletapp.DataBase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity //Кошельки
data class Wallets(
    @PrimaryKey val wallet_id: Int,  //+ id кошелька на сервере
    val network: Int, // Код сети блокчейна
    var myFlags: String = "", // свойства кошелька отвечающие за видимость и другие
    val wallet_type: Int = 1, // Тип кошелька. В норме 1 Если 3, то это кэшбэк
    val name: String = "",  // Идентификатор кошелька на сервере. имеет вид типа "05AEE241475240CB46258A5C001EDF73"
    val info: String = "",  // Имя данное кошельку владельцем при создании
    @ColumnInfo(collate = ColumnInfo.NOCASE) val addr: String = "",  // Реальный адрес кошелька в блокчейне
    val addr_info: String? = "",  // Информация о кошельке, которую владелец пожелал поведать миру
    val myUNID: String = "",  // Идентификатор кошелька в чёрном ящике. имеет вид типа "6CE19EC792163BCE46258A5C001EC2B4". Кажысь принимается из лотуса при создании кошелька чёрным ящиком.
    val tokenShortNames: String = "",  // Tокены, имеющиеся в кошельке. Имеет вид типа "78.37364 TRX;38.626781463623 USDT" Активно применяется если в настройках указано читать баланс с сервера а не со сканеров
    @ColumnInfo(collate = ColumnInfo.NOCASE) val slist: String = "", //Адреса подписантов для этого кошелька через запятую
    val minSignersCount: Int = 1, // минимальное необходимое количество подписантов
    val group_id: String = "" // не применяется пока. Видимо маркаровская тема какая-то
)