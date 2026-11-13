package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.CalendarDayCell
import com.example.calendar.CityCoordinates
import com.example.calendar.HijriDate
import com.example.calendar.IslamicCalendarEngine
import com.example.calendar.RamadanDaySchedule
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

enum class CalendarTab {
    SINDHI_HIJRI,
    RAMADAN
}

@Composable
fun IslamicCalendarScreen(
    wallpaperIndex: Int,
    hilalOffsetDays: Int,
    selectedCityName: String,
    initialTab: CalendarTab = CalendarTab.SINDHI_HIJRI,
    onHilalOffsetChange: (Int) -> Unit,
    onCityChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember(initialTab) { mutableStateOf(initialTab) }

    val todayHijri = remember(hilalOffsetDays) {
        IslamicCalendarEngine.getTodayHijri(hilalOffsetDays)
    }
    val todayGregorian = remember { IslamicCalendarEngine.getTodayGregorian() }

    val selectedCity = remember(selectedCityName) {
        IslamicCalendarEngine.sindhCities.find { it.name == selectedCityName }
            ?: IslamicCalendarEngine.sindhCities.first()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("islamic_calendar_screen")
    ) {
        // Sync with the rotating ultra-realistic Islamic wallpapers
        AnimatedIslamicBackground(
            autoChangeSeconds = 30,
            currentWallpaperIndex = wallpaperIndex
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar + 3D Glass Tabs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Glass3DCircleIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Tasbih",
                        isActive = false,
                        onClick = onBack,
                        testTag = "calendar_back_button"
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "اسلامي سنڌي ۽ رمضان ڪئلينڊر",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Islamic Sindhi & Ramadan Calendar",
                            fontSize = 11.sp,
                            color = IvorySubtle,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Spacer for symmetrical alignment
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3D Glass Dual Tab Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Glass3DPill(
                        selected = activeTab == CalendarTab.SINDHI_HIJRI,
                        onClick = { activeTab = CalendarTab.SINDHI_HIJRI },
                        modifier = Modifier.weight(1f),
                        testTag = "tab_sindhi_calendar"
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (activeTab == CalendarTab.SINDHI_HIJRI) EmeraldDeepDark else GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "سنڌي ڪئلينڊر",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == CalendarTab.SINDHI_HIJRI) EmeraldDeepDark else IvoryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Glass3DPill(
                        selected = activeTab == CalendarTab.RAMADAN,
                        onClick = { activeTab = CalendarTab.RAMADAN },
                        modifier = Modifier.weight(1f),
                        testTag = "tab_ramadan_calendar"
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = if (activeTab == CalendarTab.RAMADAN) EmeraldDeepDark else GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "رمضان ڪئلينڊر",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == CalendarTab.RAMADAN) EmeraldDeepDark else IvoryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Tab Content Area
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "calendar_tab_switch",
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    CalendarTab.SINDHI_HIJRI -> {
                        SindhiHijriCalendarContent(
                            todayHijri = todayHijri,
                            todayGregorian = todayGregorian,
                            hilalOffsetDays = hilalOffsetDays,
                            onHilalOffsetChange = onHilalOffsetChange
                        )
                    }
                    CalendarTab.RAMADAN -> {
                        RamadanCalendarContent(
                            hilalOffsetDays = hilalOffsetDays,
                            selectedCity = selectedCity,
                            onCitySelected = { onCityChange(it.name) }
                        )
                    }
                }
            }

            // Bottom Wapas Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Glass3DButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 48.dp,
                    testTag = "calendar_wapas_button"
                ) {
                    Text(
                        text = "🔄 Wapas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }
            }
        }
    }
}

// =========================================================================
// TAB 1: BEAUTIFUL ISLAMIC SINDHI CALENDAR
// =========================================================================

