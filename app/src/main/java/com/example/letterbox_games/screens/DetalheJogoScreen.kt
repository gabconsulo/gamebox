package com.example.letterbox_games.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letterbox_games.model.Amigo
import com.example.letterbox_games.model.Atividade
import com.example.letterbox_games.model.Jogo
import com.example.letterbox_games.model.comoMilhar
import com.example.letterbox_games.model.comoNota
import com.example.letterbox_games.model.tempoAtras
import com.example.letterbox_games.ui.components.AvatarJogo
import com.example.letterbox_games.ui.components.AvatarPessoa
import com.example.letterbox_games.ui.components.BadgeTendencia
import com.example.letterbox_games.ui.components.BotaoGradiente
import com.example.letterbox_games.ui.components.CartaoEscuro
import com.example.letterbox_games.ui.components.ChipOpcao
import com.example.letterbox_games.ui.components.ConfirmarDialog
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.EstrelasNota
import com.example.letterbox_games.ui.components.TelaComTopBar
import com.example.letterbox_games.ui.components.TituloSecao
import kotlin.math.roundToInt

private val labelsNota = mapOf(
    1 to "Ruim",
    2 to "Regular",
    3 to "Bom",
    4 to "Muito bom",
    5 to "Excelente"
)

/**
 * TELA 4 — Detalhe do Jogo (DETALHE da LISTA 1). Recebe o jogo certo pelo id da rota.
 * Mostra a descrição e a NOTA MÉDIA calculada (comunidade + amigos + você) e faz mais do que
 * reexibir campos:
 *  - se você ainda não jogou: escolhe a nota e marca como jogado ali mesmo;
 *  - se já jogou: muda a nota, ajusta as horas, registra uma sessão de +1h ou remove o jogo;
 *  - combina as duas listas: mostra quais amigos já jogaram (com a nota de cada um) e
 *    leva ao Detalhe do Amigo.
 */
