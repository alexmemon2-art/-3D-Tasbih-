package com.example.model

import java.util.Calendar

data class DailyBaatData(
    val dayNumber: Int,
    val ayatArabic: String,
    val ayatUrdu: String,
    val ayatReference: String,
    val hadees: String,
    val reference: String
)

object DailyBaatProvider {
    val allEntries: List<DailyBaatData> = listOf(
        DailyBaatData(
            dayNumber = 1,
            ayatArabic = "وَمَا خَلَقْتُ الْجِنَّ وَالْإِنْسَ إِلَّا لِيَعْبُدُونِ",
            ayatUrdu = "Aur main ne jinn aur insaan ko sirf isliye paida kiya hai ke woh meri ibadat karein.",
            ayatReference = "Surah Adh-Dhariyat (51:56)",
            hadees = "Nabi ﷺ ne farmaya: Insaan ki sab se behtar ibadat Allah ka zikr hai.",
            reference = "Sahih Bukhari"
        ),
        DailyBaatData(
            dayNumber = 2,
            ayatArabic = "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ",
            ayatUrdu = "Tum mera zikr karo, main tumhara zikr karunga, aur mera shukr ada karo aur naa-shukri na karo.",
            ayatReference = "Surah Al-Baqarah (2:152)",
            hadees = "Nabi ﷺ ne farmaya: Allah ki raah mein ek baar bhi uska naam lene se tumhare naam ek sawab likh diya jata hai.",
            reference = "Musnad Ahmad"
        ),
        DailyBaatData(
            dayNumber = 3,
            ayatArabic = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            ayatUrdu = "Beshak mushkil ke sath aasani bhi hai.",
            ayatReference = "Surah Ash-Sharh (94:6)",
            hadees = "Jab tum Allah ko yaad karo toh dil ko sukoon milta hai.",
            reference = "Sahih Muslim"
        ),
        DailyBaatData(
            dayNumber = 4,
            ayatArabic = "وَذَكِّرْ فَإِنَّ الذِّكْرَىٰ تَنفَعُ الْمُؤْمِنِينَ",
            ayatUrdu = "Aur tum samjhate raho, kyunke yaad dilane se momin ko faida hota hai.",
            ayatReference = "Surah Adh-Dhariyat (51:55)",
            hadees = "Jo log Allah ke zikr mein masroof rehte hain, unke liye har mushkil aasan ho jati hai.",
            reference = "Jami at-Tirmidhi"
        ),
        DailyBaatData(
            dayNumber = 5,
            ayatArabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            ayatUrdu = "Sun lo! Allah ke zikr se hi dilon ko itminan aur sukoon milta hai.",
            ayatReference = "Surah Ar-Ra'd (13:28)",
            hadees = "Jo shakhs apne Rab ka zikr karta hai aur jo nahi karta unki misaal zinda aur murda ki si hai.",
            reference = "Sahih Bukhari"
        ),
        DailyBaatData(
            dayNumber = 6,
            ayatArabic = "وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُمْ",
            ayatUrdu = "Aur tumhare Rab ne farmaya: Mujh se dua karo, main tumhari dua qubool karunga.",
            ayatReference = "Surah Ghafir (40:60)",
            hadees = "Dua ibadat ka maghz (rooh) hai; Allah us shakhs se khush hota hai jo us se mangta hai.",
            reference = "Jami at-Tirmidhi"
        ),
        DailyBaatData(
            dayNumber = 7,
            ayatArabic = "لَئِن شَكَرْتُمْ لَأَزِيدَنَّكُمْ",
            ayatUrdu = "Agar tum shukr ada karoge toh main tumhe aur zyada ata karunga.",
            ayatReference = "Surah Ibrahim (14:7)",
            hadees = "Alhamdulillah kehna neaimaton ko mehfooz karta hai aur unmein barkat dalta hai.",
            reference = "Sahih Muslim"
        ),
        DailyBaatData(
            dayNumber = 8,
            ayatArabic = "وَاسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ",
            ayatUrdu = "Aur sabr aur namaz ke zariye Allah se madad talab karo.",
            ayatReference = "Surah Al-Baqarah (2:45)",
            hadees = "Momin ka har maamla ajib aur khair wala hota hai: khushi mein shukr aur takleef mein sabr karta hai.",
            reference = "Sahih Muslim"
        ),
        DailyBaatData(
            dayNumber = 9,
            ayatArabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            ayatUrdu = "Ae hamare Rab! Hamein duniya mein bhalai de aur aakhirat mein bhi bhalai de aur dozakh ke azaab se bacha.",
            ayatReference = "Surah Al-Baqarah (2:201)",
            hadees = "Nabi ﷺ sab se zyada yahi jame dua manga karte the.",
            reference = "Sahih Bukhari & Muslim"
        ),
        DailyBaatData(
            dayNumber = 10,
            ayatArabic = "إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ",
            ayatUrdu = "Beshak Allah aur uske farishte Nabi ﷺ par durood bhejte hain; ae imaan walo tum bhi un par durood aur salam bhejo.",
            ayatReference = "Surah Al-Ahzab (33:56)",
            hadees = "Jo shakhs mujh par ek baar durood bhejta hai, Allah us par das rehmaten nazil farmata hai.",
            reference = "Sahih Muslim"
        ),
        DailyBaatData(
            dayNumber = 11,
            ayatArabic = "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ",
            ayatUrdu = "Aur jo Allah par bharosa karta hai toh Allah uske liye kaafi ho jata hai.",
            ayatReference = "Surah At-Talaq (65:3)",
            hadees = "Agar tum Allah par haqeeqi tawakkul karo toh woh tumhe parindon ki tarah rizq dega.",
            reference = "Jami at-Tirmidhi"
        ),
        DailyBaatData(
            dayNumber = 12,
            ayatArabic = "لَا تَقْنَطُوا مِن رَّحْمَةِ اللَّهِ ۚ إِنَّ اللَّهَ يَغْفِرُ الذُّنُوبَ جَمِيعًا",
            ayatUrdu = "Allah ki rehmat se mayoos na ho; beshak Allah tamam gunah bakhsh deta hai.",
            ayatReference = "Surah Az-Zumar (39:53)",
            hadees = "Allah Ta'ala raat ko apna dast-e-rehmat phelata hai taake din ko gunah karne wala taubah kare.",
            reference = "Sahih Muslim"
        )
    )

