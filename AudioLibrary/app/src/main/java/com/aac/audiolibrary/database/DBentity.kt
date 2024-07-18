package com.aac.audiolibrary.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val title: String? ="",
    val artist: String?="",
    val duration: Long?=0,
    val data: String // Path to the song file
)
