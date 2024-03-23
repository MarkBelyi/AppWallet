package com.example.walletapp.registrationScreens

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.walletapp.R
import com.example.walletapp.helper.PasswordStorageHelper
import com.example.walletapp.ui.theme.roundedShape


enum class EntryState {
    ENTERING_FIRST,
    CONFIRMING
}

fun isBiometricReady(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
}

//Для будущего не удалять
/*val correctPin = "0000"

    fun onDoneClick() {
        if (pinCode.value == correctPin) {
            Toast.makeText(
                context,
                "Correct PIN",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Incorrect PIN",
                Toast.LENGTH_SHORT
            ).show()
        }
        // Сброс пин-кода после проверки
        pinCode.value = ""
    }

    LaunchedEffect(pinCode.value) {
        // Задержка выполнения проверки
        if (pinCode.value.length == 4) {
            onDoneClick()
        }
    }*/

@Composable
fun PinLockScreen(onAction: () -> Unit/*, onBiometricAuthenticated: () -> Unit*/) {
    val pinCode = remember { mutableStateOf("") }
    val firstPinCode = remember { mutableStateOf("") }
    val entryState = remember { mutableStateOf(EntryState.ENTERING_FIRST) }
    val context = LocalContext.current
    val ps = PasswordStorageHelper(context)

    /*val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Login for My App")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Use PIN instead")
        .build()

    val executor = ContextCompat.getMainExecutor(context)

    val biometricPrompt = BiometricPrompt(LocalContext.current as FragmentActivity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onAction()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
        }
    })*/


    /*fun authenticateWithBiometrics() {
        biometricPrompt.authenticate(biometricPromptInfo)
    }*/

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
                    Toast.makeText(context, "PINs do not match, please try again", Toast.LENGTH_SHORT).show()
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
            .background(colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    )  {
        Spacer(modifier = Modifier.height(48.dp))
        val textResource = if (entryState.value == EntryState.ENTERING_FIRST) {
            R.string.create_pin_code // "Create PIN-code"
        } else {
            R.string.repeat_pin_code // "Repeat PIN-code"
        }
        Text(
            text = stringResource(id = textResource),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        PinDots(pinNumber = pinCode.value)

        Spacer(modifier = Modifier.height(32.dp))

        NumPad(
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
            onBIOClick = {}// Сделать BIO,
        )

        /*if (isBiometricReady(LocalContext.current)) {
            NumPad(
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
                    authenticateWithBiometrics()
                }
            )
        } else {
            NumPad(
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
                    Toast.makeText(context, "Biometric not set up or supported", Toast.LENGTH_SHORT).show()
                }
            )
        }*/
    }
}



@Composable
fun PinDot(isFiled: Boolean) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(16.dp)
            .background(
                color = if (isFiled) colorScheme.primary else Color.LightGray,
                shape = CircleShape
            )
    )
}

@Composable
fun PinDots(pinNumber: String) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Row {
            for (i in 1..4) {
                PinDot(isFiled = pinNumber.length >= i)
            }
        }

    }


}

@Composable
fun NumPad(
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
            NumPadRow(
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
        NumPadRow(
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
fun NumPadRow(
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
            NumPadButton(
                number = number,
                iconId = when (number) {
                    "Backspace" -> R.drawable.backspace
                    "BIO" -> R.drawable.fingerprint
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
fun NumPadButton(
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
            .background(color = Color.Transparent, shape = roundedShape)
            .padding(4.dp)
            .clickable {
                when (number) {
                    "Backspace" -> onBackspaceClick()
                    "BIO" -> onBIOClick()
                    else -> onNumberClick(number)
                }
            }
    ){
        if (iconId != null) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Text(
                text = number,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


