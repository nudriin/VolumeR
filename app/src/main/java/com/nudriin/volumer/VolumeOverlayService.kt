package com.nudriin.volumer

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.nudriin.volumer.databinding.OverlayLayoutBinding
import kotlin.math.abs

class VolumeOverlayService : Service() {
    private lateinit var binding: OverlayLayoutBinding
    private lateinit var windowManager: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Inisialisasi View Binding
        binding = OverlayLayoutBinding.inflate(LayoutInflater.from(this))

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Posisi awal overlay
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        // Tambahkan view ke window manager
        windowManager.addView(binding.root, params)

        // Tambahkan listener drag
        binding.overlayContainer.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Simpan posisi awal
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_MOVE -> {
                    // Hitung perpindahan
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()

                    // Update posisi
                    params.x = initialX + deltaX
                    params.y = initialY + deltaY

                    // Update view
                    windowManager.updateViewLayout(binding.root, params)
                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP -> {
                    // Optional: Tambahkan logika klik jika perpindahan sangat kecil
                    val deltaX = abs(event.rawX - initialTouchX)
                    val deltaY = abs(event.rawY - initialTouchY)

                    if (deltaX < 10 && deltaY < 10) {
                        // Ini dianggap sebagai klik, bukan drag
                        v.performClick()
                    }
                    return@setOnTouchListener true
                }

                else -> return@setOnTouchListener false
            }
        }

        // Listener untuk tombol
        binding.btnCloseOverlay.setOnClickListener {
            stopSelf() // Tutup service
        }

        binding.btnVolumeUp.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
        }

        binding.btnVolumeDown.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        }

        binding.btnMute.setOnClickListener {
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            audioManager.adjustVolume(AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_SHOW_UI)
            Toast.makeText(this, "Mute toggled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hapus overlay view
        windowManager.removeView(binding.root)
    }
}