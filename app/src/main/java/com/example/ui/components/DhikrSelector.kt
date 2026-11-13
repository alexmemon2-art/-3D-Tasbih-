package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Dhikr
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryText

@Composable
fun DhikrSelector(
    dhikrs: List<Dhikr>,
    selectedDhikr: Dhikr,
    onSelectDhikr: (Dhikr) -> Unit,
    onAddCustomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dhikr_selector_row"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        itemsIndexed(dhikrs, key = { _, item -> item.id }) { _, item ->
            val isSelected = item.id == selectedDhikr.id

            Glass3DPill(
                selected = isSelected,
                onClick = { onSelectDhikr(item) },
                testTag = "dhikr_chip_${item.id}"
            ) {
                Text(
                    text = item.arabic,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = if (isSelected) EmeraldDeepDark else IvoryText
                )
            }
        }

        item {
            // 3D Glass "New Dhikr" Pill
            Glass3DPill(
                selected = false,
                onClick = onAddCustomClick,
                testTag = "add_custom_dhikr_button"
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add custom dhikr",
                    tint = GoldBright,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 2.dp)
                )
                Text(
                    text = "New Dhikr",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
            }
        }
    }
}
