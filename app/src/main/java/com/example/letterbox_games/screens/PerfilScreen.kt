package com.example.letterbox_games.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letterbox_games.model.Jogo
import com.example.letterbox_games.model.comoNota
import com.example.letterbox_games.ui.components.AvatarPessoa
import com.example.letterbox_games.ui.components.BotaoGradiente
import com.example.letterbox_games.ui.components.CampoTexto
import com.example.letterbox_games.ui.components.CartaoEscuro
import com.example.letterbox_games.ui.components.ChipOpcao
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.FundoGradiente
import com.example.letterbox_games.ui.components.MiniEstatistica
import com.example.letterbox_games.ui.components.RotuloCampo
import com.example.letterbox_games.ui.components.TituloSecao

/**
 * TELA 8 — Perfil: foto ou ícone, nome, sobre, estatísticas calculadas e configurações.
 * - "Editar perfil" abre um popup com nome, sobre e ícone (emoji).
 * - "Adicionar foto" abre a galeria do celular; "Remover foto" volta para o ícone.
 * - O interruptor alterna entre modo escuro e modo claro no app inteiro.
 */
@Composable
fun PerfilScreen(
    nome: String,
    sobre: String,
    avatar: String,
    fotoPerfil: Uri?,
    modoEscuro: Boolean,
    meusJogos: List<Jogo>,
    quantidadeAmigos: Int,
    onSalvarPerfil: (nome: String, sobre: String, avatar: String) -> Unit,
    onFotoEscolhida: (Uri?) -> Unit,
    onAlternarModo: (Boolean) -> Unit
) {
    val contexto = LocalContext.current
    var mostrarEdicao by remember { mutableStateOf(false) }

    val seletorDeFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onFotoEscolhida(uri)
    }

    val imagemDaFoto = remember(fotoPerfil) { fotoPerfil?.let { carregarImagem(contexto, it) } }

    val horasTotais = meusJogos.sumOf { it.horas }
    val notaMedia = if (meusJogos.isEmpty()) null else meusJogos.map { it.minhaNota }.average()
    val plataformaFavorita = meusJogos.groupingBy { it.plataforma }.eachCount().maxByOrNull { it.value }?.key
    val maisJogado = meusJogos.maxByOrNull { it.horas }

    FundoGradiente {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Cabeçalho do perfil
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (imagemDaFoto != null) {
                    Image(
                        bitmap = imagemDaFoto,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .border(2.dp, Cores.Destaque, CircleShape)
                    )
                } else {
                    AvatarPessoa(emoji = avatar, tamanho = 104.dp)
                }
                Text(nome, color = Cores.Texto, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = sobre.ifBlank { "Conte um pouco sobre você em Editar perfil." },
                    color = Cores.Cinza,
                    fontSize = 13.sp
                )
            }

            // Ações do perfil
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BotaoGradiente(
                    texto = "Editar perfil",
                    onClick = { mostrarEdicao = true },
                    modifier = Modifier.fillMaxWidth(),
                    icone = Icons.Filled.Edit
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = {
                            seletorDeFoto.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (fotoPerfil == null) "Adicionar foto" else "Trocar foto")
                    }
                    if (fotoPerfil != null) {
                        OutlinedButton(
                            onClick = { onFotoEscolhida(null) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Remover foto", color = Cores.Perigo)
                        }
                    }
                }
            }

            // Estatísticas calculadas
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TituloSecao("Suas estatísticas")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniEstatistica(Icons.Filled.SportsEsports, meusJogos.size.toString(), "Jogos", Modifier.weight(1f))
                    MiniEstatistica(Icons.Filled.Schedule, "$horasTotais h", "Horas", Modifier.weight(1f))
                    MiniEstatistica(Icons.Filled.Star, notaMedia.comoNota(), "Nota média", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniEstatistica(Icons.Filled.Group, quantidadeAmigos.toString(), "Amigos", Modifier.weight(1f))
                    MiniEstatistica(Icons.Filled.Devices, plataformaFavorita ?: "—", "Plataforma", Modifier.weight(1f))
                    MiniEstatistica(
                        Icons.Filled.Schedule,
                        maisJogado?.let { "${it.horas} h" } ?: "—",
                        "Mais jogado",
                        Modifier.weight(1f)
                    )
                }
                if (maisJogado != null) {
                    Text(
                        text = "Seu jogo mais jogado é ${maisJogado.nome}.",
                        color = Cores.Cinza,
                        fontSize = 12.sp
                    )
                }
            }

            // Aparência (tema claro/escuro)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TituloSecao("Aparência")
                CartaoEscuro(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (modoEscuro) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = Cores.Destaque
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Modo escuro", color = Cores.Texto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (modoEscuro) "Ligado" else "Desligado (modo claro)",
                                color = Cores.Cinza,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = modoEscuro,
                            onCheckedChange = onAlternarModo,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Cores.SobreDestaque,
                                checkedTrackColor = Cores.Destaque,
                                uncheckedThumbColor = Cores.Cinza,
                                uncheckedTrackColor = Cores.Borda
                            )
                        )
                    }
                }
            }

            // Sobre o app
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TituloSecao("Sobre o app")
                CartaoEscuro(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Letterbox Games", color = Cores.Texto, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Registre os jogos que você jogou, dê notas, acompanhe o que está em alta e veja o que seus amigos estão jogando.",
                            color = Cores.Cinza,
                            fontSize = 13.sp
                        )
                        Text("Versão 1.0  ·  Dados de exemplo fictícios", color = Cores.CinzaEscuro, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    if (mostrarEdicao) {
        EditarPerfilDialog(
            nomeAtual = nome,
            sobreAtual = sobre,
            avatarAtual = avatar,
            onSalvar = { novoNome, novoSobre, novoAvatar ->
                onSalvarPerfil(novoNome, novoSobre, novoAvatar)
                mostrarEdicao = false
            },
            onCancelar = { mostrarEdicao = false }
        )
    }
}

