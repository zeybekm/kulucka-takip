package com.kulucka.tracker.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kulucka.tracker.data.KuluckaRepository
import com.kulucka.tracker.databinding.ActivityMakineDuzenleBinding

class MakineDuzenleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakineDuzenleBinding
    private lateinit var repository: KuluckaRepository
    private lateinit var makineId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakineDuzenleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makineId = intent.getStringExtra("makine_id") ?: finish().let { return }
        repository = KuluckaRepository(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Makineyi Düzenle"
        loadMakine()
        binding.btnKaydet.setOnClickListener { kaydet() }
    }

    private fun loadMakine() {
        val makine = repository.getMakine(makineId) ?: return
        binding.etMakineIsim.setText(makine.isim)
        binding.etViyolSayisi.setText(makine.viyolSayisi.toString())
        binding.etKapasite.setText(makine.kapasite.toString())
    }

    private fun kaydet() {
        val isim = binding.etMakineIsim.text.toString().trim()
        val viyolSayisiStr = binding.etViyolSayisi.text.toString().trim()
        val kapasiteStr = binding.etKapasite.text.toString().trim()
        if (isim.isEmpty()) { binding.etMakineIsim.error = "Makine ismi zorunludur"; return }
        val makine = repository.getMakine(makineId) ?: return
        val guncellenmis = makine.copy(
            isim = isim,
            viyolSayisi = viyolSayisiStr.toIntOrNull() ?: 0,
            kapasite = kapasiteStr.toIntOrNull() ?: 0
        )
        repository.updateMakine(guncellenmis)
        Toast.makeText(this, "✅ Makine bilgileri güncellendi", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { onBackPressedDispatcher.onBackPressed(); return true }
        return super.onOptionsItemSelected(item)
    }
}