    fun getTodayIndex(): Int {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return dayOfYear % allEntries.size
    }

    fun getDailyEntry(index: Int = getTodayIndex()): DailyBaatData {
        val safeIdx = ((index % allEntries.size) + allEntries.size) % allEntries.size
        return allEntries[safeIdx]
    }
}

data class HajatAzkar(
    val id: String,
    val title: String,
    val dua: String,
    val transliteration: String,
    val urdu: String,
    val countLabel: String,
    val targetCount: Int,
    val whenToRead: String,
    val reference: String
)

object HajatAzkarData {
    val allAzkar: List<HajatAzkar> = listOf(
        HajatAzkar(
            id = "hajat_pareshani",
            title = "🤲 Pareshani ke waqt",
            dua = "لَا إِلَٰهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَٰهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ",
            transliteration = "La ilaha illallahu-l-'Azimu-l-Halim, La ilaha illallahu Rabbu-l-'Arshi-l-'Azim",
            urdu = "Allah ke siwa koi ma'bud nahi, Woh bada aur burdbar hai. Allah ke siwa koi ma'bud nahi, Woh arsh-e-azim ka Malik hai.",
            countLabel = "7 baar",
            targetCount = 7,
            whenToRead = "Jab koi pareshani ya mushkil aaye",
            reference = "Sahih Bukhari #6346"
        ),
        HajatAzkar(
            id = "hajat_rizq",
            title = "💰 Rizq mein barkat ke liye",
            dua = "مَا شَاءَ اللَّهُ كَانَ، وَمَا لَمْ يَشَأْ لَمْ يَكُنْ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            transliteration = "Ma sha'Allahu kana wa ma lam yasha' lam yakun, wa la hawla wa la quwwata illa billahi-l-'Aliyyi-l-'Azim",
            urdu = "Jo Allah chahta hai woh hota hai, aur jo nahi chahta woh nahi hota. Allah ke siwa koi taqat nahi.",
            countLabel = "Rozana 100 baar",
            targetCount = 100,
            whenToRead = "Subah ke waqt aur rozgar se pehle",
            reference = "Hisn al-Hasin / Azkar"
        ),
        HajatAzkar(
            id = "hajat_gham",
            title = "❤️ Gham door karne ke liye",
            dua = "اللَّهُمَّ إِنِّي عَبْدُكَ ابْنُ عَبْدِكَ ابْنُ أَمَتِكَ، نَاصِيَتِي بِيَدِكَ",
            transliteration = "Allahumma inni 'abduka ibnu 'abdika ibnu amatika, nasiyati bi-yadika",
            urdu = "Ae Allah! Main tera banda hoon, tere bande ka beta hoon. Meri peshani tere haath mein hai, tera faisla mujh par jaari hai.",
            countLabel = "33 baar / Har waqt",
            targetCount = 33,
            whenToRead = "Jab dil ghabraye ya gham mehsoos ho",
            reference = "Musnad Ahmad #3712"
        ),
        HajatAzkar(
            id = "hajat_sehat",
            title = "🏥 Sehat aur Shifa ke liye",
            dua = "بِسْمِ اللَّهِ أُعِيذُ نَفْسِي بِاللَّهِ وَبِقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ",
            transliteration = "Bismillahi u'eedhu nafsi billahi wa bi-qudratihi min sharri ma ajidu wa uhadhiru",
            urdu = "Allah ke naam se main apne aap ko Allah ki panah aur uski qudrat mein deta hoon har takleef aur dard ke shar se.",
            countLabel = "3 baar subah, 3 baar sham",
            targetCount = 7,
            whenToRead = "Rozana subah aur shaam (dard par haath rakh kar)",
            reference = "Sahih Muslim #2202"
        ),
        HajatAzkar(
            id = "hajat_qayyum",
            title = "🌙 Hajat poori hone ke liye",
            dua = "يَا حَيُّ يَا قَيُّومُ لَا إِلَٰهَ إِلَّا أَنْتَ بِرَحْمَتِكَ أَسْتَغِيثُ",
            transliteration = "Ya Hayyu Ya Qayyumu la ilaha illa Anta, bi-rahmatika astaghith",
            urdu = "Ae Hamesha Zinda aur Qaim rakhne wale! Tere siwa koi ma'bud nahi, main teri rehmat ke zariye fariyad karta hoon.",
            countLabel = "40 baar",
            targetCount = 40,
            whenToRead = "Khas hajat aur dua ke waqt",
            reference = "Jami at-Tirmidhi #3524"
        ),
        HajatAzkar(
            id = "hajat_yunus",
            title = "🐋 Mushkil Kushai (Ayat-e-Kareema)",
            dua = "لَا إِلَٰهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
            transliteration = "La ilaha illa Anta Subhanaka inni kuntu mina-z-zalimin",
            urdu = "Tere siwa koi ma'bud nahi, Tu paak hai, beshak main hi gunahgaron mein se tha.",
            countLabel = "40 ya 100 baar",
            targetCount = 40,
            whenToRead = "Kisi bhi sakht mushkil ke waqt",
            reference = "Surah Al-Anbiya (21:87) / Tirmidhi"
        ),
        HajatAzkar(
            id = "hajat_istighfar",
            title = "🕊️ Rizq aur Hajat ke liye Istighfar",
            dua = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ الَّذِي لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
            transliteration = "Astaghfirullaha-l-'Azimalladhi la ilaha illa Huwa-l-Hayyu-l-Qayyumu wa atubu ilayh",
            urdu = "Main Azmat wale Allah se maafi mangta hoon jiske siwa koi ma'bud nahi, jo hamesha zinda hai, aur main uski taraf ruju karta hoon.",
            countLabel = "100 baar",
            targetCount = 100,
            whenToRead = "Subah-sham aur har namaz ke baad",
            reference = "Abu Dawood #1517"
        ),
        HajatAzkar(
            id = "hajat_hasbi",
            title = "🛡️ Khauf aur Dushmani se Hifazat",
            dua = "حَسْبِيَ اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            transliteration = "Hasbiyallahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbu-l-'Arshi-l-'Azim",
            urdu = "Mere liye Allah kaafi hai, uske siwa koi ma'bud nahi, usi par maine bharosa kiya aur woh Arsh-e-Azim ka Malik hai.",
            countLabel = "7 baar subah, 7 baar sham",
            targetCount = 7,
            whenToRead = "Subah aur shaam (duniya o aakhirat ke gham se nijat)",
            reference = "Abu Dawood #5081"
        )
    )
}

