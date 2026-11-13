package com.example.calendar

import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tan

data class HijriDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int    // 1..30
)

data class GregorianDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int    // 1..31
)

data class IslamicEvent(
    val titleSindhi: String,
    val titleEnglish: String,
    val isMajorEidOrNight: Boolean = true
)

data class UpcomingIslamicDay(
    val emoji: String,
    val title: String,
    val hijriMonth: Int,
    val hijriDay: Int,
    val hijriDateLabel: String,
    val gregorianDate: GregorianDate,
    val daysRemaining: Int
)

data class CalendarDayCell(
    val hijri: HijriDate,
    val gregorian: GregorianDate,
    val dayOfWeek: Int, // 0 = Sunday .. 6 = Saturday
    val isToday: Boolean,
    val isWhiteDay: Boolean, // Ayyam al-Beed: 13, 14, 15
    val event: IslamicEvent?
)

data class CityCoordinates(
    val name: String,
    val sindhiName: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneHours: Double = 5.0
)

data class RamadanDaySchedule(
    val ramadanDay: Int, // 1..30
    val hijri: HijriDate,
    val gregorian: GregorianDate,
    val dayOfWeek: Int,
    val sehriTime: String,
    val iftarTime: String,
    val isToday: Boolean,
    val isJumuah: Boolean,
    val isOddNight: Boolean,
    val specialNoteSindhi: String? = null
)

data class DailyPrayerTimes(
    val fajr: String,
    val sunrise: String,
    val zuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val fajrMinutes: Int,
    val sunriseMinutes: Int,
    val zuhrMinutes: Int,
    val asrMinutes: Int,
    val maghribMinutes: Int,
    val ishaMinutes: Int
)

object IslamicCalendarEngine {

    val sindhiMonths = listOf(
        "محرم الحرام",
        "صفر المظفر",
        "ربيع الأول",
        "ربيع الثاني",
        "جمادي الأول",
        "جمادي الثاني",
        "رجب المرجب",
        "شعبان المعظم",
        "رمضان المبارڪ",
        "شوال المڪرم",
        "ذوالقعدہ",
        "ذوالحج"
    )

    val englishMonths = listOf(
        "Muharram",
        "Safar",
        "Rabi' al-Awwal",
        "Rabi' al-Thani",
        "Jumada al-Awwal",
        "Jumada al-Akhirah",
        "Rajab",
        "Sha'ban",
        "Ramadan",
        "Shawwal",
        "Dhu al-Qi'dah",
        "Dhu al-Hijjah"
    )

    // 0 = Sunday .. 6 = Saturday
    val sindhiWeekdaysShort = listOf(
        "آچر",
        "سومر",
        "اڱارو",
        "اربع",
        "خميس",
        "جمعو",
        "ڇنڇر"
    )

    val sindhiWeekdaysFull = listOf(
        "آچر",
        "سومر",
        "اڱارو",
        "اربع",
        "خميس",
        "جمعو مبارڪ",
        "ڇنڇر"
    )

    val englishWeekdays = listOf(
        "Sunday",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday"
    )

    val gregorianMonthsShort = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val sindhCities = listOf(
        CityCoordinates("Hyderabad", "حيدرآباد", 25.3960, 68.3578),
        CityCoordinates("Karachi", "ڪراچي", 24.8607, 67.0011),
        CityCoordinates("Sukkur", "سکر", 27.7052, 68.8574),
        CityCoordinates("Larkana", "لاڙڪاڻو", 27.5570, 68.2028),
        CityCoordinates("Mirpurkhas", "ميرپورخاص", 25.5276, 69.0159),
        CityCoordinates("Nawabshah", "نوابشاهه", 26.2483, 68.4096),
        CityCoordinates("Thatta", "ٺٽو", 24.7461, 67.9243),
        CityCoordinates("Badin", "بدين", 24.6558, 68.8383),
        CityCoordinates("Islamabad", "اسلام آباد", 33.6844, 73.0479),
        CityCoordinates("Lahore", "لاهور", 31.5204, 74.3587)
    )

