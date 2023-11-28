package com.example.filelistener

import com.example.filelistener.Globals.Companion.DEFAULT_OBSERVED_FILES
import com.example.filelistener.Globals.Companion.DEFAULT_PHOTO_SENT_FOLDER_NAME
import com.example.filelistener.Globals.Companion.DEFAULT_SERVICE_SECONDS_PERIOD
import com.example.filelistener.Globals.Companion.DEFAULT_UPLOAD_PHOTOS_URL
import kotlinx.serialization.Serializable

@Serializable
data class IniConfigFile(
    val observedFoldersNames: List<String> = DEFAULT_OBSERVED_FILES,
    val sentFolderName: String = DEFAULT_PHOTO_SENT_FOLDER_NAME,
    val url: String = DEFAULT_UPLOAD_PHOTOS_URL,
    val serviceSecondsPeriod: Long = DEFAULT_SERVICE_SECONDS_PERIOD
)