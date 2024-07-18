package com.example.walletapp.appScreens.mainScreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.walletapp.appViewModel.RegistrationViewModel
import com.example.walletapp.ui.theme.onSurface
import com.example.walletapp.ui.theme.paddingColumn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChangePasswordScreen(viewModelReg: RegistrationViewModel){

    val state = rememberPagerState(pageCount = { 4 })

    HorizontalPager(
        state = state,
        contentPadding = PaddingValues(paddingColumn),
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) {page ->
        when(page){
            0 -> VerifyMnemScreen(viewModelReg)
            1 -> ChooseAuthMethod()
            2 -> PINScreen()
            3 -> PASSWORDScreen()
        }

    }
}

@Composable
fun VerifyMnemScreen(viewModelReg: RegistrationViewModel){
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingColumn),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        val listOfMnem = viewModelReg.getMnemonicList()

        Text(text = listOfMnem.toString(), color = onSurface)



    }
}

@Composable
fun ChooseAuthMethod(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

    }
}

@Composable
fun PINScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

    }
}

@Composable
fun PASSWORDScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

    }
}