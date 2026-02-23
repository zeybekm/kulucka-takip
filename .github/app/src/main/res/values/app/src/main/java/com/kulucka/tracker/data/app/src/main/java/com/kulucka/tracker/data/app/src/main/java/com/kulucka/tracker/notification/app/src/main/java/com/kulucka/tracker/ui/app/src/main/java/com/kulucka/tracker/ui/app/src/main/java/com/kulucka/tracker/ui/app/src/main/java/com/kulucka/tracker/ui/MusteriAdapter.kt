package com.kulucka.tracker.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kulucka.tracker.data.Musteri
import com.kulucka.tracker.databinding.ItemMusteriBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MusteriAdapter(
    private var musteriler: List<Musteri>,
    private val onEdit: (Musteri) -> Unit,
    private val onDelete: (Musteri) -> Unit
) : RecyclerView.Adapter<MusteriAdapter.MusteriViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    inner class MusteriViewHolder(val binding: ItemMusteriBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusteriViewHolder {
        val binding = ItemMusteriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusteriViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusteriViewHolder, position: Int) {
        val musteri = musteriler[position]
        with(holder.binding) {
            tvMusteriIsim.text = musteri.isim
            tvYumurtaInfo.text = "🥚 ${musteri.yumurtaSayisi} adet ${musteri.yumurtaTuru.displayName}"
            tvGirisDate.text = "📅 Giriş: ${dateFormat.format(Date(musteri.girisDate))}"
            tvCikisDate.text = "📅 Çıkış: ${dateFormat.format(Date(musteri.cikisDate))}"
            val kalanMs = musteri.cikisDate - System.currentTimeMillis()
            val kalanGun = TimeUnit.MILLISECONDS.toDays(kalanMs)
            when {
                kalanMs < 0 -> {
                    tvKalanGun.text = "✅ Tamamlandı"
                    tvKalanGun.setTextColor(Color.parseColor("#4CAF50"))
                    cardMusteri.strokeColor = Color.parseColor("#4CAF50")
                }
                kalanGun <= 2 -> {
                    tvKalanGun.text = "⚠️ $kalanGun gün kaldı!"
                    tvKalanGun.setTextColor(Color.parseColor("#FF5722"))
                    cardMusteri.strokeColor = Color.parseColor("#FF5722")
                }
                else -> {
                    tvKalanGun.text = "⏳ $kalanGun gün kaldı"
                    tvKalanGun.setTextColor(Color.parseColor("#FF6B35"))
                    cardMusteri.strokeColor = Color.parseColor("#E0E0E0")
                }
            }
            if (musteri.telefon.isNotEmpty()) {
                tvTelefon.text = "📞 ${musteri.telefon}"
                tvTelefon.visibility = android.view.View.VISIBLE
            } else {
                tvTelefon.visibility = android.view.View.GONE
            }
            if (musteri.notlar.isNotEmpty()) {
                tvNotlar.text = "📝 ${musteri.notlar}"
                tvNotlar.visibility = android.view.View.VISIBLE
            } else {
                tvNotlar.visibility = android.view.View.GONE
            }
            btnDuzenle.setOnClickListener { onEdit(musteri) }
            btnSil.setOnClickListener { onDelete(musteri) }
        }
    }

    override fun getItemCount() = musteriler.size

    fun updateData(yeniListe: List<Musteri>) {
        musteriler = yeniListe
        notifyDataSetChanged()
    }
}
