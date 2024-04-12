package com.example.walletapp.appScreens.mainScreens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.walletapp.parse.jsonArray
import com.example.walletapp.ui.theme.roundedShape
import java.io.IOException
import java.io.InputStream
import java.util.Locale


data class Element(
    val name: String,
    val description: String,
    val type: ElementType,
    val prefsKey: String,
    val explanation: String
)

enum class ElementType { CHECKBOX, SWITCH }






@Composable
fun SettingsScreen() {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)
    //  val gson = Gson()
    val jsonStr = loadSetsFromAssets(context) //context.assets.open("settings.json").bufferedReader().use { it.readText() }


    val settingsItems=getListFromStr(jsonStr)


    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            items(settingsItems.size) { index ->
                val item = settingsItems[index]
                var checked by remember { mutableStateOf(sharedPreferences.getBoolean(item.prefsKey, false)) }
                SettingItem(title = item.name, subtitle = item.description, control = {
                    when (item.type) {
                        ElementType.CHECKBOX -> Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                sharedPreferences.edit().putBoolean(item.prefsKey, it).apply()
                            }
                        )
                        ElementType.SWITCH -> Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                sharedPreferences.edit().putBoolean(item.prefsKey, it).apply()
                            }
                        )
                    }
                })
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
        control()
    }
}

/**Грузит настройки. Что не находит заполняет "".
 * По умолчанию ставит:
 * type = CHECKBOX.
 * prefsKey = SNoKeyStub */
fun getListFromStr(ss:String):List<Element>
{
    val jarr = jsonArray(ss)
    val gg = mutableListOf<Element>()
    for (i in 0 until jarr.length())
    {val j = jarr.getJSONObject(i)
        gg.add(
            Element(
            j.optString("name", ""),
            j.optString("description",""),
            ElementType.valueOf(j.optString("type","CHECKBOX")),
            j.optString("prefsKey","SNoKeyStub"),
            j.optString("explanation","")
            )
        )
    }
    return gg
}


fun loadSetsFromAssets(context: Context, language:String=Locale.getDefault().language): String {
    val inputStream: InputStream
    return try {
        inputStream = context.assets.open("$language/settings.json")
        inputStream.bufferedReader().use { it.readText() }
    } catch (e: IOException) { e.printStackTrace(); // не смогли прочитать, пробуем вариант иглиша:
        if (language!="en") loadSetsFromAssets(context,"en") // дефолтный en обязан быть, остальные языки по желанию (нынче просят ru,es,fr)
        else return "[]" // ваще ничё нет, даже английского - всё плохо, грустно уходим!
         }
}



