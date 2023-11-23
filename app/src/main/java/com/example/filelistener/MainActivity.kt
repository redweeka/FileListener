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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val PERMISSION_REQUEST_CODE = 873

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()
    }

    private fun getPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "onCreate: sdk 30 and above")

            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)

                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (Environment.isExternalStorageManager()) {
                        startBackgroundService()
                    }
                }.launch(intent)
            } else {
                Log.d(TAG, "onCreate: has permission")
                startBackgroundService()
            }
        } else {
            Log.d(TAG, "onCreate: sdk below 30")

            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            } else {
                Log.d(TAG, "onCreate: has permission")
                startBackgroundService()
            }
        }
    }

    private fun startBackgroundService() {
        Log.d(TAG, "startService: ")
        val serviceIntent = Intent(this, FilesObserverService::class.java)
        startService(serviceIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: has permission")
                    // Permission granted, now you can access the folder
                    startBackgroundService()
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: permission denied")
                }
            }
        }
    }
}
