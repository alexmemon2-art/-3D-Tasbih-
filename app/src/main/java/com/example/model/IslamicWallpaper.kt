package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

data class IslamicWallpaper(
    val id: Int,
    val name: String,
    val arabicName: String,
    @DrawableRes val drawableRes: Int
)

object IslamicWallpapers {
    val list = listOf(
        IslamicWallpaper(
            id = 1,
            name = "Prophet's Mosque, Madinah",
            arabicName = "المسجد النبوي الشريف",
            drawableRes = R.drawable.img_bg_masjid_nabawi
        ),
        IslamicWallpaper(
            id = 2,
            name = "Holy Kaaba at Midnight",
            arabicName = "الكعبة المشرفة",
            drawableRes = R.drawable.img_bg_makkah_night
        ),
        IslamicWallpaper(
            id = 3,
            name = "Grand Marble Colonnade",
            arabicName = "صرح المسجد الكبير",
            drawableRes = R.drawable.img_bg_grand_mosque
        ),
        IslamicWallpaper(
            id = 4,
            name = "Emerald Sanctuary Arches",
            arabicName = "أروقة الزمرد والذهب",
            drawableRes = R.drawable.img_bg_emerald_arches
        ),
        IslamicWallpaper(
            id = 5,
            name = "Golden Dome of the Rock",
            arabicName = "قبة الصخرة المشرفة",
            drawableRes = R.drawable.img_bg_dome_rock
        ),
        IslamicWallpaper(
            id = 6,
            name = "Illuminated Crescent Sanctuary",
            arabicName = "روضة الهلال والنور",
            drawableRes = R.drawable.img_splash_art
        ),
        IslamicWallpaper(
            id = 7,
            name = "Twilight Lantern & Tasbih",
            arabicName = "فانوس الغروب والذكر",
            drawableRes = R.drawable.img_exit_art
        ),
        IslamicWallpaper(
            id = 8,
            name = "Madinah Umbrellas at Dusk",
            arabicName = "مظلات المدينة المنورة",
            drawableRes = R.drawable.img_bg_masjid_nabawi
        ),
        IslamicWallpaper(
            id = 9,
            name = "Holy Sanctuary Reflections",
            arabicName = "أنوار الحرم الشريف",
            drawableRes = R.drawable.img_bg_makkah_night
        ),
        IslamicWallpaper(
            id = 10,
            name = "Royal Golden Mihrab",
            arabicName = "محراب النور والبركة",
            drawableRes = R.drawable.img_bg_emerald_arches
        )
    )
}
