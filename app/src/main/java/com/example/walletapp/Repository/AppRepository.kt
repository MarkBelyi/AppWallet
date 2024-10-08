package com.example.walletapp.Repository

import androidx.annotation.WorkerThread
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
) {
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
    suspend fun insertToken(item: Tokens) {
        val existingToken = tokensDAO.getToken(item.network_id, item.name, item.addr)

        if (existingToken != null) {
            // Сохраняем существующие значения комиссий
            val tokenToInsert = item.copy(
                c = existingToken.c,
                cMin = existingToken.cMin,
                cMax = existingToken.cMax,
                cBase = existingToken.cBase
            )
            tokensDAO.insert(tokenToInsert)
        } else {
            // Вставляем новый токен как есть
            tokensDAO.insert(item)
        }
    }


    suspend fun getToken(networkId: Int, name: String): Tokens? {
        return tokensDAO.getToken(networkId, name)
    }

    suspend fun getToken(networkId: Int, name: String, address: String): Tokens? {
        return tokensDAO.getToken(networkId, name, address)
    }

    suspend fun updateTokenCommissions(
        networkId: Int,
        name: String,
        c: Float,
        cMin: Float,
        cMax: Float,
        cBase: Float
    ) {
        tokensDAO.updateTokenCommissions(networkId, name, c, cMin, cMax, cBase)
    }

    // Wallets
    suspend fun addWallets(wallets: List<Wallets>) {
        walletsDAO.addWallets(wallets)
    }

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

    suspend fun getWalletByAddress(address: String): Wallets? {
        return walletsDAO.getWalletByAddress(address)
    }

    suspend fun getWalletByUNID(unid: String): Wallets? {
        return walletsDAO.getWalletByUNID(unid)
    }

    fun getWalletById(walletId: Int): Flow<Wallets> {
        return walletsDAO.getWalletById(walletId)
    }

    //Singer
    val allSigners: Flow<List<Signer>> = signersDao.getSignersByName()

    suspend fun upsertSigner(signer: Signer) {
        signersDao.upsertSigner(signer)
    }

    suspend fun deleteSigner(signer: Signer) {
        signersDao.deleteSigner(signer)
    }

    suspend fun insertSigner(signer: Signer) {
        signersDao.insertSigner(signer)
    }

    suspend fun getSignerAddress(address: String): Signer? {
        return signersDao.getSignerAddress(address)
    }

    //Network
    suspend fun addNetworks(networks: List<Networks>) {
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

    suspend fun insertAllTransactions(txList: List<TX>) {
        txDAO.add(txList)
    }

    suspend fun updateTransactionStatus(unid: String, status: Int) {
        txDAO.updateTransactionStatus(unid, status)
    }

    suspend fun updateTransactionRejectReason(unid: String, reason: String) {
        txDAO.updateTransactionRejectReason(unid, reason)
    }

    //AllUserTX
    val allUserTX: Flow<List<AllTX>> = allTxDAO.getAll()

    suspend fun insertAllUserTransactions(txList: List<AllTX>) {
        allTxDAO.add(txList)
    }

    suspend fun getAllTransactions(): Flow<List<AllTX>> {
        return allTxDAO.getAll()
    }

    //WalletAddresses
    val allWalletAddresses: Flow<List<WalletAddress>> = walletAddressDAO.getAllAddresses()

    suspend fun insertWalletAddress(item: WalletAddress) {
        walletAddressDAO.insertWalletAddress(item)
    }

    suspend fun deleteWalletAddress(item: WalletAddress) {
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