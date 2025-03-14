package com.example.cangacoar.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

@Composable
fun ARScreen(permissionLauncher: ActivityResultLauncher<String>) {
    val context = LocalContext.current
    var selectedModel by remember { mutableStateOf("Lamp") } // Começa com "Lamp" como padrão
    val models = mapOf(
        "Lamp" to "https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/lamp_final.glb",
        "Maria" to "https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/maria_final.glb"
    )
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tela de Realidade Aumentada",
            fontSize = 24.sp
        )
        Text(
            text = "Modelo selecionado: $selectedModel",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = { expanded = true }) {
            Text("Escolher Modelo")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            models.forEach { (name, _) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        selectedModel = name
                        expanded = false
                    }
                )
            }
        }
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
                    val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
                        .buildUpon()
                        .appendQueryParameter(
                            "file",
                            models[selectedModel]
                        )
                        .appendQueryParameter("mode", "ar_preferred")
                        .build()

                    sceneViewerIntent.data = intentUri
                    sceneViewerIntent.setPackage("com.google.ar.core")
                    context.startActivity(sceneViewerIntent)
                } catch (e: Exception) {
                    Log.e("ARScreen", "Erro ao abrir SceneView: ${e.message}")
                    Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text("Abrir Modelo Selecionado")
        }
    }
}