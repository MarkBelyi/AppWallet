package com.example.walletapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.asFlow
import com.example.walletapp.DataBase.DAO.AllTxDAO
import com.example.walletapp.DataBase.DAO.BalansDAO
import com.example.walletapp.DataBase.DAO.NetworkBalance
import com.example.walletapp.DataBase.DAO.NetworksDAO
import com.example.walletapp.DataBase.DAO.SignerDao
import com.example.walletapp.DataBase.DAO.TokensDAO
import com.example.walletapp.DataBase.DAO.TxDAO
import com.example.walletapp.DataBase.DAO.WalletAddressDao
import com.example.walletapp.DataBase.DAO.WalletsDAO
import com.example.walletapp.DataBase.Entities.AllTX
import com.example.walletapp.DataBase.Entities.Balans
import com.example.walletapp.DataBase.Entities.Networks
import com.example.walletapp.DataBase.Entities.Signer
import com.example.walletapp.DataBase.Entities.TX
import com.example.walletapp.DataBase.Entities.Tokens
import com.example.walletapp.DataBase.Entities.WalletAddress
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
    private val txDAO: TxDAO,
    private val allTxDAO: AllTxDAO,
    private val walletAddressDAO: WalletAddressDao
){
    //balans
    suspend fun getAllBalansByAddr(adr: String): List<Balans> = balansDAO.getAllByAddr(adr)

    suspend fun insertBalans(item: Balans) = balansDAO.insert(item)

    suspend fun getCombinedBalances(): List<NetworkBalance> {
        return balansDAO.getCombinedBalances()
    }

    suspend fun getCombinedBalancesWithTest(): List<NetworkBalance> {
        return balansDAO.getCombinedBalancesWithTest()
    }

    //tokens
    suspend fun insertToken(item: Tokens) = tokensDAO.insert(item)


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


    suspend fun getCount():Int{
        return signersDao.getCount()
    }

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

    //SignTX
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

    suspend fun updateTransactionRejectReason(unid: String, reason: String) {
        txDAO.updateTransactionRejectReason(unid, reason)
    }

    suspend fun getTransactionStatus(unid: String): Int? {
        return txDAO.getStatus(unid)
    }

    //AllUserTX
    val allUserTX: Flow<List<AllTX>> = allTxDAO.getAll()

    suspend fun insertUserTransaction(tx: AllTX) {
        allTxDAO.insert(tx)
    }

    suspend fun insertAllUserTransactions(txList: List<AllTX>) {
        allTxDAO.add(txList)
    }

    suspend fun getTransactionsByName(name: String): List<AllTX> {
        return allTxDAO.getTransactionsByName(name)
    }

    suspend fun getAllTransactions(): Flow<List<AllTX>> {
        return allTxDAO.getAll()
    }

    suspend fun updateUserTransactionStatus(unid: String, status: Int) {
        allTxDAO.updateTransactionStatus(unid, status)
    }

    suspend fun updateUserTransactionRejectReason(unid: String, reason: String) {
        allTxDAO.updateTransactionRejectReason(unid, reason)
    }

    suspend fun getUserTransactionStatus(unid: String): Int? {
        return allTxDAO.getStatus(unid)
    }

    //WalletAddresses
    val allWalletAddresses: Flow<List<WalletAddress>> = walletAddressDAO.getAllAddresses()

    suspend fun insertWalletAddress(item: WalletAddress){
        walletAddressDAO.insertWalletAddress(item)
    }

    suspend fun deleteWalletAddress(item: WalletAddress){
        walletAddressDAO.deleteWalletAddresses(item)
    }


    //DataBase
    suspend fun clearDataBase() {
        signersDao.clearSigners()
        networksDAO.clearNetworks()
        walletsDAO.clearWallets()
        tokensDAO.clearTokens()
        balansDAO.clearBalans()
        txDAO.clearTXs()
        allTxDAO.clearTXs()
    }
}