package com.example.cangacoar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.cangacoar.ui.screens.HomeScreen
import com.example.cangacoar.ui.theme.CangacoARTheme

class MainActivity : ComponentActivity() {
    private val cameraPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permissão de câmera concedida", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_LONG).show()
        }
    }

    private var darkTheme by mutableStateOf(false) // false significa Light Mode, true significa Dark Mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CangacoARTheme(darkTheme = darkTheme) {
                HomeScreen(
                    permissionLauncher = cameraPermissionRequest,
                    darkTheme = darkTheme,
                    onThemeChange = { newDarkTheme: Boolean -> // Especificando explicitamente o tipo de `newDarkTheme` como Boolean para tirar o erro
                        darkTheme = newDarkTheme
                    }
                )
            }
        }

        // Solicitar permissão de câmera ao iniciar
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }
}
