package com.example.walletapp.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cri.wallet.database.BalansDAO
import com.cri.wallet.database.NetworksDAO
import com.cri.wallet.database.TokensDAO
import com.cri.wallet.database.UsersDAO
import com.cri.wallet.database.WalletsDAO
import com.cri.wallet.database.entities.Balans
import com.cri.wallet.database.entities.Networks
import com.cri.wallet.database.entities.Tokens
import com.cri.wallet.database.entities.Wallets
import com.example.walletapp.DataBase.Entities.Users


@Database(
    version = 1,
    entities = [
        Networks::class,
        Wallets::class,
        Tokens::class,
        Users::class,
        Balans::class
    ]
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun networksDAO(): NetworksDAO
    abstract fun usersDAO(): UsersDAO
    abstract fun walletsDAO(): WalletsDAO
    abstract fun tokensDAO(): TokensDAO
    abstract fun balansDAO(): BalansDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "H2K.db")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}