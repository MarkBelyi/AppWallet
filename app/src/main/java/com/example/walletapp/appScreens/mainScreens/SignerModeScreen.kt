package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.walletapp.Element.ClickableText
import com.example.walletapp.R
import com.example.walletapp.appViewModel.appViewModel
import com.example.walletapp.ui.theme.newRoundedShape
import com.example.walletapp.ui.theme.roundedShape
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SignerModeScreen(
    viewModel: appViewModel,
    onShareClick: () -> Unit,
    navHostController: NavHostController
) {

//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = "home") {
//        composable("signerMode") { HomeScreen(navController) }
//        composable("share") { ShareAddress() }
//    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Signer Mode",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { onShareClick() }) {
                        Icon(Icons.Default.Share, "Share")
                    }
                }
            )
        }
    ) { padding ->

        val pages = listOf(
            SignerModePager.History,
            SignerModePager.CoSigner,
            SignerModePager.Requests,
            SignerModePager.Settings,
            SignerModePager.Support
        )
        val pageCount = 5
        val pagerState = rememberPagerState(pageCount = { pageCount }, initialPage = 2)

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.95f)
                    .background(color = MaterialTheme.colorScheme.background),
                verticalAlignment = Alignment.Top,
                key = { it },
                userScrollEnabled = false
            ){pageIndex ->
                when(pages[pageIndex]){
                    SignerModePager.History -> History(viewModel)
                    SignerModePager.CoSigner -> CoSigner()
                    SignerModePager.Requests -> Sign(viewModel)
                    SignerModePager.Settings -> SettingsScreen(viewModel, navHostController)
                    SignerModePager.Support -> Support()
                }
            }

            val icons = listOf(
                R.drawable.history_light,
                R.drawable.cosigner_light,
                R.drawable.sign_light,
                R.drawable.settings,
                R.drawable.support_light
            )

            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(0.1f)
                    .fillMaxWidth()
            ){
                icons.forEachIndexed { index, icon ->
                    val scale = if (index == pagerState.currentPage) 1.5f else 1f
                    val backgroundColor = if (index == pagerState.currentPage) Color.LightGray.copy(alpha = 0.1f) else Color.Transparent

                    val interactionSource = remember { MutableInteractionSource() }
                    CustomIconButton(
                        onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                        },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .weight(1f)
                        ) {
                        Icon(
                            painterResource(id = icon),
                            contentDescription = "Icon $index",
                            tint = if (index == pagerState.currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(1f)
                                .background(color = backgroundColor, shape = roundedShape)
                                .size(32.dp * scale)
                        )
                    }
                }
            }

        }

    }

}

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(viewModel: appViewModel) {
    val context = LocalContext.current
    val allTX by viewModel.allTX.observeAsState(initial = emptyList())

    LaunchedEffect(key1 = allTX) {
        viewModel.fetchAndStoreTransactions(context = context)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ){paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if(allTX.isNotEmpty()){
                TXScreens(viewModel = viewModel)
            }
            else{
                Text(
                    text = "You don't have any transactions!",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Light
                )
            }
        }

    }
}

@Composable
fun CoSigner(){
    Text(text = "Co-Signer Page")
}

@Composable
fun Requests(){
    Text(text = "Requests Page")
}

@Composable
fun Support(){
    Text(text = "Support Page")
}

sealed class SignerModePager {
    object History : SignerModePager()
    object CoSigner : SignerModePager()
    object Requests : SignerModePager()
    object Settings : SignerModePager()
    object Support : SignerModePager()
}