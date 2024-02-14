package com.example.walletapp.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.DAO.SignerDao

@Database(
    entities = [Signer::class],
    version = 1,
    exportSchema = false
)

abstract class DataBase: RoomDatabase(){
    abstract val dao: SignerDao
}