data class TasbihFatimaStep(
    val stepNumber: Int,
    val arabic: String,
    val title: String,
    val romanUrdu: String,
    val target: Int
)

data class SpecialDhikr(
    val id: String,
    val title: String,
    val subtitleUrdu: String,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val fazilat: String,
    val recommendedCount: Int,
    val reference: String
)

object SpecialDhikrsData {
    val tasbihFatimaSteps: List<TasbihFatimaStep> = listOf(
        TasbihFatimaStep(
            stepNumber = 1,
            arabic = "سُبْحَانَ اللَّهِ",
            title = "SubhanAllah",
            romanUrdu = "Allah har aib se paak hai",
            target = 33
        ),
        TasbihFatimaStep(
            stepNumber = 2,
            arabic = "الْحَمْدُ لِلَّهِ",
            title = "Alhamdulillah",
            romanUrdu = "Tamam tareefein Allah ke liye hain",
            target = 33
        ),
        TasbihFatimaStep(
            stepNumber = 3,
            arabic = "اللَّهُ أَكْبَرُ",
            title = "Allahu Akbar",
            romanUrdu = "Allah sab se bada hai",
            target = 34
        )
    )

    const val TASBIH_FATIMA_TOTAL = 100

    val allSpecialDhikrs: List<SpecialDhikr> = listOf(
        SpecialDhikr(
            id = "special_tamjeed",
            title = "Kalima-e-Tamjeed (Teesra Kalima)",
            subtitleUrdu = "تسبیحِ تمجید",
            arabic = "سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ وَلَا إِلَٰهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ",
            transliteration = "SubhanAllahi wal-hamdulillahi wa la ilaha illAllahu wallahu Akbar",
            meaning = "Allah paak hai, sab tareefein Allah ke liye hain, Allah ke siwa koi ma'bud nahi aur Allah sab se bada hai.",
            fazilat = "Nabi ﷺ ne farmaya: Yeh char kalimaat Allah ke nazdeek sab se zyada mehboob hain aur Jannat ke darakht lagwate hain.",
            recommendedCount = 33,
            reference = "Sahih Muslim #2137"
        ),
        SpecialDhikr(
            id = "special_durood",
            title = "Durood-e-Ibrahimi (Durood Shareef)",
            subtitleUrdu = "درودِ ابراہیمی",
            arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَىٰ إِبْرَاهِيمَ",
            transliteration = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammadin kama sallayta 'ala Ibrahim",
            meaning = "Ae Allah! Muhammad ﷺ aur unki aal par rehmat nazil farma jis tarah Tune Ibrahim (AS) par nazil farmayi.",
            fazilat = "Jumma ke din aur har dua ke aaghaz o aakhir mein parhna qabooliyat-e-dua aur shafa'at ka sabab hai.",
            recommendedCount = 100,
            reference = "Sahih Bukhari #3370"
        ),
        SpecialDhikr(
            id = "special_kalimatayn",
            title = "Meezan Bhaari Karne Wale Kalimaat",
            subtitleUrdu = "تسبیح و تحمید",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "SubhanAllahi wa bihamdihi, SubhanAllahi-l-'Azim",
            meaning = "Allah apni tareef ke sath paak hai, Azmat wala Allah paak hai.",
            fazilat = "Do kalimaat jo zuban par halke, meezan par bhaari aur Rahman ko nihayat mehboob hain.",
            recommendedCount = 100,
            reference = "Sahih Bukhari #6406"
        ),
        SpecialDhikr(
            id = "special_hawqala",
            title = "Jannat ka Khazana (Hawqala)",
            subtitleUrdu = "لا حول ولا قوة إلا بالله",
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            meaning = "Buraayi se bachne aur neki karne ki taqat sirf Allah hi ki taraf se hai.",
            fazilat = "Nabi ﷺ ne farmaya: Yeh Jannat ke khazanon mein se ek khazana hai aur 99 bimariyon ki dawa hai.",
            recommendedCount = 100,
            reference = "Sahih Bukhari #4205"
        ),
        SpecialDhikr(
            id = "special_tawheed",
            title = "Subah-Sham 100x Zikr-e-Tawheed",
            subtitleUrdu = "چوتھا کلمہ توحید",
            arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illAllahu wahdahu la sharika lahu, lahu-l-mulku wa lahu-l-hamdu wa Huwa 'ala kulli shay'in Qadir",
            meaning = "Allah ke siwa koi ma'bud nahi, Woh akela hai, usi ki badshahi aur tareef hai aur Woh har cheez par Qadir hai.",
            fazilat = "Din mein 100 baar parhne wale ko 10 ghulam aazad karne ka sawab, 100 nekiyan aur Shaitan se hifazat milti hai.",
            recommendedCount = 100,
            reference = "Sahih Bukhari #3293"
        ),
        SpecialDhikr(
            id = "special_sayyid_istighfar",
            title = "Istighfar (Taubah & Maghfirat)",
            subtitleUrdu = "استغفار",
            arabic = "أَسْتَغْفِرُ اللَّهَ رَبِّي مِنْ كُلِّ ذَنْبٍ وَأَتُوبُ إِلَيْهِ",
            transliteration = "Astaghfirullaha Rabbi min kulli dhanbin wa atubu ilayh",
            meaning = "Main apne Rab Allah se apne har gunah ki maafi mangta hoon aur Usi ki taraf ruju karta hoon.",
            fazilat = "Kasrat-e-Istighfar se rizq ke darwaze khulte hain, gham door hota hai aur har tangi se nikalne ka rasta milta hai.",
            recommendedCount = 100,
            reference = "Sunan Abi Dawood #1518"
        ),
        SpecialDhikr(
            id = "special_hasbunallah",
            title = "HasbunAllahu wa Ni'mal Wakeel",
            subtitleUrdu = "حسبنا الله ونعم الوكيل",
            arabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "HasbunAllahu wa Ni'mal-Wakeel",
            meaning = "Hamare liye Allah kaafi hai aur Woh behtareen kaarsaaz hai.",
            fazilat = "Hazrat Ibrahim (AS) aur Sahaba-e-Kiram (RA) ne sakht aazmaish ke waqt yahi kalima parha aur Allah ki madad payi.",
            recommendedCount = 100,
            reference = "Surah Aal-e-Imran (3:173)"
        )
    )
}

