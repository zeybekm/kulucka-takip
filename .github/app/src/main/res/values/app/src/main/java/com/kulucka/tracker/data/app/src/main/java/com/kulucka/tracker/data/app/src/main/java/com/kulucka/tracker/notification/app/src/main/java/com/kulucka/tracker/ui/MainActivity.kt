package com.kulucka.tracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.kulucka.tracker.R
import com.kulucka.tracker.data.KuluckaRepository
import com.kulucka.tracker.data.KuluckaMakinesi
import com.kulucka.tracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: KuluckaRepository
    private lateinit var makineAdapter: MakineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "🐣 Kuluçka Takip"
        repository = KuluckaRepository(this)
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadMakineler()
    }

    private fun setupRecyclerView() {
        makineAdapter = MakineAdapter(emptyList()) { makine ->
            val intent = Intent(this, MakineDetailActivity::class.java)
            intent.putExtra("makine_id", makine.id)
            startActivity(intent)
        }
        binding.rvMakineler.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = makineAdapter
        }
    }

    private fun loadMakineler() {
        val makineler = repository.getMakineler()
        makineAdapter.updateData(makineler)
        val toplamMusteri = makineler.sumOf { it.aktifMusteriSayisi }
        val toplamYumurta = makineler.sumOf { it.toplamYumurtaSayisi }
        binding.tvToplamMusteri.text = "👤 $toplamMusteri Müşteri"
        binding.tvToplamYumurta.text = "🥚 $toplamYumurta Yumurta"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
