package com.example.model

enum class DuaCategory(val label: String) {
    ALL("All Duas"),
    MORNING_EVENING("Subah / Shaam"),
    PRAYER_MASJID("Namaz / Masjid"),
    DAILY_LIFE("Rozmarra"),
    FORGIVENESS_RELIEF("Maghfirat / Shifa")
}

data class MasnoonDua(
    val number: Int,
    val category: DuaCategory,
    val title: String,
    val titleUrdu: String,
    val arabic: String,
    val transliteration: String,
    val romanUrdu: String,
    val englishMeaning: String,
    val reference: String,
    val recommendedCount: Int = 3
)

object MasnoonDuasData {
    val allDuas = listOf(
        MasnoonDua(
            number = 1,
            category = DuaCategory.DAILY_LIFE,
            title = "Dua Before Eating",
            titleUrdu = "کھانے سے پہلے کی دعا",
            arabic = "بِسْمِ اللَّهِ وَعَلَى بَرَكَةِ اللَّهِ",
            transliteration = "Bismillahi wa 'ala barakatillah",
            romanUrdu = "Allah ke naam se aur Allah ki barkat par shuru karta hoon.",
            englishMeaning = "In the name of Allah and upon the blessings of Allah.",
            reference = "Al-Mustadrak / Sunan Abi Dawud",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 2,
            category = DuaCategory.DAILY_LIFE,
            title = "Dua After Eating",
            titleUrdu = "کھانے کے بعد کی دعا",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
            transliteration = "Alhamdulillahil-ladhi at'amana wa saqana wa ja'alana Muslimin",
            romanUrdu = "Tamam tareefein Allah ke liye hain jis ne humein khilaya, pilaya aur Musalman banaya.",
            englishMeaning = "All praise is to Allah who fed us, gave us drink, and made us Muslims.",
            reference = "Sunan Abi Dawud 3850",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 3,
            category = DuaCategory.DAILY_LIFE,
            title = "Dua Upon Waking Up",
            titleUrdu = "نیند سے بیدار ہونے کی دعا",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Alhamdulillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur",
            romanUrdu = "Tamam tareefein Allah ke liye hain jis ne humein marne (neend) ke baad zinda kiya aur Usi ki taraf lautna hai.",
            englishMeaning = "All praise is to Allah who gave us life after causing us to die, and to Him is the resurrection.",
            reference = "Sahih al-Bukhari 6312",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 4,
            category = DuaCategory.DAILY_LIFE,
            title = "Dua Before Sleeping",
            titleUrdu = "سونے سے پہلے کی دعا",
            arabic = "اللَّهُمَّ بِاسْمِكَ أَمُوتُ وَأَحْيَا",
            transliteration = "Allahumma bismika amutu wa ahya",
            romanUrdu = "Ae Allah! Tere hi naam se main marta (sota) hoon aur zinda hota (jaagta) hoon.",
            englishMeaning = "O Allah, in Your name I die and I live.",
            reference = "Sahih al-Bukhari 6324",
            recommendedCount = 3
        ),
        MasnoonDua(
            number = 5,
            category = DuaCategory.DAILY_LIFE,
            title = "Dua When Leaving Home",
            titleUrdu = "گھر سے نکلنے کی دعا",
            arabic = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Bismillahi tawakkaltu 'alallahi, wa la hawla wa la quwwata illa billah",
            romanUrdu = "Allah ke naam se, main ne Allah par bharosa kiya, aur gunah se bachne aur neki karne ki taqat Allah hi se hai.",
            englishMeaning = "In the name of Allah, I place my trust in Allah; there is no might nor power except through Allah.",
            reference = "Sunan Abi Dawud 5095",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 6,
            category = DuaCategory.DAILY_LIFE,
            title = "Dua for Traveling (Safar)",
            titleUrdu = "سفر کی دعا",
            arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ",
            transliteration = "Subhanalladhi sakh-khara lana hadha wa ma kunna lahu muqrinin wa inna ila Rabbina lamunqalibun",
            romanUrdu = "Paak hai woh Zaat jis ne is sawari ko hamare qabu mein kar diya aur hum ise qabu karne wale na the, aur beshak humein apne Rab ke paas lautna hai.",
            englishMeaning = "Glory to Him who subjected this to us, though we could never have controlled it; surely to our Lord we shall return.",
            reference = "Sahih Muslim 1342",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 7,
            category = DuaCategory.PRAYER_MASJID,
            title = "Entering the Masjid",
            titleUrdu = "مسجد میں داخل ہونے کی دعا",
            arabic = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            transliteration = "Allahumma-ftah li abwaba rahmatik",
            romanUrdu = "Ae Allah! Mere liye apni rahmat ke darwaze khol de.",
            englishMeaning = "O Allah, open for me the doors of Your mercy.",
            reference = "Sahih Muslim 713",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 8,
            category = DuaCategory.PRAYER_MASJID,
            title = "Leaving the Masjid",
            titleUrdu = "مسجد سے نکلنے کی دعا",
            arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ",
            transliteration = "Allahumma inni as'aluka min fadlik",
            romanUrdu = "Ae Allah! Beshak main Tujh se Tera fazl maangta hoon.",
            englishMeaning = "O Allah, indeed I ask You of Your bounty.",
            reference = "Sahih Muslim 713",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 9,
            category = DuaCategory.PRAYER_MASJID,
            title = "After Wudu (Ablution)",
            titleUrdu = "وضو کے بعد کی دعا",
            arabic = "أَشْهَدُ أَنْ لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            transliteration = "Ash-hadu an la ilaha illallahu wahdahu la sharika lahu wa ash-hadu anna Muhammadan 'abduhu wa rasuluh",
            romanUrdu = "Main gawahi deta hoon ke Allah ke siwa koi mabood nahi, woh akela hai, aur Muhammad ﷺ uske bande aur rasool hain.",
            englishMeaning = "I bear witness there is no deity except Allah alone, and Muhammad ﷺ is His servant and Messenger.",
            reference = "Sahih Muslim 234",
            recommendedCount = 1
        ),
        MasnoonDua(
            number = 10,
            category = DuaCategory.MORNING_EVENING,
            title = "Protection (Morning & Evening)",
            titleUrdu = "صبح و شام حفاظت کی دعا",
            arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim",
            romanUrdu = "Allah ke naam se jis ke naam ki barkat se zameen o aasman ki koi cheez nuqsan nahi pahuncha sakti.",
            englishMeaning = "In the name of Allah with whose name nothing in earth or heaven can harm, and He is the All-Hearing, All-Knowing.",
            reference = "Sunan Abi Dawud 5088 (3x Morning & Evening)",
            recommendedCount = 3
        ),
        MasnoonDua(
            number = 11,
            category = DuaCategory.MORNING_EVENING,
            title = "Contentment in Islam (3x)",
            titleUrdu = "ایمان پر راضی رہنے کی دعا",
            arabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ ﷺ نَبِيًّا",
            transliteration = "Raditu billahi Rabban, wa bil-Islami dinan, wa bi-Muhammadin nabiyya",
            romanUrdu = "Main Allah ke Rab hone par, Islam ke deen hone par aur Muhammad ﷺ ke Nabi hone par raazi hoon.",
            englishMeaning = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad ﷺ as my Prophet.",
            reference = "Sunan Abi Dawud 5072 (3x Morning & Evening)",
            recommendedCount = 3
        ),
        MasnoonDua(
            number = 12,
            category = DuaCategory.MORNING_EVENING,
            title = "Sayyidul Istighfar",
            titleUrdu = "سید الاستغفار",
            arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Anta khalaqtani wa ana 'abduka...",
            romanUrdu = "Ae Allah! Tu mera Rab hai, Tere siwa koi mabood nahi, Tu ne mujhe paida kiya aur main Tera banda hoon — mere gunah maaf farma.",
            englishMeaning = "O Allah, You are my Lord, none has the right to be worshipped except You. You created me and I am Your servant; forgive me.",
            reference = "Sahih al-Bukhari 6306",
            recommendedCount = 3
        ),
        MasnoonDua(
            number = 13,
            category = DuaCategory.FORGIVENESS_RELIEF,
            title = "Dua of Prophet Yunus (AS)",
            titleUrdu = "مصیبت و پریشانی دور کرنے کی دعا",
            arabic = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
            transliteration = "La ilaha illa Anta subhanaka inni kuntu minaz-zalimin",
            romanUrdu = "Tere siwa koi mabood nahi, Tu paak hai, beshak main hi gunahgaron mein se tha.",
            englishMeaning = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            reference = "Quran 21:87 / Tirmidhi 3505",
            recommendedCount = 33
        ),
        MasnoonDua(
            number = 14,
            category = DuaCategory.FORGIVENESS_RELIEF,
            title = "Rabbana Atina (Best of Both Worlds)",
            titleUrdu = "دنیا و آخرت کی بھلائی کی دعا",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar",
            romanUrdu = "Ae hamare Rab! Humein duniya mein bhi bhalai de aur aakhirat mein bhi bhalai de aur aag ke azaab se bacha.",
            englishMeaning = "Our Lord, grant us good in this world and good in the Hereafter, and protect us from the punishment of the Fire.",
            reference = "Quran 2:201 / Sahih al-Bukhari 6389",
            recommendedCount = 33
        ),
        MasnoonDua(
            number = 15,
            category = DuaCategory.FORGIVENESS_RELIEF,
            title = "Dua for Parents",
            titleUrdu = "والدین کے لیے دعا",
            arabic = "رَبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-hamhuma kama rabbayani saghira",
            romanUrdu = "Ae mere Rab! Un dono (mere walidain) par reham farma jaisa unhon ne mujhe bachpan mein pala.",
            englishMeaning = "My Lord, have mercy upon them as they brought me up when I was small.",
            reference = "Quran 17:24",
            recommendedCount = 33
        ),
        MasnoonDua(
            number = 16,
            category = DuaCategory.DAILY_LIFE,
            title = "Dua for Increase in Knowledge",
            titleUrdu = "علم میں اضافے کی دعا",
            arabic = "رَبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidni 'ilma",
            romanUrdu = "Ae mere Rab! Mere ilm mein izafa farma.",
            englishMeaning = "My Lord, increase me in knowledge.",
            reference = "Quran 20:114",
            recommendedCount = 33
        ),
        MasnoonDua(
            number = 17,
            category = DuaCategory.FORGIVENESS_RELIEF,
            title = "Dua for Healing (Shifa)",
            titleUrdu = "بیماری سے شفا کی دعا",
            arabic = "أَذْهِبِ الْبَأْسَ رَبَّ النَّاسِ، وَاشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ",
            transliteration = "Adh-hibil-ba'sa Rabban-nas, washfi Antash-Shafi, la shifa'a illa shifa'uk",
            romanUrdu = "Ae logon ke Rab! Takleef door farma aur shifa de, Tu hi shifa dene wala hai.",
            englishMeaning = "Remove the harm, O Lord of mankind, and heal, for You are the Healer; there is no cure except Your cure.",
            reference = "Sahih al-Bukhari 5675",
            recommendedCount = 7
        ),
        MasnoonDua(
            number = 18,
            category = DuaCategory.PRAYER_MASJID,
            title = "Dua After Prayer (Allahumma A'inni)",
            titleUrdu = "نماز کے بعد ذکر و شکر کی دعا",
            arabic = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ",
            transliteration = "Allahumma a'inni 'ala dhikrika wa shukrika wa husni 'ibadatik",
            romanUrdu = "Ae Allah! Apne zikr, apne shukr aur apni behtareen ibadat par meri madad farma.",
            englishMeaning = "O Allah, help me to remember You, to give thanks to You, and to worship You in the best manner.",
            reference = "Sunan Abi Dawud 1522",
            recommendedCount = 3
        ),
        MasnoonDua(
            number = 19,
            category = DuaCategory.MORNING_EVENING,
            title = "Dua for Beneficial Knowledge & Halal Rizq",
            titleUrdu = "علم نافع اور رزقِ حلال کی دعا",
            arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
            transliteration = "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbalan",
            romanUrdu = "Ae Allah! Main Tujh se nafa dene wala ilm, pakiza rizq aur maqbool amal maangta hoon.",
            englishMeaning = "O Allah, I ask You for beneficial knowledge, goodly provision, and acceptable deeds.",
            reference = "Sunan Ibn Majah 925 (After Fajr)",
            recommendedCount = 3
        ),
        MasnoonDua(
            number = 20,
            category = DuaCategory.FORGIVENESS_RELIEF,
            title = "Laylat al-Qadr / Forgiveness Dua",
            titleUrdu = "معافی اور بخشش کی جامع دعا",
            arabic = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            transliteration = "Allahumma innaka 'Afuwwun tuhibbul-'afwa fa'fu 'anni",
            romanUrdu = "Ae Allah! Beshak Tu maaf karne wala hai aur maafi ko pasand karta hai, pas mujhe maaf farma de.",
            englishMeaning = "O Allah, You are Pardoning and love to pardon, so pardon me.",
            reference = "Jami' at-Tirmidhi 3513",
            recommendedCount = 100
        )
    )
}
