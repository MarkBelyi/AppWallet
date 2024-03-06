package com.example.walletapp.repository

import androidx.annotation.WorkerThread
import com.example.walletapp.DataBase.DAO.WalletsDAO
import com.example.walletapp.DataBase.DAO.NetworksDAO
import com.example.walletapp.DataBase.DAO.SignerDao
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.Wallets
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val signersDao: SignerDao,
    private val networksDAO: NetworksDAO,
    private val walletsDAO: WalletsDAO
){
    //Wallet
    val allWallets: Flow<List<Wallets>> = walletsDAO.getWallets()


    //Singer
    val allSigners: Flow<List<Signer>> = signersDao.getSignersByName()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun upsertSigner(signer: Signer){
        signersDao.upsertSigner(signer)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteSigner(signer: Signer){
        signersDao.deleteSigner(signer)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSigner(signer: Signer){
        signersDao.insertSigner(signer)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getSignerAddress(address: String): Signer? {
        return signersDao.getSignerAddress(address)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun amountOfSigner(){
        signersDao.getCount()
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