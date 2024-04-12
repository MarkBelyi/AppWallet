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

@Suppress("RedundantSuspendModifier")
@WorkerThread
class AppRepository(
    private val signersDao: SignerDao,
    private val networksDAO: NetworksDAO,
    private val walletsDAO: WalletsDAO,
    private val tokensDAO: TokensDAO,
    private val balansDAO: BalansDAO
){
    //balans
    suspend fun getAllBalans(): List<Balans> = balansDAO.getAll()

    suspend fun getBalansCount(): Int = balansDAO.getCount()

    suspend fun getOverallBalans(): List<Balans> = balansDAO.getOverall()

    suspend fun getAllBalansByAddr(adr: String): List<Balans> = balansDAO.getAllByAddr(adr)

    suspend fun getAllBalansByNet(net: Int): List<Balans> = balansDAO.getAllByNet(net)

    suspend fun deleteBalansItem(item: Balans) = balansDAO.deleteItem(item)

    suspend fun deleteAllBalans() = balansDAO.deleteAll()

    suspend fun addBalans(items: List<Balans>) = balansDAO.add(items)

    suspend fun insertBalans(item: Balans) = balansDAO.insert(item)

    //tokens
    val allTokens: Flow<List<Tokens>> = tokensDAO.getLiveTokens().asFlow()
    suspend fun insertToken(item: Tokens) = tokensDAO.insert(item)

    suspend fun addTokens(items: List<Tokens>) = tokensDAO.add(items)

    suspend fun tokensCount() =tokensDAO.getCount()

    suspend fun deleteToken(item: Tokens) = tokensDAO.deleteItem(item)

    suspend fun deleteAllTokens()=tokensDAO.deleteAll()

    suspend fun getAllForNet(net:Int)=tokensDAO.getAllForNet(net)


    // Wallets
    val allWallets: Flow<List<Wallets>> = walletsDAO.getLiveWallets().asFlow() // Получаем кошельки

    suspend fun insertWallet(wallet: Wallets) {
        walletsDAO.insertWallet(wallet)
    }

    suspend fun addWallets(wallets: List<Wallets>) {
        walletsDAO.addWallets(wallets)
    }

    suspend fun deleteWallet(wallet: Wallets) {
        walletsDAO.deleteWallet(wallet)
    }

    suspend fun deleteAllWallets() {
        walletsDAO.deleteAll()
    }

    suspend fun getCountOfWallets(): Int {
        return walletsDAO.getCount()
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

    suspend fun amountOfSigner(){
        signersDao.getCount()
    }

    //Network
    val allNetworks: Flow<List<Networks>> = networksDAO.getNetworks()


    suspend fun insertNetwork(network: Networks){
        networksDAO.insertNetwork(network)
    }

    suspend fun addNetworks(networks: List<Networks>){
        networksDAO.addNetworks(networks)
    }

    suspend fun deleteNetworks(){
        networksDAO.deleteAll()
    }

    suspend fun deleteNetwork(network: Networks){
        networksDAO.deleteNetwork(network)
    }

    fun amountOfNetworks(){
        networksDAO.getNetworks()
    }
}