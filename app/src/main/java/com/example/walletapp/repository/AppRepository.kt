package com.example.walletapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.asFlow
import com.example.walletapp.DataBase.DAO.BalansDAO
import com.example.walletapp.DataBase.DAO.NetworkBalance
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
import kotlinx.coroutines.flow.Flow

@Suppress("RedundantSuspendModifier")
@WorkerThread
class AppRepository(
    private val signersDao: SignerDao,
    private val networksDAO: NetworksDAO,
    private val walletsDAO: WalletsDAO,
    private val tokensDAO: TokensDAO,
    private val balansDAO: BalansDAO,
    private val txDAO: TxDAO
){
    //balans
    suspend fun getAllBalansByAddr(adr: String): List<Balans> = balansDAO.getAllByAddr(adr)

    suspend fun insertBalans(item: Balans) = balansDAO.insert(item)

    //tokens
    suspend fun insertToken(item: Tokens) = tokensDAO.insert(item)

    suspend fun getCombinedBalances(): List<NetworkBalance> {
        return balansDAO.getCombinedBalances()
    }

    // Wallets
    val allWallets: Flow<List<Wallets>> = walletsDAO.getLiveWallets().asFlow() // Получаем кошельки

    suspend fun addWallets(wallets: List<Wallets>) {
        walletsDAO.addWallets(wallets)
    }

    // New methods
    suspend fun getOnlyHiddenWallets(): List<Wallets> {
        return walletsDAO.getOnlyHiddenWallets()
    }

    suspend fun getVisibleWallets(): List<Wallets> {
        return walletsDAO.getVisibleWallets()
    }

    //NEW
    suspend fun getWalletsByNetwork(network: Int, testNetwork: Int): List<Wallets> {
        return walletsDAO.getWalletsByNetwork(network, testNetwork)
    }

    suspend fun getWalletsByName(name: String): List<Wallets> {
        return walletsDAO.getWalletsByName(name)
    }

    suspend fun fetchAllWallets(): List<Wallets> {
        return walletsDAO.fetchAllWallets()
    }

    suspend fun updateWalletFlags(unid: String, newFlags: String) {
        walletsDAO.updateWalletFlags(unid, newFlags)
    }

    //Singer
    val allSigners: Flow<List<Signer>> = signersDao.getSignersByName()

    suspend fun upsertSigner(signer: Signer){
        signersDao.upsertSigner(signer)
    }

    suspend fun deleteSigner(signer: Signer){
        signersDao.deleteSigner(signer)
    }

    suspend fun insertSigner(signer: Signer){
        signersDao.insertSigner(signer)
    }

    suspend fun getSignerAddress(address: String): Signer? {
        return signersDao.getSignerAddress(address)
    }

    //Network
    val allNetworks: Flow<List<Networks>> = networksDAO.getNetworks()

    suspend fun addNetworks(networks: List<Networks>){
        networksDAO.addNetworks(networks)
    }

    fun getMainNetworks(): Flow<List<Networks>> {
        return networksDAO.getMainNetworks()
    }

    fun getMainWithTestNetworks(): Flow<List<Networks>> {
        return networksDAO.getMainWithTestNetworks()
    }

    //TX
    val allTX: Flow<List<TX>> = txDAO.getAll()

    suspend fun insertTransaction(tx: TX) {
        txDAO.insert(tx)
    }

    suspend fun insertAllTransactions(txList: List<TX>) {
        txDAO.add(txList)
    }

    suspend fun updateTransactionStatus(unid: String, status: Int) {
        txDAO.updateTransactionStatus(unid, status)
    }

    suspend fun getTransactionStatus(unid: String): Int? {
        return txDAO.getStatus(unid)
    }
}