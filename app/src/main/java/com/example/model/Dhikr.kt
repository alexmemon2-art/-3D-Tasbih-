package com.example.model

data class Dhikr(
    val id: String,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val target: Int,
    val isCustom: Boolean = false,
    val fazilat: String = "",
    val reference: String = ""
)

enum class BeadStyle(val displayName: String, val primaryHex: Long, val accentHex: Long) {
    EMERALD_GOLD("Emerald & Gold", 0xFF0D4A2E, 0xFFD4AF37),
    ROYAL_GOLD("Royal Gold", 0xFFB8860B, 0xFFF4D03F),
    WHITE_PEARL("White Pearl", 0xFFE0E5E5, 0xFFD4AF37),
    AGATE_AMBER("Warm Amber", 0xFFB25D1E, 0xFFF59E0B),
    LAPIS_BLUE("Lapis Lazuli", 0xFF1E3A8A, 0xFF60A5FA)
}

enum class ClickSoundStyle(val displayName: String) {
    WOODEN_BEAD("Wooden Bead"),
    SOFT_BEEP("Soft Beep"),
    CRYSTAL_TAP("Crystal Tap"),
    SILENT("Mute Click")
}

enum class CompletionSoundStyle(val displayName: String) {
    HARMONIC_CHIME("Harmonic Chime"),
    ALLAHU_AKBAR_VOICE("Allahu Akbar Voice"),
    GENTLE_BEEP("Gentle Alert Beep"),
    SILENT("Silent")
}

data class HistoryItem(
    val id: String,
    val dhikrArabic: String,
    val dhikrTitle: String,
    val count: Int,
    val timestamp: Long,
    val formattedTime: String
)

data class AppSettings(
    val defaultTarget: Int = 33,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoReset: Boolean = true,
    val keepScreenOn: Boolean = false,
    val countLocked: Boolean = false,
    val beadStyle: BeadStyle = BeadStyle.EMERALD_GOLD,
    val clickSoundStyle: ClickSoundStyle = ClickSoundStyle.WOODEN_BEAD,
    val completionSoundStyle: CompletionSoundStyle = CompletionSoundStyle.HARMONIC_CHIME,
    val dailyGoal: Int = 300
)

data class ReminderSettings(
    val fajrEnabled: Boolean = false,
    val zuhrEnabled: Boolean = false,
    val asrEnabled: Boolean = false,
    val maghribEnabled: Boolean = false,
    val ishaEnabled: Boolean = false,
    val azanAlertsEnabled: Boolean = false,
    val customReminderEnabled: Boolean = false,
    val customHour: Int = 21,
    val customMinute: Int = 0
)

data class LocationTimezoneConfig(
    val useLiveLocation: Boolean = false,
    val liveLatitude: Double? = null,
    val liveLongitude: Double? = null,
    val liveCityName: String = "",
    val useCustomTimezone: Boolean = false,
    val customTimezoneHours: Double = 5.0
)

data class CustomPrayerTimesConfig(
    val useCustomTimes: Boolean = false,
    val fajrMinutes: Int? = null,
    val zuhrMinutes: Int? = null,
    val asrMinutes: Int? = null,
    val maghribMinutes: Int? = null,
    val ishaMinutes: Int? = null
)

data class DailyDayStat(
    val dateKey: String,
    val label: String,
    val count: Int
)

data class DhikrLevelInfo(
    val level: Int,
    val title: String,
    val badge: String,
    val currentTotal: Long,
    val nextLevelThreshold: Long,
    val progressFraction: Float
)

object DhikrLevels {
    private data class Tier(val level: Int, val minCount: Long, val title: String, val badge: String)

    private val tiers = listOf(
        Tier(1, 0L, "Dhikr Level 1 • Seeker", "🌱"),
        Tier(2, 300L, "Dhikr Level 2 • Steadfast", "🌿"),
        Tier(3, 1500L, "Dhikr Level 3 • Devoted", "🌙"),
        Tier(4, 5000L, "Dhikr Level 4 • Zakir", "✨"),
        Tier(5, 15000L, "Dhikr Level 5 • Radiant Heart", "🕌"),
        Tier(6, 50000L, "Dhikr Level 6 • Beacon of Zikr", "👑"),
        Tier(7, 150000L, "Dhikr Level 7 • Master of Dhikr", "💎")
    )

