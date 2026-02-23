package com.kulucka.tracker.data

import java.io.Serializable
import java.util.Date
import java.util.UUID

enum class YumurtaTuru(val displayName: String, val kuluckaSuresi: Int) {
    TAVUK("Tavuk", 21),
    HINDI("Hindi", 28),
    ORDEK("Ördek", 28),
    KAZ("Kaz", 30),
    BILDIRCIN("Bıldırcın", 17),
    DEVEKUSU("Devekuşu", 42),
    BEYA_HINDISI("Beyaz Hindi", 28),
    GUINEA("Beç Tavuğu", 26);

    companion object {
        fun fromName(name: String): YumurtaTuru? = values().find { it.name == name }
    }
}

data class Musteri(
    val id: String = UUID.randomUUID().toString(),
    val isim: String,
    val telefon: String = "",
    val yumurtaSayisi: Int,
    val yumurtaTuru: YumurtaTuru,
    val girisDate: Long = System.currentTimeMillis(),
    val cikisDate: Long = girisDate + (yumurtaTuru.kuluckaSuresi * 24 * 60 * 60 * 1000L),
    val makineId: String,
    val notlar: String = "",
    val aktif: Boolean = true
) : Serializable

data class KuluckaMakinesi(
    val id: String = UUID.randomUUID().toString(),
    val isim: String = "Makine",
    val viyolSayisi: Int = 0,
    val kapasite: Int = 0,
    val musteriListesi: List<Musteri> = emptyList(),
    val renk: String = "#FF6B35"
) : Serializable {
    val aktifMusteriSayisi: Int get() = musteriListesi.count { it.aktif }
    val toplamYumurtaSayisi: Int get() = musteriListesi.filter { it.aktif }.sumOf { it.yumurtaSayisi }
}
