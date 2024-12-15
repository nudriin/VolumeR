package com.nudriin.volumer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import com.nudriin.volumer.databinding.OverlayLayoutBinding
import kotlin.math.abs

class VolumeOverlayService : Service() {
    private lateinit var binding: OverlayLayoutBinding

    private lateinit var windowManager: WindowManager
    private var overlayMenuView: View? = null
    private var floatingButtonView: View? = null
    private lateinit var menuParams: WindowManager.LayoutParams
    private lateinit var floatingParams: WindowManager.LayoutParams
    private var isMenuVisible = false
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "VolumeOverlayChannel"
    }

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ClickableViewAccessibility", "ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Create Notification Channel and start Foreground Service
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        setupFloatingButton()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFloatingButton() {
        floatingButtonView =
            LayoutInflater.from(this).inflate(R.layout.overlay_floating_button, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            floatingParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
        floatingParams.gravity = Gravity.TOP or Gravity.START
        floatingParams.x = 50
        floatingParams.y = 100

        windowManager.addView(floatingButtonView, floatingParams)

        floatingButtonView?.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Simpan posisi awal dan koordinat sentuhan
                    initialX = floatingParams.x
                    initialY = floatingParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_MOVE -> {
                    // Hitung perubahan posisi
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()

                    // Update posisi floating button
                    floatingParams.x = initialX + deltaX
                    floatingParams.y = initialY + deltaY

                    // Update tata letak di window manager
                    windowManager.updateViewLayout(floatingButtonView, floatingParams)
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP -> {
                    // Hitung jarak perpindahan
                    val deltaX = abs(event.rawX - initialTouchX)
                    val deltaY = abs(event.rawY - initialTouchY)

                    // Jika perpindahan sangat kecil, anggap sebagai klik
                    if (deltaX < 10 && deltaY < 10) {
                        if (!isMenuVisible) {
                            showOverlayMenu()
                        } else {
                            hideOverlayMenu()
                        }
                    }
                    return@setOnTouchListener true
                }

                else -> return@setOnTouchListener false
            }
        }
    }

    private fun showOverlayMenu() {
        if (overlayMenuView == null) {
            overlayMenuView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

            menuParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            menuParams.gravity = Gravity.CENTER

            setupMenuListeners()
            windowManager.addView(overlayMenuView, menuParams)
            isMenuVisible = true
        }
    }

    @Suppress("DEPRECATION")
    private fun setupMenuListeners() {
        overlayMenuView?.findViewById<ImageButton>(R.id.btnVolumeUp)?.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
        }

        overlayMenuView?.findViewById<ImageButton>(R.id.btnVolumeDown)?.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        }

        overlayMenuView?.findViewById<ImageButton>(R.id.btnMute)?.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustVolume(AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_SHOW_UI)
        }

        overlayMenuView?.findViewById<Button>(R.id.btnCloseOverlay)?.setOnClickListener {
            hideOverlayMenu()
        }

        overlayMenuView?.findViewById<Button>(R.id.btnStopService)?.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                stopForeground(true)
            }
            stopSelf()
        }
    }

    private fun hideOverlayMenu() {
        overlayMenuView?.let {
            windowManager.removeView(it)
            overlayMenuView = null
            isMenuVisible = false
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Volume Overlay Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Volume Overlay")
            .setContentText("Volume control overlay is running")
            .setSmallIcon(R.drawable.megaphone3)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(binding.root)
    }
}