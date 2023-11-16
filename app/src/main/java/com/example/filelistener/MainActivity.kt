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
import com.example.filelistener.Constants.Companion.FILE_OBSERVED_PATH
import java.io.File

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val PERMISSION_REQUEST_CODE = 873
    private var myFileObserver: MyFileObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "onCreate: sdk 30 and above")

            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)

                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (Environment.isExternalStorageManager()) {
                        accessFolder()
                    }
                }.launch(intent)
            } else {
                Log.d(TAG, "onCreate: has permission")
                accessFolder()
            }
        } else {
            Log.d(TAG, "onCreate: sdk below 30")

            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            } else {
                Log.d(TAG, "onCreate: has permission")
                accessFolder()
            }
        }
    }

    private fun accessFolder() {
        val file = File(FILE_OBSERVED_PATH)

        if (file.exists()) {
            myFileObserver = MyFileObserver(FILE_OBSERVED_PATH)
            myFileObserver?.startWatching()
        } else {
            Log.w(TAG, "accessFolder: file not exists")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: has permission")
                    // Permission granted, now you can access the folder
                    accessFolder()
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: permission denied")
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()

        myFileObserver?.stopWatching()
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed: sdk $SDK_INT")

    }
}
