package com.example.filelistener

import java.io.File

class Globals {
    companion object {
        private val iniFileReader = IniFileReader.getInstance()

        // File system related
        private val OBSERVED_FOLDER_PATH = iniFileReader.getObservedFolderPath() ?: "sdcard/DCIM/Camera"
        val OBSERVED_FOLDER = File(OBSERVED_FOLDER_PATH)
        val PHOTO_SENT_FOLDER_NAME = iniFileReader.getSentFolderName() ?: "temp"

        // Api related
        private val API_IP = iniFileReader.getApiIp() ?: "10.0.0.28"
        private val API_PORT = iniFileReader.getApiPort() ?: "5000"
        val API_PHOTOS_FILE_URL = "http://$API_IP:$API_PORT/upload" //Insert here your server ipv4 address.

        // Service loop schedule
        val SERVICE_SECONDS_PERIOD = iniFileReader.getServiceSecondsPeriod() ?: 6
    }
}