package com.aac.audiolibrary.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aac.audiolibrary.database.AppDatabase
import com.aac.audiolibrary.database.Song
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private val songDao = AppDatabase.getDatabase(application).songDao()

    suspend fun getAllSongs() = viewModelScope.async { songDao.getAllSongs()}

    fun insertSong(song: Song) {
        viewModelScope.launch {
            songDao.insertSong(song)
        }
    }
}
