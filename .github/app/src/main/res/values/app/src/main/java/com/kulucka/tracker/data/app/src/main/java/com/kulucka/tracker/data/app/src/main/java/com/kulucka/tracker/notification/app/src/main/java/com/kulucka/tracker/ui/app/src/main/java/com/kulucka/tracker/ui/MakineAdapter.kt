package com.kulucka.tracker.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kulucka.tracker.data.KuluckaMakinesi
import com.kulucka.tracker.databinding.ItemMakineBinding

class MakineAdapter(
    private var makineler: List<KuluckaMakinesi>,
    private val onClick: (KuluckaMakinesi) -> Unit
) : RecyclerView.Adapter<MakineAdapter.MakineViewHolder>() {

    inner class MakineViewHolder(val binding: ItemMakineBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MakineViewHolder {
        val binding = ItemMakineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MakineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MakineViewHolder, position: Int) {
        val makine = makineler[position]
        with(holder.binding) {
            tvMakineIsim.text = makine.isim
            tvViyolSayisi.text = "Viyol: ${makine.viyolSayisi}"
            tvMusteriSayisi.text = "👤 ${makine.aktifMusteriSayisi} müşteri"
            tvYumurtaSayisi.text = "🥚 ${makine.toplamYumurtaSayisi} yumurta"
            try {
                cardMakine.setCardBackgroundColor(Color.parseColor(makine.renk))
            } catch (e: Exception) {
                cardMakine.setCardBackgroundColor(Color.parseColor("#FF6B35"))
            }
            val dolulukYuzde = if (makine.kapasite > 0)
                (makine.toplamYumurtaSayisi * 100 / makine.kapasite).coerceIn(0, 100)
            else 0
            progressDoluluk.progress = dolulukYuzde
            tvDoluluk.text = "$dolulukYuzde%"
            val yaklasanCikis = makine.musteriListesi.filter { musteri ->
                musteri.aktif &&
                musteri.cikisDate - System.currentTimeMillis() <= 2 * 24 * 60 * 60 * 1000L &&
                musteri.cikisDate > System.currentTimeMillis()
            }
            if (yaklasanCikis.isNotEmpty()) {
                tvUyari.text = "⚠️ ${yaklasanCikis.size} çıkış yaklaşıyor!"
                tvUyari.visibility = android.view.View.VISIBLE
            } else {
                tvUyari.visibility = android.view.View.GONE
            }
            root.setOnClickListener { onClick(makine) }
        }
    }

    override fun getItemCount() = makineler.size

    fun updateData(yeniListe: List<KuluckaMakinesi>) {
        makineler = yeniListe
        notifyDataSetChanged()
    }
}
