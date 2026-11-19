package com.example.model

data class ShifaDua(
    val id: Int,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val urdu: String,
    val reference: String,
    val count: String,
    val targetCount: Int = 7
)

object ShifaDuasData {
    val allDuas: List<ShifaDua> = listOf(
        ShifaDua(
            id = 1,
            title = "🩺 ہر بیماری سے شفا کی دعا",
            arabic = "اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَأْسَ، وَاشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            transliteration = "Allāhumma Rabban-nās, adhhibil-ba's, wa-shfi Antash-Shāfī, lā shifā'a illā shifā'uka, shifā'an lā yughādiru saqaman",
            urdu = "اے لوگوں کے رب! تکلیف دور فرما دے اور شفا عطا فرما، تو ہی شفا دینے والا ہے۔ تیری شفا کے سوا کوئی شفا نہیں — ایسی شفا جو کوئی بیماری باقی نہ چھوڑے۔",
            count = "3 یا 7 بار پڑھیں",
            reference = "بخاری 5675، مسلم 2191",
            targetCount = 7
        ),
        ShifaDua(
            id = 2,
            title = "💪 جسم کے درد کی دعا",
            arabic = "أَعُوذُ بِاللّٰهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ",
            transliteration = "A'udhu billāhi wa qudratihi min sharri mā ajidu wa uhadhir",
            urdu = "میں اللہ کی اور اس کی قدرت کی پناہ مانگتا ہوں اس تکلیف کے شر سے جو میں محسوس کر رہا ہوں اور جس کا مجھے اندیشہ ہے۔",
            count = "7 بار پڑھیں",
            reference = "مسلم 2202",
            targetCount = 7
        ),
        ShifaDua(
            id = 3,
            title = "😔 سخت بیماری میں — حضرت ایوبؑ کی دعا",
            arabic = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنْتَ أَرْحَمُ الرَّاحِمِينَ",
            transliteration = "Annī massaniyad-durru wa Anta Arhamur-Rāhimīn",
            urdu = "بے شک مجھے تکلیف پہنچی ہے اور تو سب سے زیادہ رحم کرنے والا ہے۔",
            count = "بار بار پڑھیں",
            reference = "سورہ الانبیاء: 83",
            targetCount = 33
        ),
        ShifaDua(
            id = 4,
            title = "❤️ کسی دوسرے کی شفا کے لیے دعا",
            arabic = "أَسْأَلُ اللَّهَ الْعَظِيمَ رَبَّ الْعَرْشِ الْعَظِيمِ أَنْ يَشْفِيَكَ",
            transliteration = "As'alullāhal-'Azīma Rabbal-'Arshil-'Azīmi an yashfiyak",
            urdu = "میں اللہ عظیم سے جو عرشِ عظیم کا رب ہے درخواست کرتا ہوں کہ وہ تمہیں شفا دے۔",
            count = "7 بار پڑھیں",
            reference = "ترمذی 2083، ابوداؤد 3106",
            targetCount = 7
        )
    )
}
