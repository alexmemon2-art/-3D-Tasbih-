package com.example

import com.example.calendar.GregorianDate
import com.example.calendar.IslamicCalendarEngine
import com.example.data.NightModeLogic
import com.example.model.AsmaUlHusnaData
import com.example.model.DailyBaatProvider
import com.example.model.DhikrLevels
import com.example.model.HajatAzkarData
import com.example.model.MasnoonDuasData
import com.example.model.NamazGuideData
import com.example.model.SpecialDhikrsData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IslamicCalendarEngineTest {

    @Test
    fun gregorianAndHijriRoundTripWorksAcrossYears() {
        for (year in 1446..1455) {
            for (month in 1..12) {
                val gDate = IslamicCalendarEngine.hijriToGregorian(year, month, 1)
                val backToHijri = IslamicCalendarEngine.gregorianToHijri(gDate.year, gDate.month, gDate.day)
                assertEquals("Year mismatch for $year-$month", year, backToHijri.year)
                assertEquals("Month mismatch for $year-$month", month, backToHijri.month)
                assertEquals("Day mismatch for $year-$month", 1, backToHijri.day)
            }
        }
    }

    @Test
    fun hijriMonthLengthIs29Or30() {
        for (year in 1446..1452) {
            for (month in 1..12) {
                val len = IslamicCalendarEngine.getHijriMonthLength(year, month)
                assertTrue("Invalid month length $len for $year/$month", len == 29 || len == 30)
            }
        }
    }

    @Test
    fun ramadanShiftsEarlierInGregorianEachYear() {
        val ramadan1446 = IslamicCalendarEngine.hijriToGregorian(1446, 9, 1)
        val ramadan1447 = IslamicCalendarEngine.hijriToGregorian(1447, 9, 1)
        val ramadan1448 = IslamicCalendarEngine.hijriToGregorian(1448, 9, 1)

        val jdn1446 = IslamicCalendarEngine.gregorianToJdn(ramadan1446.year, ramadan1446.month, ramadan1446.day)
        val jdn1447 = IslamicCalendarEngine.gregorianToJdn(ramadan1447.year, ramadan1447.month, ramadan1447.day)
        val jdn1448 = IslamicCalendarEngine.gregorianToJdn(ramadan1448.year, ramadan1448.month, ramadan1448.day)

        val diff1 = jdn1447 - jdn1446
        val diff2 = jdn1448 - jdn1447
        assertTrue("Islamic year should be 354 or 355 days, was $diff1", diff1 in 354L..355L)
        assertTrue("Islamic year should be 354 or 355 days, was $diff2", diff2 in 354L..355L)
    }

    @Test
    fun ramadanScheduleGeneratesValidSehriAndIftarTimesForSindhCities() {
        val hyderabad = IslamicCalendarEngine.sindhCities.first { it.name == "Hyderabad" }
        val schedule = IslamicCalendarEngine.buildRamadanSchedule(1447, hyderabad)

        assertTrue("Schedule size should be 29 or 30", schedule.size in 29..30)
        assertEquals(1, schedule.first().ramadanDay)
        assertEquals(schedule.size, schedule.last().ramadanDay)

        schedule.forEach { day ->
            assertTrue("Sehri must end with AM: ${day.sehriTime}", day.sehriTime.endsWith("AM"))
            assertTrue("Iftar must end with PM: ${day.iftarTime}", day.iftarTime.endsWith("PM"))
        }
    }

    @Test
    fun hilalOffsetShiftsGregorianDatesByExpectedDays() {
        val base = IslamicCalendarEngine.hijriToJdn(1447, 9, 1, hilalOffsetDays = 0)
        val plusOne = IslamicCalendarEngine.hijriToJdn(1447, 9, 1, hilalOffsetDays = 1)
        val minusOne = IslamicCalendarEngine.hijriToJdn(1447, 9, 1, hilalOffsetDays = -1)

        assertNotEquals(base, plusOne)
        assertEquals(base - 1, plusOne)
        assertEquals(base + 1, minusOne)
    }

    @Test
    fun sindhiMonthsAndWeekdaysAreComplete() {
        assertEquals(12, IslamicCalendarEngine.sindhiMonths.size)
        assertEquals(12, IslamicCalendarEngine.englishMonths.size)
        assertEquals(7, IslamicCalendarEngine.sindhiWeekdaysShort.size)
        assertEquals(7, IslamicCalendarEngine.sindhiWeekdaysFull.size)
    }

    @Test
    fun dailyPrayerTimesAreChronologicallyOrdered() {
        val hyderabad = IslamicCalendarEngine.sindhCities.first { it.name == "Hyderabad" }
        val times = IslamicCalendarEngine.computeDailyPrayerTimes(GregorianDate(2026, 3, 15), hyderabad, true)

        assertTrue(times.fajrMinutes < times.sunriseMinutes)
        assertTrue(times.sunriseMinutes < times.zuhrMinutes)
        assertTrue(times.zuhrMinutes < times.asrMinutes)
        assertTrue(times.asrMinutes < times.maghribMinutes)
        assertTrue(times.maghribMinutes < times.ishaMinutes)
    }

    @Test
    fun qiblaBearingForSindhIsWestNorthWest() {
        val hyderabad = IslamicCalendarEngine.sindhCities.first { it.name == "Hyderabad" }
        val bearing = IslamicCalendarEngine.computeQiblaBearingDegrees(hyderabad)
        assertTrue("Expected WNW Qibla bearing ~260-275 deg, got $bearing", bearing in 255.0..280.0)
    }

    @Test
    fun asmaUlHusnaHasAll99UniqueNames() {
        assertEquals(99, AsmaUlHusnaData.all99Names.size)
        assertEquals((1..99).toList(), AsmaUlHusnaData.all99Names.map { it.number })
        assertTrue(AsmaUlHusnaData.all99Names.all { it.romanUrdu.isNotBlank() && it.arabic.isNotBlank() })
    }

    @Test
    fun masnoonDuasCollectionIsPopulatedAndValid() {
        assertEquals(20, MasnoonDuasData.allDuas.size)
        assertEquals((1..20).toList(), MasnoonDuasData.allDuas.map { it.number })
        assertTrue(
            MasnoonDuasData.allDuas.all {
                it.arabic.isNotBlank() && it.romanUrdu.isNotBlank() && it.englishMeaning.isNotBlank()
            }
        )
    }

    @Test
    fun dhikrLevelsAdvanceWithTotalCount() {
        assertEquals(1, DhikrLevels.calculate(0L).level)
        assertEquals(2, DhikrLevels.calculate(500L).level)
        assertEquals(4, DhikrLevels.calculate(6000L).level)
        assertEquals(7, DhikrLevels.calculate(200000L).level)
    }

    @Test
    fun dailyBaatHajatAzkarAndSpecialDhikrsAreValid() {
        assertTrue(DailyBaatProvider.allEntries.size >= 4)
        assertTrue(
            DailyBaatProvider.allEntries.all {
                it.ayatArabic.isNotBlank() && it.ayatUrdu.isNotBlank() && it.hadees.isNotBlank() && it.reference.isNotBlank()
            }
        )
        assertEquals(DailyBaatProvider.getDailyEntry(0), DailyBaatProvider.getDailyEntry(DailyBaatProvider.allEntries.size))

        assertTrue(HajatAzkarData.allAzkar.size >= 5)
        assertTrue(
            HajatAzkarData.allAzkar.all {
                it.title.isNotBlank() && it.dua.isNotBlank() && it.urdu.isNotBlank() && it.targetCount > 0
            }
        )

        assertEquals(3, SpecialDhikrsData.tasbihFatimaSteps.size)
        assertEquals(100, SpecialDhikrsData.tasbihFatimaSteps.sumOf { it.target })
        assertTrue(SpecialDhikrsData.allSpecialDhikrs.size >= 5)
        assertTrue(
            SpecialDhikrsData.allSpecialDhikrs.all {
                it.arabic.isNotBlank() && it.meaning.isNotBlank() && it.recommendedCount > 0
            }
        )

        assertEquals(6, NamazGuideData.steps.size)
        assertEquals((1..6).toList(), NamazGuideData.steps.map { it.stepNumber })
        assertTrue(NamazGuideData.steps.all { it.title.isNotBlank() && it.instructions.isNotEmpty() })
        assertEquals(5, NamazGuideData.rakaatSummary.size)
    }

    @Test
    fun upcomingImportantIslamicDaysReturnsSortedValidEntries() {
        val upcoming = IslamicCalendarEngine.getUpcomingImportantDays(hilalOffsetDays = 0)
        assertEquals(8, upcoming.size)
        for (i in 0 until upcoming.size - 1) {
            assertTrue(
                "Expected ascending daysRemaining, got ${upcoming[i].daysRemaining} before ${upcoming[i + 1].daysRemaining}",
                upcoming[i].daysRemaining <= upcoming[i + 1].daysRemaining
            )
        }
        assertTrue(
            upcoming.all {
                it.daysRemaining >= 0 &&
                    it.title.isNotBlank() &&
                    it.hijriDateLabel.isNotBlank() &&
                    it.gregorianDate.year >= 2024 &&
                    IslamicCalendarEngine.formatGregorianFull(it.gregorianDate).isNotBlank()
            }
        )
    }

    @Test
    fun nightModeLogicRespectsAutoWindowAndManualOverride() {
        // Auto night window is 21:00..05:59
        assertTrue(NightModeLogic.isAutoNightWindow(21))
        assertTrue(NightModeLogic.isAutoNightWindow(23))
        assertTrue(NightModeLogic.isAutoNightWindow(0))
        assertTrue(NightModeLogic.isAutoNightWindow(5))
        assertTrue(!NightModeLogic.isAutoNightWindow(6))
        assertTrue(!NightModeLogic.isAutoNightWindow(14))
        assertTrue(!NightModeLogic.isAutoNightWindow(20))

        // Auto active only during night hours unless manual override is enabled
        assertTrue(NightModeLogic.isEffectiveNightMode(autoEnabled = true, manualEnabled = false, hourOfDay = 22))
        assertTrue(!NightModeLogic.isEffectiveNightMode(autoEnabled = true, manualEnabled = false, hourOfDay = 12))
        assertTrue(NightModeLogic.isEffectiveNightMode(autoEnabled = false, manualEnabled = true, hourOfDay = 12))
        assertTrue(!NightModeLogic.isEffectiveNightMode(autoEnabled = false, manualEnabled = false, hourOfDay = 22))
    }
}
