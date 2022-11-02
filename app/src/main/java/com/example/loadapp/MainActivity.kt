package com.example.loadapp

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.loadapp.databinding.ActivityMainBinding
import com.example.loadapp.databinding.ContentMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val downloadManager: DownloadManager by lazy { getSystemService(DOWNLOAD_SERVICE) as DownloadManager }
    var downloadLink: String? = null
    var isEditTxtVisible: Boolean = false
    var fileName: String =""
    var downloadSucceeded: Boolean = false
    var downloadId:Long=0
    lateinit var binding: ContentMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(toolbar)
        val toActivity = Intent(applicationContext, DetailActivity::class.java)
        custom_button.buttonState=ButtonState.Idle
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    custom_button.buttonState = ButtonState.Completed
                } else {
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                    custom_button.buttonState = ButtonState.Idle
                    downloadSucceeded = false
                }
                downloadSucceeded = validDownload(downloadId)
                val notificationManager = ContextCompat.getSystemService(application,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(fileName, applicationContext)
                toActivity.putExtra("name", fileName)
                toActivity.putExtra("state", downloadSucceeded)
                startActivity(toActivity)
            }

        }
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedBtn(checkedId)
            getUrlfromTxt()
        }
        custom_button.setOnClickListener {
            if (downloadLink != null) {
                try {
                    downloadId = downloadFile()

                    custom_button.buttonState = ButtonState.Loading
                } catch (e: java.lang.IllegalArgumentException) {
                    Toast.makeText(applicationContext, "Url is not valid", Toast.LENGTH_SHORT).show()
                }
            } else
                Toast.makeText(applicationContext, "Please add download link or choose one ", Toast.LENGTH_SHORT).show()
        }
        createChannel(
            applicationContext.getString(R.string.channel_id),
            applicationContext.getString(R.string.channel_title)
        )
    }

    private fun downloadFile(): Long {
        val request = DownloadManager.Request(Uri.parse(downloadLink))
        request.setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "managerDownload24"
            )
        return downloadManager.enqueue(request)

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.description = applicationContext.getString(R.string.notification_description)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            val notificationManager = application.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun selectedBtn(id: Int) {
        when (id) {
            R.id.glideRB -> {
                downloadLink = GLIDE_URL
                isEditTxtVisible = false
                fileName = applicationContext.getString(R.string.glide_file)
            }
            R.id.udacityRB -> {
                downloadLink = UDACITY_URL
                isEditTxtVisible = false
                fileName = applicationContext.getString(R.string.udacity_file)

            }
            R.id.retrofitRB -> {
                downloadLink = RETROFIT_URL
                isEditTxtVisible = false
                fileName = applicationContext.getString(R.string.retrofit_file)

            }
            else -> {
                isEditTxtVisible = true
                fileName = "Custom File"
            }
        }
    }

    private fun getUrlfromTxt() {
        val editTxt = binding.editTxt
        var text: Editable? = null
        if (isEditTxtVisible) {
            editTxt.visibility = View.VISIBLE
            text = editTxt.text
        } else
            editTxt.visibility = View.INVISIBLE
        if (text != null)
            downloadLink = editTxt.toString()
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
    }

    @SuppressLint("Range")
    private fun validDownload(downloadId: Long): Boolean {
        Log.d(ContentValues.TAG, "Checking download status for id: $downloadId")
        //Verify if download is a success
        val c: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
        if (c.moveToFirst()) {
            val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            return if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Log.d(ContentValues.TAG, "File was downloading properly.")
                true
            } else {
                val reason: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                Log.d(ContentValues.TAG, "Download not correct, status [$status] reason [$reason]")
                false
            }
        }
        return false
    }
}