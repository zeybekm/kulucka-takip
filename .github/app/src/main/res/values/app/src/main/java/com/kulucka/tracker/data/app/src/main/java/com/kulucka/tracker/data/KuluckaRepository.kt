package com.kulucka.tracker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class KuluckaRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("kulucka_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_MAKINELER = "makineler"
        private const val DEFAULT_MAKINE_COUNT = 4
    }

    fun getMakineler(): List<KuluckaMakinesi> {
        val json = prefs.getString(KEY_MAKINELER, null)
        return if (json != null) {
            val type = object : TypeToken<List<KuluckaMakinesi>>() {}.type
            gson.fromJson(json, type) ?: getDefaultMakineler()
        } else {
            getDefaultMakineler()
        }
    }

    private fun getDefaultMakineler(): List<KuluckaMakinesi> {
        val renkler = listOf("#FF6B35", "#4CAF50", "#2196F3", "#9C27B0")
        return (1..DEFAULT_MAKINE_COUNT).map { i ->
            KuluckaMakinesi(
                id = "makine_$i",
                isim = "Makine $i",
                viyolSayisi = 0,
                kapasite = 0,
                renk = renkler[i - 1]
            )
        }
    }

    fun saveMakineler(makineler: List<KuluckaMakinesi>) {
        prefs.edit().putString(KEY_MAKINELER, gson.toJson(makineler)).apply()
    }

    fun getMakine(makineId: String): KuluckaMakinesi? {
        return getMakineler().find { it.id == makineId }
    }

    fun updateMakine(makine: KuluckaMakinesi) {
        val makineler = getMakineler().toMutableList()
        val index = makineler.indexOfFirst { it.id == makine.id }
        if (index >= 0) {
            makineler[index] = makine
            saveMakineler(makineler)
        }
    }

    fun addMusteri(makineId: String, musteri: Musteri) {
        val makineler = getMakineler().toMutableList()
        val index = makineler.indexOfFirst { it.id == makineId }
        if (index >= 0) {
            val makine = makineler[index]
            val yeniListe = makine.musteriListesi.toMutableList()
            yeniListe.add(musteri)
            makineler[index] = makine.copy(musteriListesi = yeniListe)
            saveMakineler(makineler)
        }
    }

    fun updateMusteri(makineId: String, musteri: Musteri) {
        val makineler = getMakineler().toMutableList()
        val makineIndex = makineler.indexOfFirst { it.id == makineId }
        if (makineIndex >= 0) {
            val makine = makineler[makineIndex]
            val musteriListe = makine.musteriListesi.toMutableList()
            val musteriIndex = musteriListe.indexOfFirst { it.id == musteri.id }
            if (musteriIndex >= 0) {
                musteriListe[musteriIndex] = musteri
                makineler[makineIndex] = makine.copy(musteriListesi = musteriListe)
                saveMakineler(makineler)
            }
        }
    }

    fun removeMusteri(makineId: String, musteriId: String) {
        val makineler = getMakineler().toMutableList()
        val makineIndex = makineler.indexOfFirst { it.id == makineId }
        if (makineIndex >= 0) {
            val makine = makineler[makineIndex]
            val yeniListe = makine.musteriListesi.filter { it.id != musteriId }
            makineler[makineIndex] = makine.copy(musteriListesi = yeniListe)
            saveMakineler(makineler)
        }
    }

    fun getTumAktifMusteriler(): List<Pair<KuluckaMakinesi, Musteri>> {
        return getMakineler().flatMap { makine ->
            makine.musteriListesi.filter { it.aktif }.map { Pair(makine, it) }
        }
    }
}