@Composable
private fun SindhiHijriCalendarContent(
    todayHijri: HijriDate,
    todayGregorian: com.example.calendar.GregorianDate,
    hilalOffsetDays: Int,
    onHilalOffsetChange: (Int) -> Unit
) {
    var displayedYear by remember { mutableIntStateOf(todayHijri.year) }
    var displayedMonth by remember { mutableIntStateOf(todayHijri.month) }
    var selectedCell by remember { mutableStateOf<CalendarDayCell?>(null) }

    val gridCells = remember(displayedYear, displayedMonth, hilalOffsetDays) {
        IslamicCalendarEngine.buildHijriMonthGrid(displayedYear, displayedMonth, hilalOffsetDays)
    }

    val activeSelectedCell = selectedCell?.takeIf {
        it.hijri.year == displayedYear && it.hijri.month == displayedMonth
    } ?: gridCells.filterNotNull().find { it.isToday } ?: gridCells.filterNotNull().firstOrNull()

    val sindhiMonthName = IslamicCalendarEngine.sindhiMonths.getOrElse(displayedMonth - 1) { "" }
    val englishMonthName = IslamicCalendarEngine.englishMonths.getOrElse(displayedMonth - 1) { "" }
    val gregorianSpan = remember(displayedYear, displayedMonth, hilalOffsetDays) {
        IslamicCalendarEngine.getGregorianSpanForHijriMonth(displayedYear, displayedMonth, hilalOffsetDays)
    }

    val todayJdn = remember(todayGregorian) {
        IslamicCalendarEngine.gregorianToJdn(todayGregorian.year, todayGregorian.month, todayGregorian.day)
    }
    val todayWeekday = IslamicCalendarEngine.dayOfWeekFromJdn(todayJdn)
    val todaySindhiWeekday = IslamicCalendarEngine.sindhiWeekdaysFull.getOrElse(todayWeekday) { "" }
    val todaySindhiMonth = IslamicCalendarEngine.sindhiMonths.getOrElse(todayHijri.month - 1) { "" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Today's Live Sindhi Islamic Date Banner
            GlassCalendarCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "اڄوڪي اسلامي تاريخ • Today",
                            fontSize = 11.sp,
                            color = GoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$todaySindhiWeekday، ${todayHijri.day} $todaySindhiMonth ${todayHijri.year} ھ",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                        Text(
                            text = "${IslamicCalendarEngine.englishWeekdays[todayWeekday]}, ${IslamicCalendarEngine.formatGregorianFull(todayGregorian)}",
                            fontSize = 12.sp,
                            color = IvorySubtle
                        )
                    }

                    if (displayedYear != todayHijri.year || displayedMonth != todayHijri.month) {
                        Glass3DPill(
                            selected = true,
                            onClick = {
                                displayedYear = todayHijri.year
                                displayedMonth = todayHijri.month
                                selectedCell = null
                            },
                            testTag = "jump_today_button"
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Today",
                                tint = EmeraldDeepDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "اڄ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldDeepDark
                            )
                        }
                    }
                }
            }

            // Month & Year Controls Card (Automatically shifts across years!)
            GlassCalendarCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Year Switcher Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Glass3DPill(
                            selected = false,
                            onClick = {
                                displayedYear -= 1
                                selectedCell = null
                            },
                            testTag = "prev_hijri_year"
                        ) {
                            Text(
                                text = "« ${displayedYear - 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$displayedYear هجري ($displayedYear AH)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight
                            )
                            Text(
                                text = gregorianSpan,
                                fontSize = 11.sp,
                                color = IvoryMuted
                            )
                        }

                        Glass3DPill(
                            selected = false,
                            onClick = {
                                displayedYear += 1
                                selectedCell = null
                            },
                            testTag = "next_hijri_year"
                        ) {
                            Text(
                                text = "${displayedYear + 1} »",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Month Switcher Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Glass3DCircleIconButton(
                            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            isActive = false,
                            onClick = {
                                if (displayedMonth == 1) {
                                    displayedMonth = 12
                                    displayedYear -= 1
                                } else {
                                    displayedMonth -= 1
                                }
                                selectedCell = null
                            },
                            testTag = "prev_hijri_month"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = sindhiMonthName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldBright,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "$englishMonthName • Month $displayedMonth / 12",
                                fontSize = 12.sp,
                                color = IvorySubtle,
                                textAlign = TextAlign.Center
                            )
                        }

                        Glass3DCircleIconButton(
                            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            isActive = false,
                            onClick = {
                                if (displayedMonth == 12) {
                                    displayedMonth = 1
                                    displayedYear += 1
                                } else {
                                    displayedMonth += 1
                                }
                                selectedCell = null
                            },
                            testTag = "next_hijri_month"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sindhi Weekdays Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IslamicCalendarEngine.sindhiWeekdaysShort.forEachIndexed { idx, dayName ->
                            val isFriday = idx == 5
                            Text(
                                text = dayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFriday) GoldBright else IvorySubtle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 7-Column Calendar Grid
                    val weeks = gridCells.chunked(7)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        weeks.forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                week.forEach { cell ->
                                    if (cell == null) {
                                        Spacer(modifier = Modifier.weight(1f).aspectRatio(0.9f))
                                    } else {
                                        val isSelected = activeSelectedCell?.hijri == cell.hijri
                                        SindhiDayCellView(
                                            cell = cell,
                                            isSelected = isSelected,
                                            onClick = { selectedCell = cell },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend + Moon Sighting Hilal Adjustment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LegendDot(color = GoldBright, label = "ڏهاڙا / Events")
                            LegendDot(color = Color(0xFFE0E5E5), label = "ايامِ بيض")
                        }

                        // Hilal Sighting +/- Days
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "چنڊ:",
                                fontSize = 11.sp,
                                color = IvoryMuted
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable {
                                        if (hilalOffsetDays > -2) onHilalOffsetChange(hilalOffsetDays - 1)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = "-", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text(
                                text = if (hilalOffsetDays >= 0) "+$hilalOffsetDays" else "$hilalOffsetDays",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable {
                                        if (hilalOffsetDays < 2) onHilalOffsetChange(hilalOffsetDays + 1)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = "+", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Selected Day Detail Card
            activeSelectedCell?.let { cell ->
                val daySindhiWeekday = IslamicCalendarEngine.sindhiWeekdaysFull.getOrElse(cell.dayOfWeek) { "" }
                val dayEnglishWeekday = IslamicCalendarEngine.englishWeekdays.getOrElse(cell.dayOfWeek) { "" }
                GlassCalendarCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$daySindhiWeekday، ${cell.hijri.day} $sindhiMonthName ${cell.hijri.year} ھ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldBright
                                )
                                Text(
                                    text = "$dayEnglishWeekday, ${IslamicCalendarEngine.formatGregorianFull(cell.gregorian)}",
                                    fontSize = 12.sp,
                                    color = IvorySubtle
                                )
                            }

                            if (cell.isToday) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(GoldPrimary.copy(alpha = 0.25f))
                                        .border(1.dp, GoldBright, CircleShape)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Today", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldBright)
                                }
                            }
                        }

                        if (cell.event != null || cell.isWhiteDay || cell.dayOfWeek == 5) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = GlassBorder, thickness = 0.7.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        cell.event?.let { ev ->
                            Text(
                                text = "✨ ${ev.titleSindhi}  •  ${ev.titleEnglish}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                        if (cell.isWhiteDay) {
                            Text(
                                text = "🌕 ايامِ بيض (مسنون روزو) — Sunnah Fasting White Day",
                                fontSize = 12.sp,
                                color = IvoryText,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        if (cell.dayOfWeek == 5) {
                            Text(
                                text = "🕌 جمعو مبارڪ — Recite Surah Al-Kahf & Salawat",
                                fontSize = 12.sp,
                                color = GoldPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // Upcoming Important Islamic Days (Live Days Remaining Countdown)
            GlassCalendarCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "✨ Aane Wale Important Days",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    val upcomingDays = remember(hilalOffsetDays) {
                        IslamicCalendarEngine.getUpcomingImportantDays(hilalOffsetDays)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        upcomingDays.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .clickable {
                                        displayedMonth = item.hijriMonth
                                        selectedCell = null
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.emoji} ${item.title}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IvoryText
                                    )
                                    Text(
                                        text = "${item.hijriDateLabel} • ${IslamicCalendarEngine.formatGregorianFull(item.gregorianDate)}",
                                        fontSize = 11.sp,
                                        color = IvorySubtle
                                    )
                                }
                                Text(
                                    text = if (item.daysRemaining == 0) "Aaj! ✨" else "${item.daysRemaining} din baqi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright
                                )
                            }
                        }
                    }
                }
            }

            // Major Islamic Events for the Selected Hijri Year (Automatically recalculates for any year!)
            GlassCalendarCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "$displayedYear ھ جا اهم اسلامي ڏهاڙا • Key Dates ($displayedYear AH)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    val majorEvents = remember(displayedYear, hilalOffsetDays) {
                        listOf(
                            1 to 1,
                            1 to 10,
                            3 to 12,
                            7 to 27,
                            8 to 15,
                            9 to 1,
                            9 to 27,
                            10 to 1,
                            12 to 9,
                            12 to 10
                        ).mapNotNull { (m, d) ->
                            val ev = IslamicCalendarEngine.getEventForHijriDate(m, d) ?: return@mapNotNull null
                            val gDate = IslamicCalendarEngine.hijriToGregorian(displayedYear, m, d, hilalOffsetDays)
                            Triple(HijriDate(displayedYear, m, d), gDate, ev)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        majorEvents.forEach { (hDate, gDate, ev) ->
                            val mSindhi = IslamicCalendarEngine.sindhiMonths[hDate.month - 1]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .clickable {
                                        displayedMonth = hDate.month
                                        selectedCell = null
                                    }
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ev.titleSindhi,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                    Text(
                                        text = "${ev.titleEnglish} • ${hDate.day} $mSindhi",
                                        fontSize = 11.sp,
                                        color = IvorySubtle
                                    )
                                }
                                Text(
                                    text = IslamicCalendarEngine.formatGregorianFull(gDate),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldBright
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SindhiDayCellView(
    cell: CalendarDayCell,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFriday = cell.dayOfWeek == 5
    val hasEvent = cell.event != null

    val bgBrush = when {
        cell.isToday -> Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFD4AF37), Color(0xFF9E7714)))
        isSelected -> Brush.verticalGradient(listOf(Color(0x55D4AF37), Color(0x28B8860B)))
        hasEvent -> Brush.verticalGradient(listOf(Color(0x3010B981), Color(0x18065F46)))
        else -> Brush.verticalGradient(listOf(Color(0x20FFFFFF), Color(0x0AFFFFFF)))
    }

    val borderColor = when {
        cell.isToday -> Color.White
        isSelected -> GoldBright
        hasEvent -> GoldPrimary.copy(alpha = 0.7f)
        else -> Color(0x28FFFFFF)
    }

    Box(
        modifier = modifier
            .aspectRatio(0.88f)
            .clip(RoundedCornerShape(10.dp))
            .background(bgBrush)
            .border(if (cell.isToday || isSelected) 1.5.dp else 0.7.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = cell.hijri.day.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    cell.isToday -> EmeraldDeepDark
                    isFriday -> GoldBright
                    else -> IvoryText
                }
            )
            Text(
                text = "${cell.gregorian.day}/${cell.gregorian.month}",
                fontSize = 9.sp,
                color = if (cell.isToday) EmeraldDeepDark.copy(alpha = 0.85f) else IvoryMuted
            )

            // Indicator dot for Events or White Days
            if (hasEvent || cell.isWhiteDay) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                cell.isToday -> EmeraldDeepDark
                                hasEvent -> GoldBright
                                else -> Color(0xFFE0E5E5)
                            }
                        )
                )
            }
        }
    }
}

