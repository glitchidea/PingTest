package com.pingtest.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.pingtest.app.ui.AppState

/**
 * Testler ekran kapalıyken / uygulama arka plandayken de sürsün diye
 * ön plan (foreground) servisi. Bildirimde ilerleme gösterir; iş bitince
 * kendi kendini kapatır. Uygulama zorla kapatılırsa Android süreci
 * sonlandırdığı için test de durur.
 */
class TestService : Service() {

    companion object {
        private const val CHANNEL_ID = "pingtest_run"
        private const val NOTIF_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, TestService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private lateinit var state: AppState

    override fun onCreate() {
        super.onCreate()
        state = AppState.get(this)
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 5 saniye içinde ön plana geçmek zorunlu
        startForeground(NOTIF_ID, buildNotification(0, 0))

        state.onProgress = { done, total -> updateNotification(done, total) }
        state.onFinished = {
            state.onProgress = null
            state.onFinished = null
            stopSelfSafely()
        }

        if (state.running) {
            updateNotification(state.done, state.total)
        } else {
            val pending = state.consumePendingRun()
            if (pending != null) state.startRun(pending) else state.runVisible()
            if (!state.running) {
                // Test edilecek hedef yoksa hemen kapan
                stopSelfSafely()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        state.onProgress = null
        state.onFinished = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopSelfSafely() {
        try {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        } catch (_: Exception) {
        }
        stopSelf()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val s = state.strings
            val channel = NotificationChannel(
                CHANNEL_ID,
                s.channelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = s.channelDesc }
            val nm = getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(done: Int, total: Int): Notification {
        val s = state.strings
        val open = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val text = if (total > 0) s.notifProgress(done, total) else s.notifStarting
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(s.notifTitle)
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(open)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        if (total > 0) {
            builder.setProgress(total, done, false)
        } else {
            builder.setProgress(0, 0, true)
        }
        return builder.build()
    }

    private fun updateNotification(done: Int, total: Int) {
        try {
            val nm = getSystemService(NotificationManager::class.java)
            nm?.notify(NOTIF_ID, buildNotification(done, total))
        } catch (_: Exception) {
            // Bildirim izni yoksa sessizce geç; servis yine çalışır.
        }
    }
}