@Composable
fun DetalheJogoScreen(
    jogo: Jogo?,
    notaMedia: Double?,
    amigosQueJogaram: List<Pair<Amigo, Atividade>>,
    onVoltar: () -> Unit,
    onAbrirAmigo: (String) -> Unit,
    onMarcarJogado: (nota: Int) -> Unit,
    onAtualizar: (nota: Int, horas: Int) -> Unit,
    onRegistrarSessao: () -> Unit,
    onRemover: () -> Unit
) {
    var notaNova by remember { mutableIntStateOf(5) }
    var confirmarRemocao by remember { mutableStateOf(false) }

    // O jogo pode sumir por um instante logo depois de removido (a tela já está saindo).
    if (jogo == null) {
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    val notaAmigos = amigosQueJogaram.map { it.second.nota }
    val votos = jogo.avaliacoes + notaAmigos.size + (if (jogo.jogado && jogo.minhaNota > 0) 1 else 0)

    TelaComTopBar(
        titulo = "Detalhe do jogo",
        onVoltar = onVoltar,
        acoes = {
            if (jogo.jogado) {
                IconButton(onClick = { confirmarRemocao = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remover dos meus jogos", tint = Cores.Perigo)
                }
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
                AvatarJogo(nome = jogo.nome, tamanho = 72.dp)
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(jogo.nome, color = Cores.Texto, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "${jogo.plataforma}  ·  ${jogo.genero}  ·  ${jogo.ano}",
                        color = Cores.Cinza,
                        fontSize = 12.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        jogo.tendencia?.let { BadgeTendencia(it) }
                        if (jogo.jogado) {
                            Text("✓ Jogado", color = Cores.Sucesso, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Descrição
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TituloSecao("Sobre o jogo")
                CartaoEscuro(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = jogo.descricao.ifBlank { "Este jogo ainda não tem descrição." },
                        color = if (jogo.descricao.isBlank()) Cores.Cinza else Cores.Texto,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Nota média
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TituloSecao("Nota média")
                CartaoEscuro(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = notaMedia.comoNota(),
                                color = Cores.Destaque,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.width(14.dp))
                            Column {
                                EstrelasNota(nota = notaMedia?.roundToInt() ?: 0, tamanho = 20.dp)
                                Text(
                                    text = when (votos) {
                                        0 -> "Sem avaliações ainda"
                                        1 -> "1 avaliação"
                                        else -> "${votos.comoMilhar()} avaliações"
                                    },
                                    color = Cores.Cinza,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            ResumoNota(
                                rotulo = "Comunidade",
                                valor = if (jogo.avaliacoes > 0) jogo.notaComunidade.comoNota() else "—"
                            )
                            ResumoNota(
                                rotulo = "Amigos",
                                valor = if (notaAmigos.isEmpty()) "—" else notaAmigos.average().comoNota()
                            )
                            ResumoNota(
                                rotulo = "Você",
                                valor = if (jogo.jogado && jogo.minhaNota > 0) jogo.minhaNota.toString() else "—"
                            )
                        }
                    }
                }
            }

            // Seu registro (parte funcional)
            if (!jogo.jogado) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TituloSecao("Você já jogou?")
                    CartaoEscuro(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = labelsNota[notaNova] ?: "",
                                    color = Cores.Destaque,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                EstrelasNota(nota = notaNova, tamanho = 26.dp, aoSelecionar = { notaNova = it })
                            }
                            BotaoGradiente(
                                texto = "Marcar como jogado",
                                onClick = { onMarcarJogado(notaNova) },
                                modifier = Modifier.fillMaxWidth(),
                                icone = Icons.Filled.Add
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TituloSecao("Seu registro")
                    CartaoEscuro(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = labelsNota[jogo.minhaNota] ?: "Toque nas estrelas",
                                    color = Cores.Destaque,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                EstrelasNota(
                                    nota = jogo.minhaNota,
                                    tamanho = 26.dp,
                                    aoSelecionar = { onAtualizar(it, jogo.horas) }
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    if (jogo.horas > 0) onAtualizar(jogo.minhaNota, jogo.horas - 1)
                                }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Menos uma hora", tint = Cores.Destaque)
                                }
                                Text(
                                    text = "${jogo.horas} h",
                                    color = Cores.Texto,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(onClick = { onAtualizar(jogo.minhaNota, jogo.horas + 1) }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Mais uma hora", tint = Cores.Destaque)
                                }
                                Spacer(Modifier.weight(1f))
                                ChipOpcao(
                                    texto = "+10 h",
                                    selecionado = false,
                                    onClick = { onAtualizar(jogo.minhaNota, jogo.horas + 10) }
                                )
                            }
                            if (jogo.resenha.isNotBlank()) {
                                Text("“${jogo.resenha}”", color = Cores.Cinza, fontSize = 13.sp)
                            }
                            BotaoGradiente(
                                texto = "Registrar sessão agora (+1 h)",
                                onClick = onRegistrarSessao,
                                modifier = Modifier.fillMaxWidth(),
                                icone = Icons.Filled.PlayArrow
                            )
                        }
                    }
                }
            }

            // Amigos que jogaram (combina as duas listas)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TituloSecao("Amigos que jogaram")
                if (amigosQueJogaram.isEmpty()) {
                    Text("Nenhum amigo seu registrou esse jogo ainda.", color = Cores.Cinza, fontSize = 13.sp)
                } else {
                    amigosQueJogaram.forEach { (amigo, atividade) ->
                        CartaoEscuro(modifier = Modifier.fillMaxWidth(), onClick = { onAbrirAmigo(amigo.id) }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarPessoa(emoji = amigo.avatar, tamanho = 40.dp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(amigo.nome, color = Cores.Texto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(tempoAtras(atividade.quando), color = Cores.Cinza, fontSize = 11.sp)
                                }
                                EstrelasNota(nota = atividade.nota, tamanho = 14.dp)
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
        }
    }

    if (confirmarRemocao) {
        ConfirmarDialog(
            titulo = "Remover dos meus jogos?",
            texto = "${jogo.nome} sai dos seus jogos jogados. Sua nota, suas horas e sua resenha serão apagadas.",
            onConfirmar = {
                confirmarRemocao = false
                onRemover()
            },
            onCancelar = { confirmarRemocao = false }
        )
    }
}

@Composable
private fun ResumoNota(rotulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, color = Cores.Texto, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(rotulo.uppercase(), color = Cores.Cinza, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
