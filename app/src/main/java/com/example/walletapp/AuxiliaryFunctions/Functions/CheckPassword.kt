package com.example.walletapp.AuxiliaryFunctions.Functions

fun checkPasswordsMatch(password1: String, password2: String): Boolean {
    return password1 == password2 && password1.isNotEmpty()
}

fun isPasswordValid(password: String): Boolean {
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    val isLongEnough = password.length >= 8
    return hasUpperCase && hasDigit && hasSpecialChar && isLongEnough
}

