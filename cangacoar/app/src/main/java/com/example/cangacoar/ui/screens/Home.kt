package com.example.cangacoar.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults // Importing ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cangacoar.R
import androidx.core.net.toUri
import androidx.compose.material3.Switch
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun HomeScreen(
    permissionLauncher: ActivityResultLauncher<String>
) {
    val context = LocalContext.current
    var selectedModel by remember { mutableStateOf(lampiao) }
    var showDialog by remember { mutableStateOf(false) }
    val models = mapOf(
        lampiao to Pair(lampiao3D, lampiaoDescription),
        mariaBonita to Pair(mariaBonita3D, mariaBonitaDescription),
        pistolaLuger to Pair(pistolaLuger3D, pistolaLugerDescription),
        carabina to Pair(carabina3D, carabinaDescription),
        garrucha to Pair(garrucha3D, garruchaDescription),
        cabaca to Pair(cabaca3D, cabacaDescription),
        baiaca to Pair(baiaca3D, baiacaDescription),
        cartucheira to Pair(cartucheira3D, cartucheiraDescription),
        oculosLampiao to Pair(oculosLampiao3D, oculosLampiaoDescription),
        garrafaVinho to Pair(garrafaVinho3D, garrafaVinhoDescription),
        chapeuCouro to Pair(chapeuCouro3D, chapeuCouroDescription),
        sandaliasCouro to Pair(sandaliasCouro3D, sandaliasCouroDescription),
    )

    val isDarkTheme = isSystemInDarkTheme() // Usa o estado do sistema diretamente
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val logoResource = if (isDarkTheme) R.drawable.logo_ra_dark else R.drawable.logo_ra
    val buttonColor = MaterialTheme.colorScheme.primary
    val buttonContentColor = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = logoResource),
            contentDescription = "Logo do App",
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .padding(2.dp)
        )

        Text(
            text = "Bem-vindo ao CangacoRA!",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = textColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(
                text = "Selecione o modelo",
                fontSize = 16.sp,
                color = textColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Informações do modelo",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showDialog = true },
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(models.keys.toList()) { name ->
                val isSelected = name == selectedModel

                Card(
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { selectedModel = name }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = getModelPreviewImage(name)),
                            contentDescription = name,
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = name,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(4.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = textColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
                        val intentUri = "https://arvr.google.com/scene-viewer/1.0".toUri()
                            .buildUpon()
                            .appendQueryParameter("file", models[selectedModel]?.first)
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
            },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .height(60.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = buttonContentColor
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Iniciar",
                fontSize = 28.sp
            )
        }
    }

    if (showDialog) {
        InfoDialog(
            title = selectedModel,
            text = models[selectedModel]?.second ?: "Nenhuma informação disponível.",
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun InfoDialog(title: String, text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
fun getModelPreviewImage(selectedModel: String): Int {
    return when (selectedModel) {
        lampiao -> R.drawable.lamp
        mariaBonita -> R.drawable.maria
        pistolaLuger -> R.drawable.luger
        carabina -> R.drawable.carabina
        garrucha -> R.drawable.garrucha
        cabaca -> R.drawable.cabaca
        baiaca -> R.drawable.baiaca
        cartucheira -> R.drawable.cartucheira
        oculosLampiao -> R.drawable.oculos
        garrafaVinho -> R.drawable.garrafa
        chapeuCouro -> R.drawable.chapeu
        sandaliasCouro -> R.drawable.sandalias
        else -> R.drawable.logo_ra
    }
}