package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Dhikr
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText
import java.util.UUID

@Composable
fun AddDhikrDialog(
    onAddDhikr: (Dhikr) -> Unit,
    onDismiss: () -> Unit
) {
    var arabicText by remember { mutableStateOf("") }
    var transliteration by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("33") }
    var errorMsg by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = EmeraldDeepDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_dhikr_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Add Custom Dhikr",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = arabicText,
                    onValueChange = { arabicText = it },
                    label = { Text("Arabic Text (e.g. سُبْحَانَ اللَّهِ)", color = IvorySubtle) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_arabic_input"),
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = transliteration,
                    onValueChange = { transliteration = it },
                    label = { Text("Title / Transliteration", color = IvorySubtle) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_transliteration_input"),
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning / Translation (Optional)", color = IvorySubtle) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_meaning_input"),
                    colors = customTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("Target Count", color = IvorySubtle) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_target_input"),
                    colors = customTextFieldColors()
                )

                if (errorMsg.isNotEmpty()) {
                    Text(
                        text = errorMsg,
                        color = Color(0xFFEF5350),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3D Glass Add Button
                Glass3DButton(
                    onClick = {
                        val arabicClean = arabicText.trim()
                        val titleClean = transliteration.trim().ifEmpty { arabicClean }
                        if (arabicClean.isEmpty() && titleClean.isEmpty()) {
                            errorMsg = "Please enter Arabic text or title"
                            return@Glass3DButton
                        }
                        val target = targetText.toIntOrNull() ?: 33
                        val newDhikr = Dhikr(
                            id = "custom_${UUID.randomUUID()}",
                            arabic = if (arabicClean.isNotEmpty()) arabicClean else titleClean,
                            transliteration = titleClean,
                            meaning = meaning.trim().ifEmpty { "Custom Dhikr" },
                            target = target.coerceIn(1, 10000),
                            isCustom = true
                        )
                        onAddDhikr(newDhikr)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = true,
                    shape = RoundedCornerShape(14.dp),
                    height = 50.dp,
                    testTag = "save_custom_dhikr_button"
                ) {
                    Text(
                        text = "Add to Tasbih (شامل کریں)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }
            }
        }
    }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldBright,
    unfocusedBorderColor = GlassBorder,
    focusedTextColor = IvoryText,
    unfocusedTextColor = IvoryText,
    cursorColor = GoldBright
)
