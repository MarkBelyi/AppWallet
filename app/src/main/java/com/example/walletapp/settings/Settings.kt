package com.example.walletapp.settings

import com.example.walletapp.helper.PasswordStorageHelper
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.security.SecureRandom

//сначала генерим 16 случайных чисел:
var initialEntropy = SecureRandom.getSeed(16)
//Потом из них генерим мнемоническую фразу
var mnemonic = MnemonicUtils.generateMnemonic(initialEntropy)
// Преобразуем строку мнемонической фразы в список слов:
val mnemonicList: List<String> = mnemonic.split(" ")
//имея эту фразу можно создать ключевую пару:
//Она нужна для общения с сервером
var restoreCredentials : Credentials = WalletUtils.loadBip39Credentials("markovka" ,mnemonic)

