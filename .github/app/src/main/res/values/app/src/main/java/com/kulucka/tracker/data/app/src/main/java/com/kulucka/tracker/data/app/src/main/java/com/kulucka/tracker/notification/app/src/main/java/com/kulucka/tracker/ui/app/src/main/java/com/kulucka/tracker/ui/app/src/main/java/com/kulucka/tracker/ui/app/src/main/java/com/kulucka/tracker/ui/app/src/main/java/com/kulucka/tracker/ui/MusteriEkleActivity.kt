package com.kulucka.tracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kulucka.tracker.data.KuluckaRepository
import com.kulucka.tracker.data.Musteri
import com.kulucka.tracker.data.YumurtaTuru
import com.kulucka.tracker.databinding.ActivityMusteriEkleBinding
import com.kulucka.tracker.notification.NotificationScheduler
import java.text.SimpleDateFormat
import java.util.*

class MusteriEkleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusteriEkleBinding
    private lateinit var repository: KuluckaRepository
    private lateinit var makineId: String
    private var musteriId: String? = null
    private var mevcutMusteri: Musteri? = null
    private var secilenGirisDate: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusteriEkleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makineId = intent.getStringExtra("makine_id") ?: finish().let { return }
        musteriId = intent.getStringExtra("musteri_id")
        repository = KuluckaRepository(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupYumurtaTuruSpinner()
        setupDatePicker()
        musteriId?.let { id ->
            val makine = repository.getMakine(makineId)
            mevcutMusteri = makine?.musteriListesi?.find { it.id == id }
            mevcutMusteri?.let { fillForm(it) }
            supportActionBar?.title = "Müşteriyi Düzenle"
        } ?: run {
            supportActionBar?.title = "Yeni Müşteri Ekle"
        }
        binding.btnKaydet.setOnClickListener { kaydet() }
    }

    private fun setupYumurtaTuruSpinner() {
        val turler = YumurtaTuru.values().map { "${it.displayName} (${it.kuluckaSuresi} gün)" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, turler)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYumurtaTuru.adapter = adapter
    }

    private fun setupDatePicker() {
        updateDateText()
        binding.btnGirisDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = secilenGirisDate }
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                secilenGirisDate = cal.timeInMillis
                updateDateText()
                updateCikisDate()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateDateText() {
        binding.tvGirisDate.text = "Giriş: ${dateFormat.format(Date(secilenGirisDate))}"
    }

    private fun updateCikisDate() {
        val secilenTur = YumurtaTuru.values()[binding.spinnerYumurtaTuru.selectedItemPosition]
        val cikis = secilenGirisDate + (secilenTur.kuluckaSuresi * 24 * 60 * 60 * 1000L)
        binding.tvCikisDate.text = "Tahmini Çıkış: ${dateFormat.format(Date(cikis))}"
    }

    private fun fillForm(musteri: Musteri) {
        binding.etIsim.setText(musteri.isim)
        binding.etTelefon.setText(musteri.telefon)
        binding.etYumurtaSayisi.setText(musteri.yumurtaSayisi.toString())
        binding.etNotlar.setText(musteri.notlar)
        binding.spinnerYumurtaTuru.setSelection(musteri.yumurtaTuru.ordinal)
        secilenGirisDate = musteri.girisDate
        updateDateText()
        updateCikisDate()
    }

    private fun kaydet() {
        val isim = binding.etIsim.text.toString().trim()
        val telefon = binding.etTelefon.text.toString().trim()
        val yumurtaSayisiStr = binding.etYumurtaSayisi.text.toString().trim()
        val notlar = binding.etNotlar.text.toString().trim()
        if (isim.isEmpty()) { binding.etIsim.error = "İsim zorunludur"; return }
        if (yumurtaSayisiStr.isEmpty()) { binding.etYumurtaSayisi.error = "Yumurta sayısı zorunludur"; return }
        val yumurtaSayisi = yumurtaSayisiStr.toIntOrNull() ?: run {
            binding.etYumur
