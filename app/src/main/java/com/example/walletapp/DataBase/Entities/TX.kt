package com.example.walletapp.DataBase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["unid","id","tx"]) //транзакции
data class TX(
    @ColumnInfo(collate = ColumnInfo.NOCASE) val unid: String="",  // unid транзакции на сервере
    val id:Int=0, // id транзакции на сервере
    @ColumnInfo(collate = ColumnInfo.NOCASE) val tx:String="", //реальный хэш транзакции в сети блокчейна
    val minsign:Int=1, // Минимально необходимое колво подписантов для запуска этой транзакции в сеть
    @ColumnInfo(collate = ColumnInfo.NOCASE) val waitEC:String="", // EC Адреса подписантов, подписи которых ждёт транзакция
    @ColumnInfo(collate = ColumnInfo.NOCASE) val signedEC:String="", // EC Адреса подписантов, которые уже подписали

    //Не надо тута
    @ColumnInfo(collate = ColumnInfo.NOCASE) val waitEmail:String="", // email-Адреса подписантов, подписи которых ждёт транзакция. Пока не используется.
    @ColumnInfo(collate = ColumnInfo.NOCASE) val signedEmail:String="", // email-Адреса подписантов, которые уже подписали. Пока не используется.
    @ColumnInfo(collate = ColumnInfo.NOCASE) val waitSMS:String="", // sms-Адреса подписантов, подписи которых ждёт транзакция. Пока не используется.
    @ColumnInfo(collate = ColumnInfo.NOCASE) val signedSMS:String="", // sms-Адреса подписантов, которые уже подписали. Пока не используется.
    //во во

    val network: Int, // Код сети блокчейна (например 1000 для битка)
    val token: String, // имя токена (например TRX для трона)
    @ColumnInfo(collate = ColumnInfo.NOCASE) val to_addr: String,  // куда
    val info: String="",  // инфа
    val tx_value:Double, // сумма транзакции

    //вот здесь не надо
    val value_hex:String="", //Пока не используется
    //вот так воот

    val init_ts:Int=0, // Время создания транзакции
    val eMSG:String="", // Cообщение например об ошибке (например нехватило газа для осуществления транзакции)

    //Не надо дядя
    val instant:Int=0, // Сразу отправить транзакцию при сборе кворума или ждать подтверждения создателя транзакции. Пока не используется.
    val json_info:String="", // какая-то инфа в джейсон формате. Видимо маркаровская тема для хранения order_id. Пока не используется.
    val lifetime:Int=0, // время жизни транзакции. Пока не используется.
    val cancel:Int=0, // транзакция отменена. Пока не используется
    val deny:String="", // транзакция запрещена. Пока не используется
    val from:String="", // откуда. Пока резервное поле
    val status:Int=0, // резервное поле
    val type:Int=0, // резервное поле
    val r1:String="" // резервное поле
)
