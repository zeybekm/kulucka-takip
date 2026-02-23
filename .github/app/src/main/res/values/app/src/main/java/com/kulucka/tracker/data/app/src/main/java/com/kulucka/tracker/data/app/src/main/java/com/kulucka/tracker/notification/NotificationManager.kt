package com.kulucka.tracker.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kulucka.tracker.data.KuluckaRepository
import com.kulucka.tracker.data.Musteri
import com.kulucka.tracker.ui.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val makineIsim = intent.getStringExtra("makine_isim") ?: return
        val musteriIsim = intent.getStringExtra("musteri_isim") ?: return
        val yumurtaSayisi = intent.getIntExtra("yumurta_sayisi", 0)
        val yumurtaTuru = intent.getStringExtra("yumurta_turu") ?: return
        val musteriId = intent.getStringExtra("musteri_id") ?: return
        showNotification(context, makineIsim, musteriIsim, yumurtaSayisi, yumurtaTuru, musteriId)
    }

    private fun showNotification(context: Context, makineIsim: String, musteriIsim: String,
        yumurtaSayisi: Int, yumurtaTuru: String, musteriId: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Kuluçka Bildirimleri",
                NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🐣 Çıkış Zamanı Yaklaşıyor!")
            .setContentText("$makineIsim | $musteriIsim - $yumurtaSayisi adet $yumurtaTuru")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("⚠️ 2 gün sonra çıkış yapılacak!\n\n📍 Makine: $makineIsim\n👤 Müşteri: $musteriIsim\n🥚 $yumurtaSayisi adet $yumurtaTuru"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(musteriId.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "kulucka_notifications"
    }
}

object NotificationScheduler {
    fun scheduleNotification(context: Context, makineIsim: String, musteri: Musteri) {
        cancelNotification(context, musteri.id)
        val notificationTime = musteri.cikisDate - (2 * 24 * 60 * 60 * 1000L)
        if (notificationTime <= System.currentTimeMillis()) return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("makine_isim", makineIsim)
            putExtra("musteri_isim", musteri.isim)
            putExtra("yumurta_sayisi", musteri.yumurtaSayisi)
            putExtra("yumurta_turu", musteri.yumurtaTuru.displayName)
            putExtra("musteri_id", musteri.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, musteri.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
        }
    }

    fun cancelNotification(context: Context, musteriId: String) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, musteriId.hashCode(), intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        pendingIntent?.let {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
        }
    }

    fun rescheduleAllNotifications(context: Context) {
        val repo = Kuluck