    fun getTodayGregorian(): GregorianDate {
        val cal = Calendar.getInstance()
        return GregorianDate(
            year = cal.get(Calendar.YEAR),
            month = cal.get(Calendar.MONTH) + 1,
            day = cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun getTodayHijri(hilalOffsetDays: Int = 0): HijriDate {
        val g = getTodayGregorian()
        return gregorianToHijri(g.year, g.month, g.day, hilalOffsetDays)
    }

    fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day + (153L * m + 2) / 5 + 365L * y + y / 4 - y / 100 + y / 400 - 32045
    }

    fun jdnToGregorian(jdn: Long): GregorianDate {
        val a = jdn + 32044
        val b = (4 * a + 3) / 146097
        val c = a - (146097 * b) / 4
        val d = (4 * c + 3) / 1461
        val e = c - (1461 * d) / 4
        val m = (5 * e + 2) / 153
        val day = (e - (153 * m + 2) / 5 + 1).toInt()
        val month = (m + 3 - 12 * (m / 10)).toInt()
        val year = (100 * b + d - 4800 + (m / 10)).toInt()
        return GregorianDate(year, month, day)
    }

    /**
     * Kuwaity / Civil Hijri to Julian Day Number.
     */
    fun hijriToJdn(hYear: Int, hMonth: Int, hDay: Int, hilalOffsetDays: Int = 0): Long {
        val raw = ((11L * hYear + 3) / 30) +
            354L * hYear +
            30L * hMonth -
            ((hMonth - 1) / 2) +
            hDay +
            1948440L -
            385L
        return raw - hilalOffsetDays
    }

    fun jdnToHijri(jdn: Long, hilalOffsetDays: Int = 0): HijriDate {
        val adjustedJdn = jdn + hilalOffsetDays
        val l = adjustedJdn - 1948440L + 10632L
        val n = (l - 1) / 10631L
        val l1 = l - 10631L * n + 354L
        val j = ((10985L - l1) / 5316L) * ((50L * l1) / 17719L) +
            (l1 / 5670L) * ((43L * l1) / 15238L)
        val l2 = l1 - ((30L - j) / 15L) * ((17719L * j) / 50L) -
            (j / 16L) * ((15238L * j) / 43L) + 29L
        val month = ((24L * l2) / 709L).toInt()
        val day = (l2 - (709L * month) / 24L).toInt()
        val year = (30L * n + j - 30L).toInt()
        return HijriDate(year, month, day)
    }

    fun gregorianToHijri(year: Int, month: Int, day: Int, hilalOffsetDays: Int = 0): HijriDate {
        val jdn = gregorianToJdn(year, month, day)
        return jdnToHijri(jdn, hilalOffsetDays)
    }

    fun hijriToGregorian(hYear: Int, hMonth: Int, hDay: Int, hilalOffsetDays: Int = 0): GregorianDate {
        val jdn = hijriToJdn(hYear, hMonth, hDay, hilalOffsetDays)
        return jdnToGregorian(jdn)
    }

    /**
     * Day of week from JDN: 0 = Sunday, 1 = Monday, ..., 5 = Friday, 6 = Saturday.
     */
    fun dayOfWeekFromJdn(jdn: Long): Int {
        // JDN 0 was Monday (1). (jdn + 1) % 7 yields 0=Sun, 1=Mon, ..., 5=Fri, 6=Sat.
        return (((jdn + 1) % 7 + 7) % 7).toInt()
    }

    fun getHijriMonthLength(hYear: Int, hMonth: Int): Int {
        val startJdn = hijriToJdn(hYear, hMonth, 1, 0)
        val nextMonth = if (hMonth == 12) 1 else hMonth + 1
        val nextYear = if (hMonth == 12) hYear + 1 else hYear
        val nextJdn = hijriToJdn(nextYear, nextMonth, 1, 0)
        return (nextJdn - startJdn).toInt().coerceIn(29, 30)
    }

    fun getEventForHijriDate(month: Int, day: Int): IslamicEvent? {
        return when (month to day) {
            1 to 1 -> IslamicEvent("نئون اسلامي سال", "Islamic New Year")
            1 to 9 -> IslamicEvent("تاسوعا محرم", "9th Muharram (Tasua)", false)
            1 to 10 -> IslamicEvent("يومِ عاشورا", "Day of Ashura")
            3 to 12 -> IslamicEvent("عيد ميلاد النبي ﷺ", "Eid Milad-un-Nabi ﷺ")
            7 to 27 -> IslamicEvent("شبِ معراج", "Shab-e-Miraj")
            8 to 15 -> IslamicEvent("شبِ براءت", "Shab-e-Barat")
            9 to 1 -> IslamicEvent("رمضان المبارڪ جي شروعات", "1st Ramadan")
            9 to 17 -> IslamicEvent("يومِ بدر", "Battle of Badr", false)
            9 to 21 -> IslamicEvent("ليلة القدر (طاق رات)", "21st Night (Laylat al-Qadr)")
            9 to 23 -> IslamicEvent("ليلة القدر (طاق رات)", "23rd Night (Laylat al-Qadr)")
            9 to 25 -> IslamicEvent("ليلة القدر (طاق رات)", "25th Night (Laylat al-Qadr)")
            9 to 27 -> IslamicEvent("ليلة القدر (ستائيسهين رات)", "27th Night (Laylat al-Qadr)")
            9 to 29 -> IslamicEvent("ليلة القدر (طاق رات)", "29th Night (Laylat al-Qadr)")
            10 to 1 -> IslamicEvent("عيد الفطر", "Eid al-Fitr")
            12 to 9 -> IslamicEvent("يومِ عرفه", "Day of Arafah")
            12 to 10 -> IslamicEvent("عيد الاضحىٰ", "Eid al-Adha")
            12 to 11 -> IslamicEvent("ايامِ تشريق", "Days of Tashriq", false)
            12 to 12 -> IslamicEvent("ايامِ تشريق", "Days of Tashriq", false)
            else -> null
        }
    }

    fun buildHijriMonthGrid(
        hYear: Int,
        hMonth: Int,
        hilalOffsetDays: Int = 0
    ): List<CalendarDayCell?> {
        val monthLength = getHijriMonthLength(hYear, hMonth)
        val firstDayJdn = hijriToJdn(hYear, hMonth, 1, hilalOffsetDays)
        val startWeekday = dayOfWeekFromJdn(firstDayJdn) // 0=Sun .. 6=Sat

        val todayGreg = getTodayGregorian()
        val todayJdn = gregorianToJdn(todayGreg.year, todayGreg.month, todayGreg.day)

        val cells = mutableListOf<CalendarDayCell?>()
        repeat(startWeekday) { cells.add(null) }

        for (day in 1..monthLength) {
            val dayJdn = firstDayJdn + (day - 1)
            val gDate = jdnToGregorian(dayJdn)
            val weekday = dayOfWeekFromJdn(dayJdn)
            val hijri = HijriDate(hYear, hMonth, day)

            cells.add(
                CalendarDayCell(
                    hijri = hijri,
                    gregorian = gDate,
                    dayOfWeek = weekday,
                    isToday = dayJdn == todayJdn,
                    isWhiteDay = day in 13..15,
                    event = getEventForHijriDate(hMonth, day)
                )
            )
        }

        while (cells.size % 7 != 0) {
            cells.add(null)
        }
        return cells
    }

    /**
     * Generates the full Ramadan schedule for any Hijri year [ramadanHijriYear],
     * automatically adapting Gregorian dates, weekdays, and astronomical Sehri/Iftar times
     * as Ramadan shifts across seasons over the years.
     */
    fun buildRamadanSchedule(
        ramadanHijriYear: Int,
        city: CityCoordinates,
        hilalOffsetDays: Int = 0
    ): List<RamadanDaySchedule> {
        val monthLength = getHijriMonthLength(ramadanHijriYear, 9)
        val firstDayJdn = hijriToJdn(ramadanHijriYear, 9, 1, hilalOffsetDays)

        val todayGreg = getTodayGregorian()
        val todayJdn = gregorianToJdn(todayGreg.year, todayGreg.month, todayGreg.day)

        val list = mutableListOf<RamadanDaySchedule>()
        for (day in 1..monthLength) {
            val dayJdn = firstDayJdn + (day - 1)
            val gDate = jdnToGregorian(dayJdn)
            val weekday = dayOfWeekFromJdn(dayJdn)
            val (sehri, iftar) = computeSehriIftarTimes(gDate, city)

            val note = when {
                day == 1 -> "پهريون روزو"
                day == 17 -> "يومِ بدر"
                day == 27 -> "شبِ قدر (27)"
                day >= 21 && day % 2 == 1 -> "طاق رات (ليلة القدر)"
                weekday == 5 -> "جمعو مبارڪ"
                else -> null
            }

            list.add(
                RamadanDaySchedule(
                    ramadanDay = day,
                    hijri = HijriDate(ramadanHijriYear, 9, day),
                    gregorian = gDate,
                    dayOfWeek = weekday,
                    sehriTime = sehri,
                    iftarTime = iftar,
                    isToday = dayJdn == todayJdn,
                    isJumuah = weekday == 5,
                    isOddNight = day >= 21 && day % 2 == 1,
                    specialNoteSindhi = note
                )
            )
        }
        return list
    }

    /**
     * Returns days until 1st Ramadan of [ramadanHijriYear] relative to today:
     * > 0 : upcoming in X days
     * -29..0 : currently in Ramadan (day = 1 - diff)
     * < -29 : already passed
     */
    fun daysUntilRamadan(ramadanHijriYear: Int, hilalOffsetDays: Int = 0): Long {
        val firstRamadanJdn = hijriToJdn(ramadanHijriYear, 9, 1, hilalOffsetDays)
        val todayGreg = getTodayGregorian()
        val todayJdn = gregorianToJdn(todayGreg.year, todayGreg.month, todayGreg.day)
        return firstRamadanJdn - todayJdn
    }

    /**
     * Suggests the current or upcoming Ramadan Hijri year automatically based on today's date.
     */
    fun getDefaultRamadanYear(hilalOffsetDays: Int = 0): Int {
        val today = getTodayHijri(hilalOffsetDays)
        return if (today.month > 9) today.year + 1 else today.year
    }

    /**
     * Computes astronomical Fajr (Sehri ends) and Maghrib (Iftar) times for a given Gregorian date and city.
     * Uses Karachi University standard (18 deg Fajr depression angle) with standard NOAA solar formulas.
     */
    fun computeSehriIftarTimes(
        date: GregorianDate,
        city: CityCoordinates
    ): Pair<String, String> {
        val jdn = gregorianToJdn(date.year, date.month, date.day).toDouble()
        val n = jdn - 2451545.0 + 0.0008 // Julian days since J2000.0
        val t = n / 36525.0

        // Geometric mean longitude & anomaly of the Sun (degrees)
        val l0 = normalizeDeg(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val m = normalizeDeg(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val mRad = Math.toRadians(m)
        val e = 0.016708634 - 0.000042037 * t - 0.0000001267 * t * t

        // Sun's equation of center
        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mRad) +
            (0.019993 - 0.000101 * t) * sin(2 * mRad) +
            0.000289 * sin(3 * mRad)
        val sunTrueLong = l0 + c
        val omega = 125.04 - 1934.136 * t
        val lambda = sunTrueLong - 0.00569 - 0.00478 * sin(Math.toRadians(omega))

        // Obliquity of the ecliptic
        val obliq = 23.439291 - 0.0130042 * t
        val obliqRad = Math.toRadians(obliq)
        val lambdaRad = Math.toRadians(lambda)

        // Solar declination
        val sinDecl = sin(obliqRad) * sin(lambdaRad)
        val declRad = asin(sinDecl)

        // Equation of time (minutes)
        val y = tan(obliqRad / 2.0) * tan(obliqRad / 2.0)
        val l0Rad = Math.toRadians(l0)
        val eotRad = y * sin(2 * l0Rad) -
            2 * e * sin(mRad) +
            4 * e * y * sin(mRad) * cos(2 * l0Rad) -
            0.5 * y * y * sin(4 * l0Rad) -
            1.25 * e * e * sin(2 * mRad)
        val eotMinutes = 4.0 * Math.toDegrees(eotRad)

        // Solar noon in local minutes from midnight
        val solarNoonMin = 720.0 - 4.0 * city.longitude - eotMinutes + 60.0 * city.timezoneHours
        val latRad = Math.toRadians(city.latitude)

        // Hour angle for Fajr (18 degrees below horizon -> zenith = 108 deg)
        val fajrHourAngleDeg = computeHourAngleDeg(latRad, declRad, 108.0)
        // Subtract 2 minutes safety ihtiyat for Sehri end
        val sehriMinutes = (solarNoonMin - 4.0 * fajrHourAngleDeg - 2.0).roundToInt()

        // Hour angle for Maghrib / Sunset (zenith = 90.833 deg)
        val maghribHourAngleDeg = computeHourAngleDeg(latRad, declRad, 90.833)
        // Add 1 minute safety ihtiyat for Iftar
        val iftarMinutes = (solarNoonMin + 4.0 * maghribHourAngleDeg + 1.0).roundToInt()

        return formatMinutes12h(sehriMinutes) to formatMinutes12h(iftarMinutes)
    }

    /**
     * Computes all 5 daily prayer times + Sunrise for [date] and [city] using Karachi University
     * standard (18 deg Fajr & Isha) and Hanafi (shadow factor 2) or Shafi (shadow factor 1) Asr.
     */
    fun computeDailyPrayerTimes(
        date: GregorianDate,
        city: CityCoordinates,
        hanafiAsr: Boolean = true
    ): DailyPrayerTimes {
        val jdn = gregorianToJdn(date.year, date.month, date.day).toDouble()
        val n = jdn - 2451545.0 + 0.0008
        val t = n / 36525.0

        val l0 = normalizeDeg(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val m = normalizeDeg(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val mRad = Math.toRadians(m)
        val e = 0.016708634 - 0.000042037 * t - 0.0000001267 * t * t

        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mRad) +
            (0.019993 - 0.000101 * t) * sin(2 * mRad) +
            0.000289 * sin(3 * mRad)
        val sunTrueLong = l0 + c
        val omega = 125.04 - 1934.136 * t
        val lambda = sunTrueLong - 0.00569 - 0.00478 * sin(Math.toRadians(omega))

        val obliq = 23.439291 - 0.0130042 * t
        val obliqRad = Math.toRadians(obliq)
        val lambdaRad = Math.toRadians(lambda)
        val declRad = asin(sin(obliqRad) * sin(lambdaRad))

        val y = tan(obliqRad / 2.0) * tan(obliqRad / 2.0)
        val l0Rad = Math.toRadians(l0)
        val eotRad = y * sin(2 * l0Rad) -
            2 * e * sin(mRad) +
            4 * e * y * sin(mRad) * cos(2 * l0Rad) -
            0.5 * y * y * sin(4 * l0Rad) -
            1.25 * e * e * sin(2 * mRad)
        val eotMinutes = 4.0 * Math.toDegrees(eotRad)

        val solarNoonMin = 720.0 - 4.0 * city.longitude - eotMinutes + 60.0 * city.timezoneHours
        val latRad = Math.toRadians(city.latitude)

        val fajrMin = (solarNoonMin - 4.0 * computeHourAngleDeg(latRad, declRad, 108.0)).roundToInt()
        val sunriseMin = (solarNoonMin - 4.0 * computeHourAngleDeg(latRad, declRad, 90.833)).roundToInt()
        val zuhrMin = (solarNoonMin + 2.0).roundToInt()

        val shadowFactor = if (hanafiAsr) 2.0 else 1.0
        val asrAltRad = atan(1.0 / (shadowFactor + tan(abs(latRad - declRad))))
        val asrZenithDeg = 90.0 - Math.toDegrees(asrAltRad)
        val asrMin = (solarNoonMin + 4.0 * computeHourAngleDeg(latRad, declRad, asrZenithDeg)).roundToInt()

        val maghribMin = (solarNoonMin + 4.0 * computeHourAngleDeg(latRad, declRad, 90.833) + 1.0).roundToInt()
        val ishaMin = (solarNoonMin + 4.0 * computeHourAngleDeg(latRad, declRad, 108.0)).roundToInt()

        return DailyPrayerTimes(
            fajr = formatMinutes12h(fajrMin),
            sunrise = formatMinutes12h(sunriseMin),
            zuhr = formatMinutes12h(zuhrMin),
            asr = formatMinutes12h(asrMin),
            maghrib = formatMinutes12h(maghribMin),
            isha = formatMinutes12h(ishaMin),
            fajrMinutes = ((fajrMin % 1440) + 1440) % 1440,
            sunriseMinutes = ((sunriseMin % 1440) + 1440) % 1440,
            zuhrMinutes = ((zuhrMin % 1440) + 1440) % 1440,
            asrMinutes = ((asrMin % 1440) + 1440) % 1440,
            maghribMinutes = ((maghribMin % 1440) + 1440) % 1440,
            ishaMinutes = ((ishaMin % 1440) + 1440) % 1440
        )
    }

    /**
     * Computes the great-circle Qibla bearing in degrees clockwise from True North (0..360)
     * from [city] toward the Holy Kaaba (21.4225 N, 39.8262 E).
     */
    fun computeQiblaBearingDegrees(city: CityCoordinates): Double {
        val kaabaLat = Math.toRadians(21.4225)
        val kaabaLon = Math.toRadians(39.8262)
        val lat = Math.toRadians(city.latitude)
        val lon = Math.toRadians(city.longitude)
        val dLon = kaabaLon - lon

        val y = sin(dLon)
        val x = cos(lat) * tan(kaabaLat) - sin(lat) * cos(dLon)
        return normalizeDeg(Math.toDegrees(atan2(y, x)))
    }

    private fun computeHourAngleDeg(latRad: Double, declRad: Double, zenithDeg: Double): Double {
        val zenithRad = Math.toRadians(zenithDeg)
        val cosHa = (cos(zenithRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
        val clamped = cosHa.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped))
    }

    private fun normalizeDeg(deg: Double): Double {
        var r = deg % 360.0
        if (r < 0) r += 360.0
        return r
    }

    private fun formatMinutes12h(totalMinutes: Int): String {
        val m = ((totalMinutes % 1440) + 1440) % 1440
        val hour24 = m / 60
        val min = m % 60
        val amPm = if (hour24 < 12) "AM" else "PM"
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        return String.format(Locale.US, "%d:%02d %s", hour12, min, amPm)
    }

    fun formatGregorianShort(g: GregorianDate): String {
        val mName = gregorianMonthsShort.getOrElse(g.month - 1) { "" }
        return "${g.day} $mName"
    }

    fun formatGregorianFull(g: GregorianDate): String {
        val mName = gregorianMonthsShort.getOrElse(g.month - 1) { "" }
        return "${g.day} $mName ${g.year}"
    }

    fun getGregorianSpanForHijriMonth(hYear: Int, hMonth: Int, hilalOffsetDays: Int = 0): String {
        val len = getHijriMonthLength(hYear, hMonth)
        val start = hijriToGregorian(hYear, hMonth, 1, hilalOffsetDays)
        val end = hijriToGregorian(hYear, hMonth, len, hilalOffsetDays)
        val sMonth = gregorianMonthsShort.getOrElse(start.month - 1) { "" }
        val eMonth = gregorianMonthsShort.getOrElse(end.month - 1) { "" }
        return when {
            start.year != end.year -> "$sMonth ${start.year} – $eMonth ${end.year}"
            start.month != end.month -> "$sMonth – $eMonth ${end.year}"
            else -> "$sMonth ${end.year}"
        }
    }

    fun getUpcomingImportantDays(hilalOffsetDays: Int = 0): List<UpcomingIslamicDay> {
        val todayGreg = getTodayGregorian()
        val todayJdn = gregorianToJdn(todayGreg.year, todayGreg.month, todayGreg.day)
        val todayHijri = jdnToHijri(todayJdn, hilalOffsetDays)

        val candidates = listOf(
            Triple("🌑", "Shab-e-Meraj", 7 to 27),
            Triple("🌙", "Shab-e-Barat", 8 to 15),
            Triple("🌙", "1st Ramzan", 9 to 1),
            Triple("✨", "Shab-e-Qadr (27th)", 9 to 27),
            Triple("🎉", "Eid-ul-Fitr", 10 to 1),
            Triple("🕌", "Youm-e-Arafat", 12 to 9),
            Triple("🐑", "Eid-ul-Adha", 12 to 10),
            Triple("🏴", "Youm-e-Ashura", 1 to 10)
        )

        val monthLabels = listOf(
            "Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani",
            "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Shaban",
            "Ramzan", "Shawwal", "ZilQad", "ZilHajj"
        )

        return candidates.map { (emoji, title, md) ->
            val (hMonth, hDay) = md
            var targetYear = todayHijri.year
            var targetJdn = hijriToJdn(targetYear, hMonth, hDay, hilalOffsetDays)
            if (targetJdn < todayJdn) {
                targetYear += 1
                targetJdn = hijriToJdn(targetYear, hMonth, hDay, hilalOffsetDays)
            }
            val gDate = jdnToGregorian(targetJdn)
            val daysLeft = (targetJdn - todayJdn).toInt().coerceAtLeast(0)
            UpcomingIslamicDay(
                emoji = emoji,
                title = title,
                hijriMonth = hMonth,
                hijriDay = hDay,
                hijriDateLabel = "$hDay ${monthLabels[hMonth - 1]} $targetYear AH",
                gregorianDate = gDate,
                daysRemaining = daysLeft
            )
        }.sortedBy { it.daysRemaining }
    }
}
