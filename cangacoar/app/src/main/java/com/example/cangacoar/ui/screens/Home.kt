package com.example.cangacoar.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.cangacoar.R

@Composable
fun HomeScreen(permissionLauncher: ActivityResultLauncher<String>) {
    val context = LocalContext.current
    var selectedModel by remember { mutableStateOf("Lampião") }
    var showDialog by remember { mutableStateOf(false) }
    val models = mapOf(
        "Lampião" to Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/lamp_resize.glb",
            "Virgolino Ferreira da Silva, vulgo Lampião,  nascido em Vila Bela, atual Serra Talhada, Sertão do Pajeú, que entra para a história com o curioso apelido de Lampião.  Motivado por vingança, mediante a desorganização social dos sertões do Nordeste, tornou-se cangaceiro."),
        "Maria Bonita" to Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/maria_resize.glb",
            "Maria Gomes de Oliveira, conhecida como Maria de Déa e, após sua morte, Maria Bonita, foi uma cangaceira brasileira, companheira de Virgulino Ferreira da Silva, o Lampião e a primeira mulher a participar de um grupo de cangaceiros."),
        "Pistola Luger" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/luger.glb",
            "A pistola Luger P08 é uma antiga pistola semiautomática fabricada na Alemanha, entre os anos de 1900 e 1941. Foi considerada como o maior souvenir da Segunda Guerra Mundial. Esta pistola foi adotada pelo exército alemão em 1908, razão do nome P08. De calibre 9 mm – parabelum – cano de 4 polegadas. Uma arma destas foi encontrada com Lampião, após sua morte, em Angico."),
        "Carabina" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/carabina.glb",
            "A Carabina Winchester 1873 era conhecido popularmente como Rifle “Papo Amarelo” devido a uma peça de latão exposta na parte inferior da caixa da culatra (parte posterior do mecanismo de uma Arma de Fogo, onde se aloja o mecanismo de disparo, localizada junto à coronha). Foi vastamente usado no momento inicial do cangaço, perdurando até 1926."),
        "Garrucha" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/garrucha.glb",
            "A garrucha é uma arma de fogo de pequeno porte, de cano duplo, geralmente de calibre 22 ou 32. Era muito utilizada pelos cangaceiros por sua facilidade de manuseio e o poder de disparar dois tiros rapidamente. Era comumente carregada como arma de reserva e escondida nas vestes, sendo utilizadas por cangaceiros e volantes por sua discrição e eficácia em combates rápidos."),
        "Cabaça" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/cabaca.glb",
            "Usada para armazenar água, essencial para a sobrevivência no ambiente seco do sertão nordestino. Também utilizada para guardar bebidas alcoólicas, como aguardente."),
        "Baiaca" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/baiaca.glb",
            "Usado tanto por cangaceiros quanto pela volante, na baiaca eram carregados os objetos preciosos, que não podiam ser expostos ao suor ou chuva, como cartas, dinheiro, ou cigarros."),
        "Cartucheira" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/cartucheira.glb",
            "As cartucheiras eram confeccionadas em couro, para guardar munição. Usavam uma ou duas na cintura e outra, em formato de X, cruzada no tórax, por cima das roupas, estrategicamente posicionadas."),
        "Óculos de Lampião" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/oculos.glb",
            "Lampião, desde jovem, apresentava problemas de visão. Percebe-se em várias fotografias que seu olho esquerdo tem o globo embranquecido e a pálpebra mais baixa. Entretanto, em 1925 ele teve seu olho esquerdo arranhado por um garrancho de jurema. Após alguns tratamentos, com leves melhoras, Lampião passa a usar óculos escuros, por que a luminosidade do sol provocava incômodos.  Porém, ele só se deixou fotografar com óculos claro, com aros de ouro, por questão de vaidade."),
        "Garrafa de vinho" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/garrafa.glb",
            "A garrafa de vinho, embora não fosse um item essencial como a cabaça, era ocasionalmente carregada pelos cangaceiros em momentos de celebração ou repouso. Feita de vidro ou cerâmica, podia conter bebidas alcoólicas como vinho ou cachaça, adquiridas em saques ou trocas com moradores locais. Representava um raro luxo no árduo cotidiano do sertão, sendo um símbolo de status entre os bandos de Lampião."),
        "Chapéu de couro" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/chapeu.glb",
            "O chapéu serviu de suporte de arte, já que eram colocados alguns enfeites, e também de alerta, pois nenhum cangaceiro poderia correr o risco de ser surpreendido em uma emboscada, por isso não usavam com a aba abaixada escondendo os olhos."),
        "Sandálias de couro" to  Pair("https://github.com/pedronso/arcore_android/raw/refs/heads/master/cangacoar/resources/models/sandalias.glb",
            "Podemos dizer que as sandálias dos cangaceiros eram as chamadas alpercatas, que, mediante as amplas distâncias que eram percorridas, a pé, as alpercatas eram confeccionadas em couro de boi, com um solado resistente para longas caminhadas e enfrentamento do solo áspero e dos espinhos da vegetação típica da caatinga. Em cada região que andava com seu bando, Lampião já tinha em determinados locais, remendões para cuidar de seus calçados e do respectivo grupo."),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            var buttonWidth by remember { mutableStateOf(0) }

            Button(
                onClick = { expanded = true },
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        buttonWidth = coordinates.size.width
                    }
            ) {
                Text(selectedModel)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir seleção de modelos"
                )
            }

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Informações do modelo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (buttonWidth / 4.5).dp)
                    .size(24.dp)
                    .clickable { showDialog = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
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
            }
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

