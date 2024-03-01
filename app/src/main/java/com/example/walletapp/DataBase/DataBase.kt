package com.example.walletapp.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.walletapp.DataBase.DAO.NetworksDAO
import com.example.walletapp.DataBase.DAO.SignerDao
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer

@Database(
    entities = [
        Signer::class,
        Networks::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DataBase: RoomDatabase(){
    abstract fun signerDao(): SignerDao
    abstract fun networksDao(): NetworksDAO

    companion object{
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getDatabase(context: Context): DataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "AppDataBase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}