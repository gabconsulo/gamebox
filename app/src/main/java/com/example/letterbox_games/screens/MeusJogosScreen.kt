package com.example.letterbox_games.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letterbox_games.model.Jogo
import com.example.letterbox_games.ui.components.AvatarJogo
import com.example.letterbox_games.ui.components.BotaoRedondo
import com.example.letterbox_games.ui.components.CabecalhoTela
import com.example.letterbox_games.ui.components.CampoTexto
import com.example.letterbox_games.ui.components.CartaoEscuro
import com.example.letterbox_games.ui.components.ChipOpcao
import com.example.letterbox_games.ui.components.ConfirmarDialog
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.EstadoVazio
import com.example.letterbox_games.ui.components.EstrelasNota
import com.example.letterbox_games.ui.components.FundoGradiente

private enum class Ordem(val rotulo: String) {
    RECENTES("Recentes"),
    ALFABETICA("A–Z"),
    NOTA("Nota")
}

/**
 * TELA 2 — Meus Jogos (LISTA 1, data class Jogo): os jogos que você já jogou.
 * - Botões de ordenação no topo da lista: recentes, ordem alfabética e nota.
 * - Cada card tem um botão de lápis (abre um popup para editar) e uma lixeira (remove o jogo).
 * - Tocar no card abre o Detalhe do Jogo.
 * - O "+" pequeno no cabeçalho abre a tela Adicionar Jogo.
 */
@Composable
fun MeusJogosScreen(
    jogos: List<Jogo>,
    onCarregar: () -> Unit,
    onAdicionar: () -> Unit,
    onAbrirDetalhe: (String) -> Unit,
    onSalvarEdicao: (id: String, nota: Int, horas: Int, resenha: String) -> Unit,
    onRemover: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        onCarregar()
    }

    var ordem by remember { mutableStateOf(Ordem.RECENTES) }
    var jogoEmEdicao by remember { mutableStateOf<Jogo?>(null) }
    var jogoParaRemover by remember { mutableStateOf<Jogo?>(null) }

    val ordenados = when (ordem) {
        Ordem.RECENTES -> jogos.sortedByDescending { it.jogadoEm ?: 0L }
        Ordem.ALFABETICA -> jogos.sortedBy { it.nome.lowercase() }
        Ordem.NOTA -> jogos.sortedWith(compareByDescending<Jogo> { it.minhaNota }.thenBy { it.nome.lowercase() })
    }

    FundoGradiente {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                CabecalhoTela(
                    sobretitulo = "BIBLIOTECA",
                    titulo = "Meus jogos",
                    subtitulo = "${jogos.size} ${if (jogos.size == 1) "jogo jogado" else "jogos jogados"}",
                    acao = { BotaoRedondo(Icons.Filled.Add, "Adicionar jogo", onAdicionar) }
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ORDENAR", color = Cores.Cinza, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Ordem.entries.forEach { opcao ->
                        ChipOpcao(opcao.rotulo, ordem == opcao, { ordem = opcao })
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (ordenados.isEmpty()) {
                    item {
                        EstadoVazio(
                            titulo = "Sua biblioteca está vazia",
                            texto = "Toque no + para registrar um jogo que você já jogou."
                        )
                    }
                } else {
                    items(ordenados, key = { it.id }) { jogo ->
                        JogoCard(
                            jogo = jogo,
                            onClick = { onAbrirDetalhe(jogo.id) },
                            onEditar = { jogoEmEdicao = jogo },
                            onRemover = { jogoParaRemover = jogo }
                        )
                    }
                }
            }
        }
    }

    jogoEmEdicao?.let { jogo ->
        EditarJogoDialog(
            jogo = jogo,
            onSalvar = { nota, horas, resenha ->
                onSalvarEdicao(jogo.id, nota, horas, resenha)
                jogoEmEdicao = null
            },
            onCancelar = { jogoEmEdicao = null }
        )
    }

    jogoParaRemover?.let { jogo ->
        ConfirmarDialog(
            titulo = "Remover jogo?",
            texto = "${jogo.nome} sai dos seus jogos jogados. Sua nota, suas horas e sua resenha serão apagadas.",
            onConfirmar = {
                onRemover(jogo.id)
                jogoParaRemover = null
            },
            onCancelar = { jogoParaRemover = null }
        )
    }
}

@Composable
private fun JogoCard(
    jogo: Jogo,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onRemover: () -> Unit
) {
    CartaoEscuro(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarJogo(nome = jogo.nome)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = jogo.nome,
                    color = Cores.Texto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${jogo.plataforma}  ·  ${jogo.genero}",
                    color = Cores.Destaque,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EstrelasNota(nota = jogo.minhaNota, tamanho = 13.dp)
                    if (jogo.horas > 0) {
                        Text("${jogo.horas} h", color = Cores.Cinza, fontSize = 11.sp)
                    }
                }
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar ${jogo.nome}", tint = Cores.Destaque)
            }
            IconButton(onClick = onRemover) {
                Icon(Icons.Filled.Delete, contentDescription = "Remover ${jogo.nome}", tint = Cores.Perigo)
            }
        }
    }
}

/** Popup de edição: nota, horas jogadas e resenha. */
@Composable
private fun EditarJogoDialog(
    jogo: Jogo,
    onSalvar: (nota: Int, horas: Int, resenha: String) -> Unit,
    onCancelar: () -> Unit
) {
    var nota by remember(jogo.id) { mutableIntStateOf(jogo.minhaNota) }
    var horas by remember(jogo.id) { mutableStateOf(if (jogo.horas > 0) jogo.horas.toString() else "") }
    var resenha by remember(jogo.id) { mutableStateOf(jogo.resenha) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar ${jogo.nome}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Sua nota", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                EstrelasNota(nota = nota, tamanho = 30.dp, aoSelecionar = { nota = it })
                CampoTexto(
                    valor = horas,
                    aoAlterar = { horas = it.filter { c -> c.isDigit() }.take(4) },
                    rotulo = "Horas jogadas",
                    teclado = KeyboardType.Number
                )
                CampoTexto(
                    valor = resenha,
                    aoAlterar = { resenha = it },
                    rotulo = "Resenha",
                    singleLine = false,
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSalvar(nota, horas.toIntOrNull() ?: 0, resenha.trim()) },
                enabled = nota > 0
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
