package com.example.letterbox_games.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
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
import com.example.letterbox_games.model.Jogo
import com.example.letterbox_games.model.comoNota
import com.example.letterbox_games.model.tempoAtras
import com.example.letterbox_games.ui.components.AvatarJogo
import com.example.letterbox_games.ui.components.AvatarPessoa
import com.example.letterbox_games.ui.components.CartaoEscuro
import com.example.letterbox_games.ui.components.ConfirmarDialog
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.EstrelasNota
import com.example.letterbox_games.ui.components.MiniEstatistica
import com.example.letterbox_games.ui.components.TelaComTopBar
import com.example.letterbox_games.ui.components.TituloSecao
import kotlin.math.abs

/**
 * TELA 6 — Detalhe do Amigo (DETALHE da LISTA 2). Recebe o amigo certo pelo id da rota.
 * Faz mais do que reexibir campos:
 *  - informações calculadas: nº de jogos, nota média que ele dá e plataforma favorita;
 *  - combina as duas listas: "jogos em comum" com os seus jogos, comparando as notas,
 *    e uma COMPATIBILIDADE de gosto calculada a partir da diferença entre as notas;
 *  - navegação secundária: cada jogo abre o Detalhe do Jogo;
 *  - permite remover o amigo.
 */
@Composable
fun DetalheAmigoScreen(
    amigo: Amigo?,
    atividadesDoAmigo: List<Atividade>,
    todosJogos: List<Jogo>,
    onVoltar: () -> Unit,
    onAbrirJogo: (String) -> Unit,
    onRemover: () -> Unit
) {
    var confirmarRemocao by remember { mutableStateOf(false) }

    // O amigo pode sumir por um instante logo depois de removido (a tela já está saindo).
    if (amigo == null) {
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    // Última nota dele em cada jogo (a lista já vem da mais recente para a mais antiga)
    val ultimas = atividadesDoAmigo.distinctBy { it.jogoId }
    val jogosDele = ultimas.mapNotNull { atividade ->
        todosJogos.firstOrNull { it.id == atividade.jogoId }?.let { it to atividade }
    }
    val notaMediaDele = if (ultimas.isEmpty()) null else ultimas.map { it.nota }.average()
    val plataformaFavorita = jogosDele
        .groupingBy { it.first.plataforma }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key

    // Jogos em comum com os meus jogos
    val emComum = jogosDele.filter { it.first.jogado }
    val compatibilidade = if (emComum.isEmpty()) {
        null
    } else {
        val diferencaMedia = emComum.map { abs(it.first.minhaNota - it.second.nota) }.average()
        (100 - diferencaMedia * 25).coerceIn(0.0, 100.0)
    }

    TelaComTopBar(
        titulo = amigo.nome,
        onVoltar = onVoltar,
        acoes = {
            IconButton(onClick = { confirmarRemocao = true }) {
                Icon(Icons.Filled.PersonRemove, contentDescription = "Remover amigo", tint = Cores.Perigo)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Cabeçalho
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarPessoa(emoji = amigo.avatar, tamanho = 72.dp)
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(amigo.nome, color = Cores.Texto, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        text = amigo.bio.ifBlank { "Sem descrição" },
                        color = Cores.Cinza,
                        fontSize = 13.sp
                    )
                }
            }

            // Estatísticas calculadas
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniEstatistica(Icons.Filled.SportsEsports, jogosDele.size.toString(), "Jogos", Modifier.weight(1f))
                MiniEstatistica(Icons.Filled.Star, notaMediaDele.comoNota(), "Nota média", Modifier.weight(1f))
                MiniEstatistica(Icons.Filled.Devices, plataformaFavorita ?: "—", "Plataforma", Modifier.weight(1f))
            }

            // Compatibilidade
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TituloSecao("Compatibilidade de gosto")
                CartaoEscuro(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (compatibilidade == null) {
                            Text(
                                "Vocês ainda não têm jogos em comum para comparar.",
                                color = Cores.Cinza,
                                fontSize = 13.sp
                            )
                        } else {
                            Text(
                                text = "${compatibilidade.toInt()}%",
                                color = Cores.Destaque,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            LinearProgressIndicator(
                                progress = { (compatibilidade / 100.0).toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                                color = Cores.Destaque,
                                trackColor = Cores.Borda
                            )
                            Text(
                                text = "Calculado pela diferença entre as notas de ${emComum.size} ${if (emComum.size == 1) "jogo" else "jogos"} em comum.",
                                color = Cores.Cinza,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Jogos em comum
            if (emComum.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TituloSecao("Jogos em comum")
                    emComum.forEach { (jogo, atividade) ->
                        CartaoEscuro(modifier = Modifier.fillMaxWidth(), onClick = { onAbrirJogo(jogo.id) }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarJogo(nome = jogo.nome, tamanho = 44.dp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = jogo.nome,
                                        color = Cores.Texto,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Você", color = Cores.Cinza, fontSize = 11.sp)
                                        EstrelasNota(nota = jogo.minhaNota, tamanho = 12.dp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(amigo.nome, color = Cores.Cinza, fontSize = 11.sp)
                                        EstrelasNota(nota = atividade.nota, tamanho = 12.dp)
                                    }
                                }
                                Icon(
                                    Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = Cores.Cinza,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Atividade recente
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TituloSecao("Atividade recente")
                if (atividadesDoAmigo.isEmpty()) {
                    Text("${amigo.nome} ainda não registrou nenhum jogo.", color = Cores.Cinza, fontSize = 13.sp)
                } else {
                    atividadesDoAmigo.forEach { atividade ->
                        val jogo = todosJogos.firstOrNull { it.id == atividade.jogoId }
                        if (jogo != null) {
                            CartaoEscuro(modifier = Modifier.fillMaxWidth(), onClick = { onAbrirJogo(jogo.id) }) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Jogou ${jogo.nome}",
                                            color = Cores.Texto,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(tempoAtras(atividade.quando), color = Cores.Cinza, fontSize = 11.sp)
                                        if (atividade.comentario.isNotBlank()) {
                                            Text("“${atividade.comentario}”", color = Cores.Cinza, fontSize = 12.sp)
                                        }
                                    }
                                    EstrelasNota(nota = atividade.nota, tamanho = 14.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (confirmarRemocao) {
        ConfirmarDialog(
            titulo = "Remover amigo?",
            texto = "${amigo.nome} sai da sua lista e a atividade dele some do seu feed.",
            onConfirmar = {
                confirmarRemocao = false
                onRemover()
            },
            onCancelar = { confirmarRemocao = false }
        )
    }
}