    fun calculate(totalCount: Long): DhikrLevelInfo {
        var current = tiers.first()
        var nextThreshold = tiers[1].minCount
        for (i in tiers.indices) {
            if (totalCount >= tiers[i].minCount) {
                current = tiers[i]
                nextThreshold = if (i + 1 < tiers.size) tiers[i + 1].minCount else tiers[i].minCount
            }
        }
        val span = (nextThreshold - current.minCount).coerceAtLeast(1L)
        val fraction = if (nextThreshold <= current.minCount) {
            1f
        } else {
            ((totalCount - current.minCount).toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }
        return DhikrLevelInfo(
            level = current.level,
            title = current.title,
            badge = current.badge,
            currentTotal = totalCount,
            nextLevelThreshold = nextThreshold,
            progressFraction = fraction
        )
    }
}

object DefaultDhikrs {
    val list = listOf(
        Dhikr(
            id = "subhanallah",
            arabic = "سُبْحَانَ اللَّهِ",
            transliteration = "SubhanAllah",
            meaning = "Glory be to Allah • پاک ہے اللہ",
            target = 33,
            fazilat = "Mizan (scale of deeds) mein bhari aur har namaz ke baad 33 martaba padhne se gunah samandar ke jhaag ke barabar bhi hon toh maaf ho jate hain. Tasbih-e-Fatima (RA) ka pehla hissa.",
            reference = "Sahih Muslim 597"
        ),
        Dhikr(
            id = "alhamdulillah",
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            meaning = "Praise be to Allah • تمام تعریفیں اللہ کے لیے ہیں",
            target = 33,
            fazilat = "Alhamdulillah Mizan-e-Amal ko bhar deta hai. Allah ka shukr ada karne par Allah apni ne'matein aur barakat badha deta hai. Namaz ke baad 33 martaba sunnat hai.",
            reference = "Sahih Muslim 223"
        ),
        Dhikr(
            id = "allahuakbar",
            arabic = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            meaning = "Allah is the Greatest • اللہ سب سے بڑا ہے",
            target = 34,
            fazilat = "SubhanAllah aur Alhamdulillah aasman aur zameen ke darmiyan ki khali jagah ko bhar dete hain, aur Allahu Akbar dil ko Allah ki azmat aur himmat se roshan karta hai.",
            reference = "Sahih Muslim 597"
        ),
        Dhikr(
            id = "lailaha",
            arabic = "لَا إِلَهَ إِلَّا اللَّهُ",
            transliteration = "La ilaha illallah",
            meaning = "There is no deity except Allah • اللہ کے سوا کوئی معبود نہیں",
            target = 100,
            fazilat = "Sab se afzal zikr 'La ilaha illallah' hai. Jo shakhs din mein 100 martaba 'La ilaha illallahu wahdahu...' padhe use 10 ghulam azaad karne ka sawab aur shaitan se hifazat milti hai.",
            reference = "Jami' at-Tirmidhi 3383 / Bukhari 3293"
        ),
        Dhikr(
            id = "astaghfirullah",
            arabic = "أَسْتَغْفِرُ اللَّهَ",
            transliteration = "Astaghfirullah",
            meaning = "I seek forgiveness from Allah • میں اللہ سے معافی مانگتا ہوں",
            target = 100,
            fazilat = "Jo lagatar Istighfar karta rahe, Allah us ke liye har tangi se nikalne ka rasta, har gham se nijat aur aisi jagah se rizq deta hai jahan se usay gumaan bhi na ho.",
            reference = "Sunan Abi Dawud 1518"
        ),
        Dhikr(
            id = "salawat",
            arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
            transliteration = "Allahumma salli ala Muhammad",
            meaning = "O Allah, send blessings upon Muhammad ﷺ • یا اللہ محمد ﷺ پر درود نازل فرما",
            target = 100,
            fazilat = "Jo Nabi Kareem ﷺ par ek martaba Durood bhejta hai, Allah us par 10 rahmatein nazil farmata hai, 10 gunah mita deta hai aur 10 darjaat buland karta hai.",
            reference = "Sahih Muslim 408 / Nasai 1297"
        ),
        Dhikr(
            id = "lahawla",
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            meaning = "There is no power nor strength except with Allah • نیکی کی طاقت اور گناہ سے بچاؤ اللہ ہی کی توفیق سے ہے",
            target = 100,
            fazilat = "Yeh kalimat Jannat ke khazanon mein se ek khazana hai aur 99 bimariyon (jin mein sab se chhoti gham o pareshani hai) ka roohani ilaaj hai.",
            reference = "Sahih al-Bukhari 4205"
        ),
        Dhikr(
            id = "subhanallah_wabihamdih",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "SubhanAllahi wa bihamdihi, SubhanAllahil Azeem",
            meaning = "Glory and praise to Allah, Glory to Allah the Almighty • اللہ پاک ہے اپنی حمد کے ساتھ",
            target = 100,
            fazilat = "Do kalme jo zaban par halke, Mizan (tarazu) par bohot bhari aur Rahman ko bohot pyare hain. Rozana 100 martaba padhne se gunah bakhsh diye jate hain.",
            reference = "Sahih al-Bukhari 6406"
        ),
        Dhikr(
            id = "hasbunallah",
            arabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "HasbunAllahu wa ni'mal wakeel",
            meaning = "Sufficient for us is Allah, and He is the best disposer of affairs • ہمیں اللہ کافی ہے اور وہ بہترین کارساز ہے",
            target = 100,
            fazilat = "Jab Hazrat Ibrahim (AS) ko aag mein dala gaya aur jab Sahaba ko dushmanon ne ghera, toh unhon ne yahi dua padhi aur Allah ki khaas madad aur itminan hasil hua.",
            reference = "Sahih al-Bukhari 4563"
        )
    )
}
