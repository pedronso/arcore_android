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

@Composable
fun HomeScreen(
    permissionLauncher: ActivityResultLauncher<String>,
    darkTheme: Boolean, // Recebendo o parâmetro darkTheme
    onThemeChange: (Boolean) -> Unit // Recebendo a função onThemeChange
) {
    val context = LocalContext.current
    var selectedModel by remember { mutableStateOf(lampiao) }
    var showDialog by remember { mutableStateOf(false) }
    val models = mapOf(
        lampiao to Pair(lampiao3D, lampiaoDescription),
        mariaBonita to Pair(mariaBonita3D, mariaBonitaDescription),
        pistolaLuger to  Pair(pistolaLuger3D, pistolaLugerDescription),
        carabina to  Pair(carabina3D, carabinaDescription),
        garrucha to  Pair(garrucha3D, garruchaDescription),
        cabaca to  Pair(cabaca3D, cabacaDescription),
        baiaca to  Pair(baiaca3D, baiacaDescription),
        cartucheira to  Pair(cartucheira3D, cartucheiraDescription),
        oculosLampiao to  Pair(oculosLampiao3D, oculosLampiaoDescription),
        garrafaVinho to  Pair(garrafaVinho3D, garrafaVinhoDescription),
        chapeuCouro to  Pair(chapeuCouro3D, chapeuCouroDescription),
        sandaliasCouro to  Pair(sandaliasCouro3D, sandaliasCouroDescription),
    )

    // Mudando o fundo para adaptar ao tema
    val backgroundColor = MaterialTheme.colorScheme.background
    // Definindo a cor do texto com base no tema
    val textColor = if (darkTheme) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground

    // Escolher a imagem do logo dependendo do tema
    val logoResource = if (darkTheme) R.drawable.logo_ra_dark else R.drawable.logo_ra

    // Definir a cor do botão de acordo com o tema
    val buttonColor = if (darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val buttonContentColor = if (darkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(backgroundColor), // Define o fundo conforme o tema
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Alterando a imagem do logo conforme o tema
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
            color = textColor // Definindo a cor do texto
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Componente para alternar entre os temas
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = if (darkTheme) "Modo Escuro" else "Modo Claro",
                fontSize = 16.sp,
                color = textColor // Usando a cor do texto
            )

            Switch(
                checked = darkTheme,
                onCheckedChange = { onThemeChange(it) } // Chama a função para mudar o tema
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(
                text = "Selecione o modelo",
                fontSize = 16.sp,
                color = textColor // Usando a cor do texto
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Informações do modelo",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showDialog = true }
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
                            color = textColor // Usando a cor do texto
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
                .padding(vertical = 16.dp) // Espaçamento vertical para o botão
                .height(60.dp) // Aumenta a altura do botão
                .fillMaxWidth(), // Ocupa a largura toda
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor, // Background color of the button
                contentColor = buttonContentColor // Text color inside the button
            ),
            shape = RoundedCornerShape(8.dp) // Adicionando cantos arredondados
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
