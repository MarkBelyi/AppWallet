package com.example.walletapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.asFlow
import com.cri.wallet.database.BalansDAO
import com.cri.wallet.database.TokensDAO
import com.example.walletapp.DataBase.DAO.WalletsDAO
import com.example.walletapp.DataBase.DAO.NetworksDAO
import com.example.walletapp.DataBase.DAO.SignerDao
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.Tokens
import com.example.walletapp.DataBase.Entities.Wallets
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val signersDao: SignerDao,
    private val networksDAO: NetworksDAO,
    private val walletsDAO: WalletsDAO,
    private val tokensDAO: TokensDAO,
    private val balansDAO: BalansDAO
){
    //balans
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllBalans(): List<Balans> = balansDAO.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getBalansCount(): Int = balansDAO.getCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getOverallBalans(): List<Balans> = balansDAO.getOverall()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllBalansByAddr(adr: String): List<Balans> = balansDAO.getAllByAddr(adr)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllBalansByNet(net: Int): List<Balans> = balansDAO.getAllByNet(net)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteBalansItem(item: Balans) = balansDAO.deleteItem(item)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllBalans() = balansDAO.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addBalans(items: List<Balans>) = balansDAO.add(items)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertBalans(item: Balans) = balansDAO.insert(item)

    //tokens
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertToken(item: Tokens) = tokensDAO.insert(item)
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addTokens(items: List<Tokens>) = tokensDAO.add(items)
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun tokensCount() =tokensDAO.getCount()
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteToken(item: Tokens) = tokensDAO.deleteItem(item)
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllTokens()=tokensDAO.deleteAll()
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllForNet(net:Int)=tokensDAO.getAllForNet(net)


    // Wallets
    val allWallets: Flow<List<Wallets>> = walletsDAO.getLiveWallets().asFlow() // Получаем кошельки

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertWallet(wallet: Wallets) {
        walletsDAO.insertWallet(wallet)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addWallets(wallets: List<Wallets>) {
        walletsDAO.addWallets(wallets)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteWallet(wallet: Wallets) {
        walletsDAO.deleteWallet(wallet)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllWallets() {
        walletsDAO.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCountOfWallets(): Int {
        return walletsDAO.getCount()
    }


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