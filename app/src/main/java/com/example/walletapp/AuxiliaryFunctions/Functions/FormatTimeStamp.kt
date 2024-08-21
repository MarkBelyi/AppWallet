package com.example.walletapp.AuxiliaryFunctions.Functions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun formatTimestamp(initTs: Int): String {
    val date = Date(initTs * 1000L)
    val formatter = SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}