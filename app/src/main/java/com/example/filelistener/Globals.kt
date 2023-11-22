package com.example.filelistener

class Globals {
    companion object {
        // File system related
        var OBSERVED_FOLDER_PATH = "sdcard/DCIM/Camera"
        const val DEFAULT_PHOTO_SENT_FOLDER_NAME = "temp"

        // Api related
        const val API_PHOTOS_FILE_URL = "http://192.168.120.40:5000/upload" //Insert here your server ipv4 address.
    }
}