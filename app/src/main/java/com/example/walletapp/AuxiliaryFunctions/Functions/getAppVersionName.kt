package com.example.walletapp.AuxiliaryFunctions.Functions

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

fun getAppVersionName(context: Context): String {
    return try {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        "Unknown"
    }
}