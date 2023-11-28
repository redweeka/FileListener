package com.example.filelistener

import android.util.Log
import com.example.filelistener.Globals.Companion.INI_FILE_PATH
import com.example.filelistener.Globals.Companion.OBSERVED_FOLDERS_PARENT
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File

class ConfigManager private constructor() {
    private val TAG = "ConfigManager"
    private var configs = IniConfigFile()
    private val observedFolders = ArrayList<File>()

    companion object {
        private var instance: ConfigManager? = null

        fun getInstance(): ConfigManager {
            return instance ?: synchronized(this) {
                instance ?: ConfigManager().also { instance = it }
            }
        }
    }

    init {
        // Read configs from JSON file
        try {
            val jsonConfigFile = File(INI_FILE_PATH)

            if (jsonConfigFile.exists()) {
                val jsonString = jsonConfigFile.readText()
                configs = Json.decodeFromString(jsonString)
            }

            // Build the observed folders
            configs.observedFoldersNames.forEach {
                observedFolders.add(File("${OBSERVED_FOLDERS_PARENT}/$it"))
            }

            Log.i(TAG, "observedFolders      = $observedFolders")
            Log.i(TAG, "sentFolderName       = ${getSentFolderName()}")
            Log.i(TAG, "url                  = ${getUrl()}")
            Log.i(TAG, "serviceSecondsPeriod = ${getServiceSecondsPeriod()}")
        } catch (e: SerializationException) {
            Log.e(TAG, "loadIniFile: Error reading JSON file: ${e.message}")
        }
    }

    fun getObservedFolders(): List<File> {
        return observedFolders
    }

    fun getSentFolderName(): String {
        return configs.sentFolderName
    }

    fun getUrl(): String {
        return configs.url
    }

    fun getServiceSecondsPeriod(): Long {
        return configs.serviceSecondsPeriod
    }
}