data class NamazGuideStep(
    val stepNumber: Int,
    val title: String,
    val urduTitle: String,
    val arabicSnippet: String?,
    val instructions: List<String>,
    val note: String? = null
)

data class NamazRakaatSummary(
    val prayerName: String,
    val rakaatDetails: String
)

object NamazGuideData {
    val rakaatSummary: List<NamazRakaatSummary> = listOf(
        NamazRakaatSummary("Fajr", "2 Sunnat + 2 Farz (Total 4)"),
        NamazRakaatSummary("Zuhr", "4 Sunnat + 4 Farz + 2 Sunnat + 2 Nafl (12)"),
        NamazRakaatSummary("Asr", "4 Sunnat + 4 Farz (Total 8)"),
        NamazRakaatSummary("Maghrib", "3 Farz + 2 Sunnat + 2 Nafl (Total 7)"),
        NamazRakaatSummary("Isha", "4 Sunnat + 4 Farz + 2 Sunnat + 2 Nafl + 3 Witr + 2 Nafl")
    )

    val steps: List<NamazGuideStep> = listOf(
        NamazGuideStep(
            stepNumber = 1,
            title = "✅ Step 1 — Wuzu",
            urduTitle = "وضو کا طریقہ",
            arabicSnippet = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            instructions = listOf(
                "Niyat karein aur Bismillah padhein",
                "Dono haath kalaiyon tak 3 baar dhoein",
                "Munh (kulli) aur naak 3-3 baar saaf karein",
                "Poora chehra 3 baar dhoein",
                "Pehle daen phir baen haath kohniyon samet 3-3 baar dhoein",
                "Geelay haathon se sar aur kaanon ka masah karein",
                "Dono paon takhnon samet 3-3 baar dhoein"
            )
        ),
        NamazGuideStep(
            stepNumber = 2,
            title = "✅ Step 2 — Shuruat (Qiyam & Niyat)",
            urduTitle = "قیام اور تکبیرِ تحریمہ",
            arabicSnippet = "اللَّهُ أَكْبَرُ",
            instructions = listOf(
                "Qibla ki taraf munh karein aur dil mein Namaz ki Niyat karein",
                "Dono haath oonche karein aur bolein: 🔊 Allah-o-Akbar",
                "Daen haath baen haath par rakh lein aur nigaah sajde ki jagah par rakhein"
            )
        ),
        NamazGuideStep(
            stepNumber = 3,
            title = "✅ Step 3 — Sana aur Sura Fatiha",
            urduTitle = "ثناء، سورہ فاتحہ اور قراءت",
            arabicSnippet = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ وَتَبَارَكَ اسْمُكَ وَتَعَالَىٰ جَدُّكَ وَلَا إِلَٰهَ غَيْرُكَ",
            instructions = listOf(
                "🤲 Sana: Subhanaka Allahumma wa bihamdika wa tabarakasmuka wa ta'ala jadduka wa la ilaha ghayruk",
                "📖 Ta'awwuz & Tasmiyah: A'udhu billahi minash-shaytanir rajeem • Bismillah hir Rahman nir Raheem",
                "📖 Sura Fatiha (Alhamdulillahi Rabbil 'Alamin...) mukammal padhein",
                "Phir koi chhoti Surah padhein (jaise Surah Ikhlas ya Surah Kawthar)"
            )
        ),
        NamazGuideStep(
            stepNumber = 4,
            title = "✅ Step 4 — Ruku",
            urduTitle = "رکوع",
            arabicSnippet = "سُبْحَانَ رَبِّيَ الْعَظِيمِ",
            instructions = listOf(
                "Allah-o-Akbar keh kar ghutnon par haath rakh kar jhuk jayein",
                "3 baar padhein: Subhana Rabbiyal Azeem",
                "Seedhe khade ho kar bolein: Sami Allahu liman hamidah, phir Rabbana lakal hamd"
            )
        ),
        NamazGuideStep(
            stepNumber = 5,
            title = "✅ Step 5 — Sajda",
            urduTitle = "سجدہ",
            arabicSnippet = "سُبْحَانَ رَبِّيَ الْأَعْلَىٰ",
            instructions = listOf(
                "Allah-o-Akbar keh kar naak aur matha zameen par rakhein",
                "3 baar padhein: Subhana Rabbiyal A'la",
                "Baith kar thodi der rukein (Rabbighfir li), phir dusra Sajda karein",
                "Phir agli Rakaat ke liye seedhe khade ho jayein"
            )
        ),
        NamazGuideStep(
            stepNumber = 6,
            title = "✅ Step 6 — Attahiyat, Durood & Salam",
            urduTitle = "قعدہ، تشہد، درود اور سلام",
            arabicSnippet = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
            instructions = listOf(
                "Baith kar Attahiyat padhein (At-tahiyyatu lillahi was-salawatu wat-tayyibat...)",
                "Aakhri Qa'dah mein Durood-e-Ibrahimi padhein (Allahumma salli 'ala Muhammadin...)",
                "Dua-e-Masoora padhein (Rabbana atina fid-dunya hasanatan...)",
                "Pehle daen phir baen taraf munh kar ke Salam pherein: Assalamu alaikum wa rahmatullah"
            )
        )
    )
}