// =========================================================================
// TAB 2: DYNAMIC RAMADAN CALENDAR (AUTO-CHANGES ACROSS YEARS)
// =========================================================================

@Composable
private fun RamadanCalendarContent(
    hilalOffsetDays: Int,
    selectedCity: CityCoordinates,
    onCitySelected: (CityCoordinates) -> Unit
) {
    val defaultRamadanYear = remember(hilalOffsetDays) {
        IslamicCalendarEngine.getDefaultRamadanYear(hilalOffsetDays)
    }
    var ramadanYear by remember { mutableIntStateOf(defaultRamadanYear) }
    var ashraFilter by remember { mutableIntStateOf(0) } // 0=All, 1=1st, 2=2nd, 3=3rd

    val schedule = remember(ramadanYear, selectedCity, hilalOffsetDays) {
        IslamicCalendarEngine.buildRamadanSchedule(ramadanYear, selectedCity, hilalOffsetDays)
    }
    val gregorianSpan = remember(ramadanYear, hilalOffsetDays) {
        IslamicCalendarEngine.getGregorianSpanForHijriMonth(ramadanYear, 9, hilalOffsetDays)
    }
    val daysDelta = remember(ramadanYear, hilalOffsetDays) {
        IslamicCalendarEngine.daysUntilRamadan(ramadanYear, hilalOffsetDays)
    }

    val filteredSchedule = remember(schedule, ashraFilter) {
        when (ashraFilter) {
            1 -> schedule.filter { it.ramadanDay in 1..10 }
            2 -> schedule.filter { it.ramadanDay in 11..20 }
            3 -> schedule.filter { it.ramadanDay >= 21 }
            else -> schedule
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ramadan Year Switcher & Countdown Card
            GlassCalendarCard {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Glass3DCircleIconButton(
                            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Ramadan Year",
                            isActive = false,
                            onClick = { ramadanYear -= 1 },
                            testTag = "prev_ramadan_year"
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "رمضان المبارڪ $ramadanYear ھ",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldBright
                            )
                            Text(
                                text = "Ramadan $ramadanYear AH • $gregorianSpan",
                                fontSize = 12.sp,
                                color = IvorySubtle
                            )
                        }

                        Glass3DCircleIconButton(
                            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Ramadan Year",
                            isActive = false,
                            onClick = { ramadanYear += 1 },
                            testTag = "next_ramadan_year"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status / Countdown Banner
                    val statusText = when {
                        daysDelta > 0 -> "🌙 $daysDelta days remaining until 1st Ramadan $ramadanYear AH"
                        daysDelta in -29..0 -> "✨ Today is Day ${1 - daysDelta} of Ramadan $ramadanYear AH!"
                        else -> "🤲 Ramadan $ramadanYear AH ($gregorianSpan)"
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldPrimary.copy(alpha = 0.16f))
                            .border(1.dp, GoldPrimary.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Year Pills (auto-centered around current/upcoming years)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        val yearsList = ((defaultRamadanYear - 1)..(defaultRamadanYear + 5)).toList()
                        items(yearsList) { yr ->
                            val startGreg = IslamicCalendarEngine.hijriToGregorian(yr, 9, 1, hilalOffsetDays)
                            Glass3DPill(
                                selected = yr == ramadanYear,
                                onClick = { ramadanYear = yr },
                                testTag = "ramadan_year_pill_$yr"
                            ) {
                                Text(
                                    text = "$yr (${startGreg.year})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (yr == ramadanYear) EmeraldDeepDark else IvoryText
                                )
                            }
                        }
                    }
                }
            }

            // Sindh / Pakistan City Selector for accurate Sehri & Iftar
            GlassCalendarCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "شهر چونڊيو • Sehri & Iftar City",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                        Text(
                            text = "${selectedCity.sindhiName} (${selectedCity.name})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(IslamicCalendarEngine.sindhCities) { city ->
                            val isSel = city.name == selectedCity.name
                            Glass3DPill(
                                selected = isSel,
                                onClick = { onCitySelected(city) },
                                testTag = "city_pill_${city.name}"
                            ) {
                                Text(
                                    text = "${city.sindhiName} • ${city.name}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSel) EmeraldDeepDark else IvoryText
                                )
                            }
                        }
                    }
                }
            }

            // Three Ashras Filter & Authentic Dua Card
            GlassCalendarCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val tabs = listOf(
                            0 to "مڪمل 30 ڏينهن",
                            1 to "1st عشرو رحمت",
                            2 to "2nd عشرو مغفرت",
                            3 to "3rd عشرو نجات"
                        )
                        items(tabs) { (id, label) ->
                            Glass3DPill(
                                selected = ashraFilter == id,
                                onClick = { ashraFilter = id }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ashraFilter == id) EmeraldDeepDark else IvoryText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val (duaTitle, duaArabic, duaMeaning) = when (ashraFilter) {
                        1 -> Triple(
                            "پهرئين عشري جي دعا (رحمت - Days 1–10)",
                            "رَبِّ اغْفِرْ وَارْحَمْ وَأَنْتَ خَيْرُ الرَّاحِمِينَ",
                            "My Lord, forgive and have mercy, and You are the best of the merciful."
                        )
                        2 -> Triple(
                            "ٻئين عشري جي دعا (مغفرت - Days 11–20)",
                            "أَسْتَغْفِرُ اللَّهَ رَبِّي مِنْ كُلِّ ذَنْبٍ وَأَتُوبُ إِلَيْهِ",
                            "I seek forgiveness from Allah, my Lord, for every sin and turn to Him in repentance."
                        )
                        3 -> Triple(
                            "ٽئين عشري ۽ شبِ قدر جي دعا (نجات - Days 21–30)",
                            "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
                            "O Allah, You are Pardoning and love to pardon, so pardon me."
                        )
                        else -> Triple(
                            "روزو رکڻ ۽ افطار جي نيت / دعا",
                            "وَبِصَوْمِ غَدٍ نَّوَيْتُ مِنْ شَهْرِ رَمَضَانَ • اللَّهُمَّ إِنِّي لَكَ صُمْتُ",
                            "I intend to fast tomorrow for Ramadan • O Allah, for You I have fasted and upon Your provision I break my fast."
                        )
                    }

                    Text(text = duaTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = duaArabic,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = duaMeaning,
                        fontSize = 11.sp,
                        color = IvorySubtle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }

            // Ramadan Timetable Header + 30-Day Rows
            GlassCalendarCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "روزو",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright,
                            modifier = Modifier.width(42.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "تاريخ / ڏينهن",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright,
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            modifier = Modifier.width(74.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = "Sehri",
                                tint = GoldLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "سحري",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                        Row(
                            modifier = Modifier.width(74.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Iftar",
                                tint = GoldBright,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "افطار",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )
                        }
                    }

                    HorizontalDivider(color = GlassBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        filteredSchedule.forEach { day ->
                            RamadanScheduleRow(day = day)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RamadanScheduleRow(day: RamadanDaySchedule) {
    val sindhiWeekday = IslamicCalendarEngine.sindhiWeekdaysShort.getOrElse(day.dayOfWeek) { "" }
    val gregFormatted = IslamicCalendarEngine.formatGregorianShort(day.gregorian)

    val rowBg = when {
        day.isToday -> Brush.horizontalGradient(listOf(Color(0x55FDE047), Color(0x33D4AF37)))
        day.isOddNight -> Brush.horizontalGradient(listOf(Color(0x2810B981), Color(0x18065F46)))
        day.isJumuah -> Brush.horizontalGradient(listOf(Color(0x25D4AF37), Color(0x10B8860B)))
        else -> Brush.horizontalGradient(listOf(Color(0x14FFFFFF), Color(0x08FFFFFF)))
    }

    val rowBorder = when {
        day.isToday -> GoldBright
        day.isOddNight -> Color(0x6634D399)
        day.isJumuah -> GoldPrimary.copy(alpha = 0.5f)
        else -> Color(0x20FFFFFF)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .border(if (day.isToday) 1.4.dp else 0.7.dp, rowBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day Number Badge
        Box(
            modifier = Modifier
                .width(38.dp)
                .clip(CircleShape)
                .background(
                    if (day.isToday || day.isOddNight) GoldPrimary.copy(alpha = 0.25f)
                    else Color.White.copy(alpha = 0.08f)
                )
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.ramadanDay.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (day.isToday || day.isOddNight || day.isJumuah) GoldBright else IvoryText
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Weekday + Gregorian Date + Optional Badge
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$sindhiWeekday • $gregFormatted",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = IvoryText
            )
            day.specialNoteSindhi?.let { note ->
                Text(
                    text = note,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (day.isOddNight) Color(0xFF6EE7B7) else GoldLight
                )
            }
        }

        // Sehri Time
        Text(
            text = day.sehriTime,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = IvoryText,
            modifier = Modifier.width(74.dp),
            textAlign = TextAlign.Center
        )

        // Iftar Time
        Text(
            text = day.iftarTime,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldBright,
            modifier = Modifier.width(74.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GlassCalendarCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x55FFFFFF), GlassBorder, Color(0x18000000))),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = IvorySubtle
        )
    }
}
