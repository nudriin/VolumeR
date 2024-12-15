package com.nudriin.volumer

import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        findViewById<Button>(R.id.btnIncreaseVolume).setOnClickListener {
            adjustVolume(AudioManager.ADJUST_RAISE)
        }

        findViewById<Button>(R.id.btnDecreaseVolume).setOnClickListener {
            adjustVolume(AudioManager.ADJUST_LOWER)
        }

        findViewById<Button>(R.id.btnStartOverlay).setOnClickListener {
            checkOverlayPermission()
        }
    }

    private fun adjustVolume(direction: Int) {
        audioManager.adjustVolume(direction, AudioManager.FLAG_SHOW_UI)
    }

    @Suppress("DEPRECATION")
    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        } else {
            startOverlayService()
        }
    }

    private fun startOverlayService() {
        val serviceIntent = Intent(this, VolumeOverlayService::class.java)
        startService(serviceIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                startOverlayService()
            } else {
                Toast.makeText(this, "Izin overlay diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    }
}