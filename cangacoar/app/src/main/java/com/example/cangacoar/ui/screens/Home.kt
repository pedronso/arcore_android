package com.example.cangacoar.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cangacoar.R

@Composable
fun HomeScreen(permissionLauncher: ActivityResultLauncher<String>) {
    val context = LocalContext.current
    var selectedModel by remember { mutableStateOf("Lampião") }
    val models = mapOf(
        "Lampião" to "https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/lamp_final.glb",
        "Maria Bonita" to "https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/maria_final.glb"
    )
    var expanded by remember { mutableStateOf(false) }
    val anchor = remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_ra),
            contentDescription = "Logo do App",
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .padding(2.dp)
        )

        Text(
            text = "Bem-vindo ao CangacoRA!",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selecione o modelo",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    anchor.value = coordinates.boundsInWindow()
                }
            ) {
                Text(selectedModel)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir menu"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 0.dp, y = 8.dp),
                modifier = Modifier.width(200.dp)
            ) {
                models.keys.forEach { name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedModel = name
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
                        val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
                            .buildUpon()
                            .appendQueryParameter("file", models[selectedModel])
                            .appendQueryParameter("mode", "ar_preferred")
                            .build()

                        sceneViewerIntent.data = intentUri
                        sceneViewerIntent.setPackage("com.google.ar.core")
                        context.startActivity(sceneViewerIntent)
                    } catch (e: Exception) {
                        Log.e("HomeScreen", "Erro ao abrir SceneView: ${e.message}")
                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Text(
                text = "Iniciar",
                fontSize = 20.sp
            )
        }
    }
}

