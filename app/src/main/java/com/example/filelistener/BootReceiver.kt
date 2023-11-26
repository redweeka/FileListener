package com.example.filelistener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver: BroadcastReceiver() {
    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "onReceive: BOOT completed")
            val serviceIntent = Intent(context, FilesObserverService::class.java)
            context.startService(serviceIntent)
        }
    }
}