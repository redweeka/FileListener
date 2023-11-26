package com.example.filelistener

import java.io.FileInputStream
import java.util.Properties

class IniFileReader private constructor() {
    private val properties = Properties()

    companion object {
        private var instance: IniFileReader? = null

        fun getInstance(): IniFileReader {
            return instance ?: synchronized(this) {
                instance ?: IniFileReader().also { instance = it }
            }
        }
    }

    init {
        loadIniFile()
    }

    private fun loadIniFile() {
        try {
            val input = FileInputStream("sdcard/894/fileListener.ini")
            properties.load(input)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getProperty(key: String): String? {
        return properties.getProperty(key)
    }

    fun getObservedFolderPath(): String? {
        return getProperty("observed_folder_path")
    }

    fun getSentFolderName(): String? {
        return getProperty("sent_folder_name")
    }

    fun getApiIp(): String? {
        return getProperty("api_ip")
    }

    fun getApiPort(): String? {
        return getProperty("api_port")
    }

    fun getServiceSecondsPeriod(): Long? {
        return getProperty("service_seconds_period")?.toLong()
    }
}
