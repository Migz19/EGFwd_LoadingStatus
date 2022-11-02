package com.example.loadapp

import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.loadapp.databinding.ContentDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ContentDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        val state = intent.getBooleanExtra("state",true)
        val fileName=intent.getStringExtra("name")
        binding.stateTv.text = when (state) {
            true -> "Succeeded"
            false -> "Failed"
        }
        binding.fileTv.text=fileName
        binding.button2.setOnClickListener {
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val notificationManager =
            ContextCompat.getSystemService(applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()
    }

}