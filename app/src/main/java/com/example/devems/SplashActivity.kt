package com.example.devems

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DURATION: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoView: VideoView = findViewById(R.id.splashVideoView)
        val videoUri = Uri.parse("android.resource://${packageName}/raw/event_management_system")
        videoView.setVideoURI(videoUri)
        videoView.start()

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_DURATION)
    }
}
