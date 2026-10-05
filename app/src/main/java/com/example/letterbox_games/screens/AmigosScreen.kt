package com.example.letterbox_games.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letterbox_games.model.Amigo
import com.example.letterbox_games.model.Atividade
import com.example.letterbox_games.model.tempoAtras
import com.example.letterbox_games.ui.components.AvatarPessoa
import com.example.letterbox_games.ui.components.BotaoRedondo
import com.example.letterbox_games.ui.components.CabecalhoTela
import com.example.letterbox_games.ui.components.CampoTexto
import com.example.letterbox_games.ui.components.CartaoEscuro
import com.example.letterbox_games.ui.components.ChipOpcao
import com.example.letterbox_games.ui.components.ConfirmarDialog
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.EstadoVazio
import com.example.letterbox_games.ui.components.FundoGradiente
import com.example.letterbox_games.ui.components.RotuloCampo

val avataresDisponiveis = listOf("🎮", "🦊", "🐙", "🎧", "🚀", "🐱", "👾", "🦄", "🐺", "🤖")

/**
 * TELA 5 — Amigos (LISTA 2, data class Amigo).
 * - Busca por nome.
 * - O botão de "adicionar amigo" (cabeçalho) abre um formulário com nome, bio e avatar.
 * - Cada card tem um botão para remover o amigo (com confirmação).
 * - Tocar no card abre o Detalhe do Amigo.
 */
@Composable
fun AmigosScreen(
    amigos: List<Amigo>,
    atividades: List<Atividade>,
    onAbrirDetalhe: (String) -> Unit,
    onAdicionar: (nome: String, avatar: String, bio: String) -> Unit,
    onRemover: (String) -> Unit
) {
    var busca by remember { mutableStateOf("") }
    var mostrarAdicionar by remember { mutableStateOf(false) }
    var amigoParaRemover by remember { mutableStateOf<Amigo?>(null) }

    val filtrados = amigos.filter { busca.isBlank() || it.nome.contains(busca.trim(), ignoreCase = true) }

    FundoGradiente {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                CabecalhoTela(
                    sobretitulo = "SOCIAL",
                    titulo = "Amigos",
                    subtitulo = "${amigos.size} ${if (amigos.size == 1) "amigo" else "amigos"}",
                    acao = { BotaoRedondo(Icons.Filled.PersonAdd, "Adicionar amigo", { mostrarAdicionar = true }) }
                )
                Spacer(Modifier.height(16.dp))
                CampoTexto(
                    valor = busca,
                    aoAlterar = { busca = it },
                    rotulo = "Buscar amigo",
                    placeholder = "Digite o nome...",
                    iconeInicial = Icons.Filled.Search
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filtrados.isEmpty()) {
                    item {
                        if (amigos.isEmpty()) {
                            EstadoVazio(
                                titulo = "Você ainda não tem amigos aqui",
                                texto = "Toque no ícone de pessoa no topo para adicionar o primeiro."
                            )
                        } else {
                            EstadoVazio(titulo = "Ninguém encontrado", texto = "Tente outro nome.")
                        }
                    }
                } else {
                    items(filtrados, key = { it.id }) { amigo ->
                        val doAmigo = atividades.filter { it.amigoId == amigo.id }
                        AmigoCard(
                            amigo = amigo,
                            quantidadeJogos = doAmigo.map { it.jogoId }.distinct().size,
                            ultimaAtividade = doAmigo.maxOfOrNull { it.quando },
                            onClick = { onAbrirDetalhe(amigo.id) },
                            onRemover = { amigoParaRemover = amigo }
                        )
                    }
                }
            }
        }
    }

    if (mostrarAdicionar) {
        AdicionarAmigoDialog(
            nomesExistentes = amigos.map { it.nome },
            onAdicionar = { nome, avatar, bio ->
                onAdicionar(nome, avatar, bio)
                mostrarAdicionar = false
            },
            onCancelar = { mostrarAdicionar = false }
        )
    }

    amigoParaRemover?.let { amigo ->
        ConfirmarDialog(
            titulo = "Remover amigo?",
            texto = "${amigo.nome} sai da sua lista e a atividade dele some do seu feed.",
            onConfirmar = {
                onRemover(amigo.id)
                amigoParaRemover = null
            },
            onCancelar = { amigoParaRemover = null }
        )
    }
}

@Composable
private fun AmigoCard(
    amigo: Amigo,
    quantidadeJogos: Int,
    ultimaAtividade: Long?,
    onClick: () -> Unit,
    onRemover: () -> Unit
) {
    CartaoEscuro(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarPessoa(emoji = amigo.avatar, tamanho = 48.dp)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = amigo.nome,
                    color = Cores.Texto,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (amigo.bio.isNotBlank()) {
                    Text(
                        text = amigo.bio,
                        color = Cores.Cinza,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = if (ultimaAtividade == null) {
                        "Sem atividade ainda"
                    } else {
                        "$quantidadeJogos ${if (quantidadeJogos == 1) "jogo" else "jogos"}  ·  ${tempoAtras(ultimaAtividade)}"
                    },
                    color = Cores.Destaque,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onRemover) {
                Icon(Icons.Filled.PersonRemove, contentDescription = "Remover ${amigo.nome}", tint = Cores.Perigo)
            }
        }
    }
}

/** Formulário (em popup) para adicionar um amigo novo. */
@Composable
private fun AdicionarAmigoDialog(
    nomesExistentes: List<String>,
    onAdicionar: (nome: String, avatar: String, bio: String) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf(avataresDisponiveis.first()) }
    var erro by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Adicionar amigo") },
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
                    placeholder = "Como seu amigo se chama?",
                    erro = erro
                )
                CampoTexto(
                    valor = bio,
                    aoAlterar = { bio = it },
                    rotulo = "Sobre (opcional)",
                    placeholder = "Que tipo de jogo ele curte?",
                    singleLine = false,
                    minLines = 2
                )
                RotuloCampo("Avatar")
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
                when {
                    nomeLimpo.isBlank() -> erro = "Digite o nome do amigo."
                    nomesExistentes.any { it.equals(nomeLimpo, ignoreCase = true) } ->
                        erro = "Você já tem um amigo com esse nome."
                    else -> onAdicionar(nomeLimpo, avatar, bio.trim())
                }
            }) { Text("Adicionar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
