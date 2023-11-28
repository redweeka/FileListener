package com.example.filelistener

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        Log.d(TAG, "onResume: ")
        super.onResume()

        if (getPermission()) {
            Log.d(TAG, "onResume: has permission")
            startBackgroundService()
        }
    }

    /**
     *    Check if all permission granted and ask for not granted permission
     *    @return true if all permission granted
     */
    private fun getPermission(): Boolean {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "getPermission: sdk 30 and above")

            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                startActivity(intent)

                return false
            }
        } else {
            Log.d(TAG, "getPermission: sdk below 30")

            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 904)

                return false
            }
        }

        return true
    }

    private fun startBackgroundService() {
        Log.d(TAG, "startBackgroundService: ")
        val serviceIntent = Intent(this, FilesObserverService::class.java)
        startService(serviceIntent)
    }
}
