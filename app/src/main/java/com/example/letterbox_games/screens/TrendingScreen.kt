package com.example.letterbox_games.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letterbox_games.model.Jogo
import com.example.letterbox_games.model.Tendencia
import com.example.letterbox_games.model.comoMilhar
import com.example.letterbox_games.model.comoNota
import com.example.letterbox_games.ui.components.AvatarJogo
import com.example.letterbox_games.ui.components.BadgeTendencia
import com.example.letterbox_games.ui.components.CabecalhoTela
import com.example.letterbox_games.ui.components.CartaoEscuro
import com.example.letterbox_games.ui.components.ChipOpcao
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.EstadoVazio
import com.example.letterbox_games.ui.components.EstrelasNota
import com.example.letterbox_games.ui.components.FundoGradiente
import com.example.letterbox_games.ui.components.TituloSecao
import kotlin.math.roundToInt

/**
 * TELA 1 — Trending. Mostra os jogos do momento: os que estão em alta, os que voltaram,
 * os recém-lançados no hype e os que nunca saem da lista.
 * - Os chips filtram pelo motivo da tendência.
 * - "Hype da semana" (carrossel) mostra os 3 jogos que mais cresceram.
 * - O botão "Ver detalhes" (e o toque no card) abre a tela de Detalhe do Jogo.
 */
@Composable
fun TrendingScreen(
    jogosEmAlta: List<Jogo>,
    onAbrirDetalhe: (String) -> Unit
) {
    var filtro by remember { mutableStateOf<Tendencia?>(null) }
    val filtrados = jogosEmAlta.filter { filtro == null || it.tendencia == filtro }
    val hype = jogosEmAlta.take(3)

    FundoGradiente {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                CabecalhoTela(
                    sobretitulo = "AGORA NO RADAR",
                    titulo = "Trending",
                    subtitulo = "Os jogos do momento"
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChipOpcao("Todos", filtro == null, { filtro = null })
                    Tendencia.entries.forEach { tendencia ->
                        ChipOpcao(
                            texto = "${tendencia.emoji} ${tendencia.rotulo}",
                            selecionado = filtro == tendencia,
                            onClick = { filtro = if (filtro == tendencia) null else tendencia }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filtro == null && hype.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            TituloSecao("Hype da semana")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(hype, key = { it.id }) { jogo ->
                                    CartaoHype(jogo = jogo, onClick = { onAbrirDetalhe(jogo.id) })
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            TituloSecao("Todos em tendência")
                        }
                    }
                }

                if (filtrados.isEmpty()) {
                    item {
                        EstadoVazio(
                            titulo = "Nada por aqui",
                            texto = "Nenhum jogo nessa categoria no momento."
                        )
                    }
                } else {
                    itemsIndexed(filtrados, key = { _, jogo -> jogo.id }) { indice, jogo ->
                        TrendingCard(
                            posicao = indice + 1,
                            jogo = jogo,
                            onAbrirDetalhe = { onAbrirDetalhe(jogo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoHype(jogo: Jogo, onClick: () -> Unit) {
    CartaoEscuro(modifier = Modifier.width(250.dp), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            jogo.tendencia?.let { BadgeTendencia(it) }
            Text(
                text = jogo.nome,
                color = Cores.Texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = Cores.Sucesso, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "+${jogo.variacaoJogadores}% de jogadores",
                    color = Cores.Sucesso,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = jogo.descricao,
                color = Cores.Cinza,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TrendingCard(posicao: Int, jogo: Jogo, onAbrirDetalhe: () -> Unit) {
    CartaoEscuro(modifier = Modifier.fillMaxWidth(), onClick = onAbrirDetalhe) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#$posicao",
                    color = Cores.Destaque,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.width(36.dp)
                )
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
                        text = "${jogo.plataforma}  ·  ${jogo.genero}  ·  ${jogo.ano}",
                        color = Cores.Cinza,
                        fontSize = 11.sp
                    )
                    jogo.tendencia?.let { BadgeTendencia(it) }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        EstrelasNota(nota = jogo.notaComunidade.roundToInt(), tamanho = 14.dp)
                        Text(
                            text = "${jogo.notaComunidade.comoNota()} (${jogo.avaliacoes.comoMilhar()})",
                            color = Cores.Cinza,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = if (jogo.jogado) "✓ Você já jogou" else "▲ ${jogo.variacaoJogadores}% de jogadores",
                        color = if (jogo.jogado) Cores.Destaque else Cores.Sucesso,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = onAbrirDetalhe,
                    border = BorderStroke(1.dp, Cores.Destaque),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Cores.Destaque),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver detalhes", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