@Composable
private fun EditarPerfilDialog(
    nomeAtual: String,
    sobreAtual: String,
    avatarAtual: String,
    onSalvar: (nome: String, sobre: String, avatar: String) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf(nomeAtual) }
    var sobre by remember { mutableStateOf(sobreAtual) }
    var avatar by remember { mutableStateOf(avatarAtual) }
    var erro by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar perfil") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CampoTexto(
                    valor = nome,
                    aoAlterar = {
                        nome = it
                        erro = null
                    },
                    rotulo = "Nome",
                    erro = erro
                )
                CampoTexto(
                    valor = sobre,
                    aoAlterar = { sobre = it },
                    rotulo = "Sobre você",
                    singleLine = false,
                    minLines = 3
                )
                RotuloCampo("Ícone (quando não há foto)")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    avataresDisponiveis.forEach { opcao ->
                        ChipOpcao(opcao, avatar == opcao, { avatar = opcao })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val nomeLimpo = nome.trim()
                if (nomeLimpo.isBlank()) erro = "O nome não pode ficar vazio." else onSalvar(nomeLimpo, sobre.trim(), avatar)
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

/** Lê a foto escolhida na galeria já reduzida (para não gastar memória à toa). */
private fun carregarImagem(contexto: Context, uri: Uri, tamanhoMaximo: Int = 600): ImageBitmap? = runCatching {
    val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contexto.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, medidas) }
    var reducao = 1
    while (medidas.outWidth / reducao > tamanhoMaximo * 2 || medidas.outHeight / reducao > tamanhoMaximo * 2) {
        reducao *= 2
    }
    val opcoes = BitmapFactory.Options().apply { inSampleSize = reducao }
    contexto.contentResolver.openInputStream(uri)
        ?.use { BitmapFactory.decodeStream(it, null, opcoes) }
        ?.asImageBitmap()
}.getOrNull()
