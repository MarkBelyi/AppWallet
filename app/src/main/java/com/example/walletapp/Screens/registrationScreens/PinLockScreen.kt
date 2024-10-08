package com.example.walletapp.Screens.registrationScreens

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.walletapp.AuxiliaryFunctions.ENUM.EntryState
import com.example.walletapp.AuxiliaryFunctions.Element.PinDots
import com.example.walletapp.AuxiliaryFunctions.HelperClass.PasswordStorageHelper
import com.example.walletapp.R
import com.example.walletapp.ui.theme.newRoundedShape

@Composable
fun PinLockScreen(onAction: () -> Unit) {
    val pinCode = remember { mutableStateOf("") }
    val firstPinCode = remember { mutableStateOf("") }
    val entryState = remember { mutableStateOf(EntryState.ENTERING_FIRST) }
    val context = LocalContext.current
    val ps = PasswordStorageHelper(context)

    fun savePin(pin: String) {
        ps.setData("MyPassword", pin.toByteArray())
        Toast.makeText(context, "PIN saved successfully", Toast.LENGTH_SHORT).show()
    }

    fun handlePinEntry(pin: String) {
        when (entryState.value) {
            EntryState.ENTERING_FIRST -> {
                firstPinCode.value = pin
                pinCode.value = ""
                entryState.value = EntryState.CONFIRMING
            }

            EntryState.CONFIRMING -> {
                if (pin == firstPinCode.value) {
                    savePin(pin)
                    onAction()
                } else {
                    Toast.makeText(
                        context,
                        "PINs do not match, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    pinCode.value = ""
                    entryState.value = EntryState.ENTERING_FIRST
                }
            }
        }
    }

    LaunchedEffect(pinCode.value) {
        if (pinCode.value.length == 4) {
            handlePinEntry(pinCode.value)
        }
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.weight(1f))


        val textResource = if (entryState.value == EntryState.ENTERING_FIRST) {
            R.string.create_pin_code
        } else {
            R.string.repeat_pin_code
        }


        Text(
            text = stringResource(id = textResource),
            color = colorScheme.onSurface,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.weight(0.5f))

        PinDots(pinNumber = pinCode.value)

        Spacer(modifier = Modifier.weight(0.5f))

        RegNumPad(
            onNumberClick = { number ->
                if (pinCode.value.length < 4) {
                    pinCode.value += number
                }
            },
            onBackspaceClick = {
                if (pinCode.value.isNotEmpty()) {
                    pinCode.value = pinCode.value.dropLast(1)
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun RegNumPad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val buttonSize = (screenWidthDp - (8.dp * 6)) / 3

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 1..3) {
            RegNumPadRow(
                listOf(
                    (i * 3 - 2).toString(),
                    (i * 3 - 1).toString(),
                    (i * 3).toString()
                ),
                onNumberClick,
                onBackspaceClick,
                buttonSize
            )
        }
        RegNumPadRow(
            listOf(
                "0", "Backspace"
            ),
            onNumberClick,
            onBackspaceClick,
            buttonSize

        )
    }
}

@Composable
fun RegNumPadRow(
    numbers: List<String>,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    buttonSize: Dp
) {

    Row(

        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)

    ) {

        Spacer(modifier = Modifier.weight(1f))

        numbers.forEach { number ->
            RegNumPadButton(
                number = number,
                iconId = when (number) {
                    "Backspace" -> R.drawable.backspac_200
                    else -> null
                },
                onNumberClick = onNumberClick,
                onBackspaceClick = onBackspaceClick,
                buttonSize = buttonSize
            )
        }

    }
}

@Composable
fun RegNumPadButton(
    number: String,
    iconId: Int?,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    buttonSize: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(buttonSize)
            .height(buttonSize * 10 / 15)
            .clip(shape = newRoundedShape)
            .padding(4.dp)
            .clickable {
                when (number) {
                    "Backspace" -> onBackspaceClick()
                    else -> onNumberClick(number)
                }
            }
    ) {
        if (iconId != null) {
            Icon(
                painter = painterResource(id = iconId),
                tint = colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
            )
        } else {
            Text(
                text = number,
                color = colorScheme.onSurface,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AppNumPadRow(
    numbers: List<String>,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBIOClick: () -> Unit,
    buttonSize: Dp
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        numbers.forEach { number ->
            AppNumPadButton(
                number = number,
                iconId = when (number) {
                    "Backspace" -> R.drawable.backspac_200
                    "BIO" -> R.drawable.fingerprint_light
                    else -> null
                },
                onNumberClick = onNumberClick,
                onBackspaceClick = onBackspaceClick,
                onBIOClick = onBIOClick,
                buttonSize = buttonSize
            )
        }
    }
}

@Composable
fun AppNumPadButton(
    number: String,
    iconId: Int?,
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBIOClick: () -> Unit,
    buttonSize: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(buttonSize)
            .height(buttonSize * 10 / 15)
            .clip(shape = newRoundedShape)
            .padding(4.dp)
            .clickable {
                when (number) {
                    "Backspace" -> onBackspaceClick()
                    "BIO" -> onBIOClick()
                    else -> onNumberClick(number)
                }
            }
    ) {
        if (iconId != null) {
            Icon(
                painter = painterResource(id = iconId),
                tint = colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
            )
        } else {
            Text(
                text = number,
                color = colorScheme.onSurface,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AppNumPad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBIOClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val buttonSize = (screenWidthDp - (8.dp * 6)) / 3

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (i in 1..3) {
            AppNumPadRow(
                listOf(
                    (i * 3 - 2).toString(),
                    (i * 3 - 1).toString(),
                    (i * 3).toString()
                ),
                onNumberClick,
                onBackspaceClick,
                onBIOClick,
                buttonSize
            )
        }
        AppNumPadRow(
            listOf
                (
                "BIO", "0", "Backspace"
            ),
            onNumberClick,
            onBackspaceClick,
            onBIOClick,
            buttonSize

        )
    }
}

@Composable
fun PinLockScreenApp(onAction: () -> Unit, onBiometricAuthenticated: () -> Unit) {
    val context = LocalContext.current
    val ps = PasswordStorageHelper(context)
    val pinCode = remember { mutableStateOf(ps.getPin() ?: "") }
    val passwordBytes = ps.getData("MyPassword") ?: byteArrayOf()

    fun handlePinEntry(pin: String) {
        if (pin.toByteArray().contentEquals(passwordBytes)) {
            onAction()
            pinCode.value = ""
        } else {
            Toast.makeText(
                context,
                "PINs do not match, please try again",
                Toast.LENGTH_SHORT
            ).show()
            pinCode.value = ""
        }
    }

    fun authenticateWithBiometrics(context: Context) {
        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                    onBiometricAuthenticated()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for My App")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    LaunchedEffect(pinCode.value) {
        if (pinCode.value.length == 4) {
            handlePinEntry(pinCode.value)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Enter your PIN",
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.weight(0.5f))

        PinDots(pinNumber = pinCode.value)

        Spacer(modifier = Modifier.weight(0.5f))

        AppNumPad(
            onNumberClick = { number ->
                if (pinCode.value.length < 4) {
                    pinCode.value += number
                }
            },
            onBackspaceClick = {
                if (pinCode.value.isNotEmpty()) {
                    pinCode.value = pinCode.value.dropLast(1)
                }
            },
            onBIOClick = {
                authenticateWithBiometrics(context)
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}


