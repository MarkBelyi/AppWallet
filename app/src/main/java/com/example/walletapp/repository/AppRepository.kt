package com.example.walletapp.repository

import androidx.annotation.WorkerThread
import com.example.walletapp.DataBase.DAO.NetworksDAO
import com.example.walletapp.DataBase.DAO.SignerDao
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val signerDao: SignerDao,
    private val networksDAO: NetworksDAO
){
    //Singer
    val allSigners: Flow<List<Signer>> = signerDao.getSignersByName()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun upsertSigner(signer: Signer){
        signerDao.upsertSigner(signer)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteSigner(signer: Signer){
        signerDao.deleteSigner(signer)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSigner(signer: Signer){
        signerDao.insertSigner(signer)
    }
    fun amountOfSigner(){
        signerDao.getCount()
    }

    //Network
    val allNetworks: Flow<List<Networks>> = networksDAO.getNetworks()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertNetwork(network: Networks){
        networksDAO.insertNetwork(network)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addNetworks(networks: List<Networks>){
        networksDAO.addNetworks(networks)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteNetworks(){
        networksDAO.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteNetwork(network: Networks){
        networksDAO.deleteNetwork(network)
    }

    fun amountOfNetworks(){
        networksDAO.getNetworks()
    }
}