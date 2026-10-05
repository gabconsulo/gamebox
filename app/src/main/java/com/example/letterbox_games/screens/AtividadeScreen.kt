package com.example.letterbox_games.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.letterbox_games.model.ItemFeed
import com.example.letterbox_games.model.UM_DIA_EM_MS
import com.example.letterbox_games.model.tempoAtras
import com.example.letterbox_games.ui.components.AvatarPessoa
import com.example.letterbox_games.ui.components.CabecalhoTela
import com.example.letterbox_games.ui.components.CartaoEscuro
import com.example.letterbox_games.ui.components.ChipOpcao
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.EstadoVazio
import com.example.letterbox_games.ui.components.EstrelasNota
import com.example.letterbox_games.ui.components.FundoGradiente
import com.example.letterbox_games.ui.components.MiniEstatistica

private enum class FiltroAutor(val rotulo: String) {
    TODOS("Todos"),
    VOCE("Você"),
    AMIGOS("Amigos")
}

private enum class Periodo(val rotulo: String, val dias: Int?) {
    SEMANA("7 dias", 7),
    MES("30 dias", 30),
    TUDO("Tudo", null)
}

/**
 * TELA 7 — Atividade: o que você e seus amigos jogaram ultimamente, do mais recente ao mais antigo.
 * - Filtros por quem jogou (todos, você, amigos) e por período (7 dias, 30 dias, tudo).
 * - Resumo da semana calculado a partir dos dados.
 * - Botão de curtir em cada atividade.
 * - Nas suas atividades, "+1 h" registra uma nova sessão agora; nas dos amigos, tocar no avatar
 *   abre o Detalhe do Amigo.
 * - Tocar no card abre o Detalhe do Jogo.
 */
@Composable
fun AtividadeScreen(
    itens: List<ItemFeed>,
    curtidas: List<String>,
    onCurtir: (String) -> Unit,
    onAbrirJogo: (String) -> Unit,
    onAbrirAmigo: (String) -> Unit,
    onRegistrarSessao: (String) -> Unit
) {
    var autor by remember { mutableStateOf(FiltroAutor.TODOS) }
    var periodo by remember { mutableStateOf(Periodo.TUDO) }

    val agora = System.currentTimeMillis()
    val naSemana = itens.filter { agora - it.quando <= 7 * UM_DIA_EM_MS }
    val minhasNaSemana = naSemana.count { it.ehVoce }
    val amigosAtivos = naSemana.filter { !it.ehVoce }.map { it.amigoId }.distinct().size

    val filtrados = itens.filter { item ->
        val passaAutor = when (autor) {
            FiltroAutor.TODOS -> true
            FiltroAutor.VOCE -> item.ehVoce
            FiltroAutor.AMIGOS -> !item.ehVoce
        }
        val limite = periodo.dias
        val passaPeriodo = limite == null || agora - item.quando <= limite * UM_DIA_EM_MS
        passaAutor && passaPeriodo
    }

    FundoGradiente {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                CabecalhoTela(
                    sobretitulo = "FEED",
                    titulo = "Atividade",
                    subtitulo = "O que você e seus amigos jogaram"
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniEstatistica(Icons.Filled.PlayArrow, minhasNaSemana.toString(), "Jogos na semana", Modifier.weight(1f))
                    MiniEstatistica(Icons.Filled.Favorite, amigosAtivos.toString(), "Amigos ativos", Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroAutor.entries.forEach { opcao ->
                        ChipOpcao(opcao.rotulo, autor == opcao, { autor = opcao })
                    }
                    Spacer(Modifier.width(8.dp))
                    Periodo.entries.forEach { opcao ->
                        ChipOpcao(opcao.rotulo, periodo == opcao, { periodo = opcao })
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filtrados.isEmpty()) {
                    item {
                        EstadoVazio(
                            titulo = "Nenhuma atividade",
                            texto = "Nada encontrado com esses filtros. Tente ampliar o período."
                        )
                    }
                } else {
                    items(filtrados, key = { it.id }) { item ->
                        AtividadeCard(
                            item = item,
                            curtido = item.id in curtidas,
                            onCurtir = { onCurtir(item.id) },
                            onAbrirJogo = { onAbrirJogo(item.jogo.id) },
                            onAbrirAmigo = { item.amigoId?.let(onAbrirAmigo) },
                            onRegistrarSessao = { onRegistrarSessao(item.jogo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AtividadeCard(
    item: ItemFeed,
    curtido: Boolean,
    onCurtir: () -> Unit,
    onAbrirJogo: () -> Unit,
    onAbrirAmigo: () -> Unit,
    onRegistrarSessao: () -> Unit
) {
    CartaoEscuro(modifier = Modifier.fillMaxWidth(), onClick = onAbrirJogo) {
        Column(modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 8.dp, bottom = 4.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = if (item.ehVoce) Modifier else Modifier.clickable(onClick = onAbrirAmigo)) {
                    AvatarPessoa(emoji = item.avatar, tamanho = 44.dp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${if (item.ehVoce) "Você" else item.autor} jogou ${item.jogo.nome}",
                        color = Cores.Texto,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EstrelasNota(nota = item.nota, tamanho = 13.dp)
                        Text(tempoAtras(item.quando), color = Cores.Cinza, fontSize = 11.sp)
                    }
                    if (item.comentario.isNotBlank()) {
                        Text("“${item.comentario}”", color = Cores.Cinza, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onCurtir) {
                    Icon(
                        imageVector = if (curtido) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (curtido) "Descurtir" else "Curtir",
                        tint = if (curtido) Cores.Perigo else Cores.Cinza
                    )
                }
                if (item.ehVoce) {
                    TextButton(onClick = onRegistrarSessao) {
                        Text("+1 h agora", color = Cores.Destaque, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    TextButton(onClick = onAbrirAmigo) {
                        Text("Ver perfil", color = Cores.Destaque, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
