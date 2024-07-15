package com.example.walletapp.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.walletapp.DataBase.DAO.BalansDAO
import com.example.walletapp.DataBase.DAO.NetworksDAO
import com.example.walletapp.DataBase.DAO.SignerDao
import com.example.walletapp.DataBase.DAO.TokensDAO
import com.example.walletapp.DataBase.DAO.TxDAO
import com.example.walletapp.DataBase.DAO.WalletsDAO
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.DataBase.Entities.Tokens
import com.example.walletapp.DataBase.Entities.Wallets

@Database(
    entities = [
        Signer::class,
        Networks::class,
        Wallets::class,
        TX::class,
        Tokens::class,
        Balans::class
    ],
    version = 4,
    exportSchema = false
)
abstract class DataBase: RoomDatabase(){
    abstract fun signerDao(): SignerDao
    abstract fun networksDao(): NetworksDAO
    abstract fun walletsDao(): WalletsDAO
    abstract fun tokensDao(): TokensDAO
    abstract fun TxDAO(): TxDAO
    abstract fun balansDAO(): BalansDAO

    companion object{
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getDatabase(context: Context): DataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "AppDataBase"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}