package com.example.letterbox_games.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letterbox_games.ui.components.BotaoGradiente
import com.example.letterbox_games.ui.components.CampoTexto
import com.example.letterbox_games.ui.components.ChipOpcao
import com.example.letterbox_games.ui.components.Cores
import com.example.letterbox_games.ui.components.EstrelasNota
import com.example.letterbox_games.ui.components.RotuloCampo
import com.example.letterbox_games.ui.components.TelaComTopBar

private val plataformas = listOf("PC", "PS5", "Switch", "Xbox", "Mobile")
private val labelsNota = mapOf(
    1 to "Ruim",
    2 to "Regular",
    3 to "Bom",
    4 to "Muito bom",
    5 to "Excelente"
)

/**
 * TELA 3 — Adicionar Jogo (formulário da LISTA 1).
 * Campos: nome (obrigatório), plataforma, horas jogadas (numérico), nota (estrelas) e resenha (várias linhas).
 * Se já existir um jogo com o mesmo nome no catálogo, ele é apenas marcado como jogado.
 */
@Composable
fun AdicionarJogoScreen(
    nomesJaJogados: List<String>,
    onVoltar: () -> Unit,
    onSalvar: (nome: String, plataforma: String, nota: Int, horas: Int, resenha: String) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var plataforma by remember { mutableStateOf("PC") }
    var horas by remember { mutableStateOf("") }
    var nota by remember { mutableIntStateOf(5) }
    var resenha by remember { mutableStateOf("") }
    var erroNome by remember { mutableStateOf<String?>(null) }

    TelaComTopBar(titulo = "Adicionar jogo", onVoltar = onVoltar) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CampoTexto(
                valor = nome,
                aoAlterar = {
                    nome = it
                    if (it.isNotBlank()) erroNome = null
                },
                rotulo = "Nome do jogo",
                placeholder = "Digite o nome do jogo...",
                erro = erroNome
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RotuloCampo("Plataforma")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    plataformas.forEach { opcao ->
                        ChipOpcao(opcao, plataforma == opcao, { plataforma = opcao })
                    }
                }
            }

            CampoTexto(
                valor = horas,
                aoAlterar = { horas = it.filter { c -> c.isDigit() }.take(4) },
                rotulo = "Horas jogadas",
                placeholder = "0",
                teclado = KeyboardType.Number
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RotuloCampo("Sua nota")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Cores.Cartao)
                        .border(1.dp, Cores.Borda, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = labelsNota[nota] ?: "",
                        color = Cores.Destaque,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    EstrelasNota(nota = nota, tamanho = 24.dp, aoSelecionar = { nota = it })
                }
            }

            CampoTexto(
                valor = resenha,
                aoAlterar = { resenha = it },
                rotulo = "Resenha curta (opcional)",
                placeholder = "O que achou do jogo?",
                singleLine = false,
                minLines = 4
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onVoltar,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Cancelar")
                }
                BotaoGradiente(
                    texto = "Salvar jogo",
                    onClick = {
                        val nomeLimpo = nome.trim()
                        if (nomeLimpo.isBlank()) {
                            erroNome = "Digite o nome do jogo para continuar."
                        } else if (nomesJaJogados.any { it.equals(nomeLimpo, ignoreCase = true) }) {
                            erroNome = "Você já registrou esse jogo. Edite-o em Meus jogos."
                        } else {
                            onSalvar(nomeLimpo, plataforma, nota, horas.toIntOrNull() ?: 0, resenha.trim())
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
