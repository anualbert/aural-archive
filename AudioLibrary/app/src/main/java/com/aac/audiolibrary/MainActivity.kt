package com.aac.audiolibrary

import android.app.AlertDialog
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.aac.audiolibrary.ui.screens.MusicPlayerScreen
import com.aac.audiolibrary.ui.theme.AudioLibraryTheme
import com.aac.audiolibrary.viewmodels.SongViewModel

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue with the operation.
            setContent {
                AudioLibraryTheme {
                    MusicPlayerScreen(SongViewModel(application))
                }
            }
        } else {
            // Permission is denied. Show a message to the user.
            AlertDialog.Builder(this)
                .setMessage("Permission is required to access audio files.")
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val viewmodel = ViewModelProvider(this)[SongViewModel::class.java]
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                setContent {
                    AudioLibraryTheme {
                        MusicPlayerScreen(viewmodel)
                    }
                }
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            else -> {
                // Display a rationale
                AlertDialog.Builder(this)
                    .setMessage("Permission is required to access audio files.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                    }
                    .show()
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AudioLibraryTheme {
        Greeting("Android")
    }
}