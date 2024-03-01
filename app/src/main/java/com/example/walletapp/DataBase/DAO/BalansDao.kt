package com.cri.wallet.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.walletapp.DataBase.Entities.Balans


@Dao
interface BalansDAO {
    @Query("SELECT * FROM Balans")
    suspend fun getAll(): List<Balans>
    @Query("SELECT COUNT(*) FROM Balans")
    suspend fun getCount(): Int

    //@RewriteQueriesToDropUnusedColumns
    @Query("SELECT name,contract,'' as addr, network_id, SUM(amount) AS amount,MAX(price) as price FROM Balans GROUP BY name, network_id, contract")
    suspend fun getOverall(): List<Balans>




    @Query("SELECT * FROM Balans WHERE addr = :adr")
    suspend fun getAllByAddr(adr:String): List<Balans>

    @Query("SELECT * FROM Balans WHERE network_id = :net")
    suspend fun getAllByNet(net:Int): List<Balans>

    @Delete
    suspend fun deleteItem(item: Balans)

    @Query("DELETE FROM Balans")
    suspend fun deleteAll()

    @Upsert(entity = Balans::class)
    suspend fun add(items: List<Balans>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Balans)
}