package com.kulucka.tracker.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kulucka.tracker.R
import com.kulucka.tracker.data.KuluckaRepository
import com.kulucka.tracker.data.KuluckaMakinesi
import com.kulucka.tracker.data.Musteri
import com.kulucka.tracker.databinding.ActivityMakineDetailBinding
import com.kulucka.tracker.notification.NotificationScheduler

class MakineDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakineDetailBinding
    private lateinit var repository: KuluckaRepository
    private lateinit var makineId: String
    private lateinit var makine: KuluckaMakinesi
    private lateinit var musteriAdapter: MusteriAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makineId = intent.getStringExtra("makine_id") ?: finish().let { return }
        repository = KuluckaRepository(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()
        binding.fabEkle.setOnClickListener {
            val intent = Intent(this, MusteriEkleActivity::class.java)
            intent.putExtra("makine_id", makineId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        makine = repository.getMakine(makineId) ?: run { finish(); return }
        supportActionBar?.title = makine.isim
        val aktifMusteriler = makine.musteriListesi.filter { it.aktif }
        musteriAdapter.updateData(aktifMusteriler)
        binding.tvBosMesaj.visibility = if (aktifMusteriler.isEmpty()) View.VISIBLE else View.GONE
        binding.tvViyolInfo.text = "Viyol: ${makine.viyolSayisi}"
        binding.tvKapasiteInfo.text = "Kapasite: ${makine.kapasite}"
        binding.tvDoluInfo.text = "Dolu: ${makine.toplamYumurtaSayisi}"
    }

    private fun setupRecyclerView() {
        musteriAdapter = MusteriAdapter(
            emptyList(),
            onEdit = { musteri ->
                val intent = Intent(this, MusteriEkleActivity::class.java)
                intent.putExtra("makine_id", makineId)
                intent.putExtra("musteri_id", musteri.id)
                startActivity(intent)
            },
            onDelete = { musteri -> confirmDelete(musteri) }
        )
        binding.rvMusteriler.apply {
            layoutManager = LinearLayoutManager(this@MakineDetailActivity)
            adapter = musteriAdapter
        }
    }

    private fun confirmDelete(musteri: Musteri) {
        AlertDialog.Builder(this)
            .setTitle("Müşteriyi Sil")
            .setMessage("${musteri.isim} müşterisini silmek istediğinize emin misiniz?")
            .setPositiveButton("Sil") { _, _ ->
                NotificationScheduler.cancelNotification(this, musteri.id)
                repository.removeMusteri(makineId, musteri.id)
                loadData()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.makine_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { onBackPressedDispatcher.onBackPressed(); true }
            R.id.action_edit_makine -> {
                val intent = Intent(this, MakineDuzenleActivity::class.java)
                intent.putExtra("makine_id", makineId)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
