package com.example.letterbox_games.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.letterbox_games.model.Tendencia
import com.example.letterbox_games.ui.theme.LocalPaleta

/**
 * Cores do app. Cada propriedade lê a paleta atual (escura ou clara), então só pode ser
 * usada dentro de funções @Composable. Trocar o modo no Perfil atualiza tudo automaticamente.
 */
object Cores {
    val FundoTopo: Color @Composable get() = LocalPaleta.current.fundoTopo
    val FundoBase: Color @Composable get() = LocalPaleta.current.fundoBase
    val Barra: Color @Composable get() = LocalPaleta.current.barra
    val Cartao: Color @Composable get() = LocalPaleta.current.cartao
    val Borda: Color @Composable get() = LocalPaleta.current.borda
    val Texto: Color @Composable get() = LocalPaleta.current.texto
    val Cinza: Color @Composable get() = LocalPaleta.current.cinza
    val CinzaEscuro: Color @Composable get() = LocalPaleta.current.cinzaEscuro
    val Destaque: Color @Composable get() = LocalPaleta.current.destaque
    val SobreDestaque: Color @Composable get() = LocalPaleta.current.sobreDestaque
    val ChipSelecionado: Color @Composable get() = LocalPaleta.current.chipSelecionado
    val Roxo: Color @Composable get() = LocalPaleta.current.roxo
    val GradienteFim: Color @Composable get() = LocalPaleta.current.gradienteFim
    val Estrela: Color @Composable get() = LocalPaleta.current.estrela
    val EstrelaVazia: Color @Composable get() = LocalPaleta.current.estrelaVazia
    val Perigo: Color @Composable get() = LocalPaleta.current.perigo
    val Sucesso: Color @Composable get() = LocalPaleta.current.sucesso
}

@Composable
fun FundoGradiente(modifier: Modifier = Modifier, conteudo: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Cores.FundoTopo, Cores.FundoBase))),
        content = conteudo
    )
}

/** Estrutura das telas "secundárias": fundo + Scaffold + TopAppBar com botão de voltar. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaComTopBar(
    titulo: String,
    onVoltar: () -> Unit,
    acoes: @Composable RowScope.() -> Unit = {},
    conteudo: @Composable (PaddingValues) -> Unit
) {
    FundoGradiente {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(titulo, fontWeight = FontWeight.Bold, maxLines = 1) },
                    navigationIcon = {
                        IconButton(onClick = onVoltar) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    actions = acoes,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Cores.Barra,
                        titleContentColor = Cores.Texto,
                        navigationIconContentColor = Cores.Texto,
                        actionIconContentColor = Cores.Texto
                    )
                )
            },
            content = conteudo
        )
    }
}

@Composable
fun CartaoEscuro(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(16.dp)
    Card(
        modifier = modifier
            .clip(forma)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = forma,
        colors = CardDefaults.cardColors(containerColor = Cores.Cartao),
        border = BorderStroke(1.dp, Cores.Borda),
        content = conteudo
    )
}

@Composable
fun BotaoGradiente(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icone: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(listOf(Cores.Roxo, Cores.GradienteFim)),
                    RoundedCornerShape(16.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icone != null) {
                Icon(icone, contentDescription = null, tint = Cores.SobreDestaque)
                Spacer(Modifier.width(8.dp))
            }
            Text(texto, color = Cores.SobreDestaque, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Botão redondo pequeno com o gradiente do app (usado no "+" dos cabeçalhos). */
@Composable
fun BotaoRedondo(icone: ImageVector, descricao: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Brush.horizontalGradient(listOf(Cores.Roxo, Cores.GradienteFim)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icone, contentDescription = descricao, tint = Cores.SobreDestaque)
    }
}

@Composable
fun CampoTexto(
    valor: String,
    aoAlterar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    teclado: KeyboardType = KeyboardType.Text,
    erro: String? = null,
    iconeInicial: ImageVector? = null
) {
    val iconeComposable: (@Composable () -> Unit)? =
        if (iconeInicial != null) { { Icon(iconeInicial, contentDescription = null) } } else null
    val erroComposable: (@Composable () -> Unit)? =
        if (erro != null) { { Text(erro) } } else null

    OutlinedTextField(
        value = valor,
        onValueChange = aoAlterar,
        modifier = modifier.fillMaxWidth(),
        label = { Text(rotulo) },
        placeholder = { if (placeholder != null) Text(placeholder) },
        leadingIcon = iconeComposable,
        supportingText = erroComposable,
        isError = erro != null,
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Cores.Texto,
            unfocusedTextColor = Cores.Texto,
            errorTextColor = Cores.Texto,
            focusedContainerColor = Cores.Cartao,
            unfocusedContainerColor = Cores.Cartao,
            errorContainerColor = Cores.Cartao,
            cursorColor = Cores.Destaque,
            focusedBorderColor = Cores.Destaque,
            unfocusedBorderColor = Cores.Borda,
            errorBorderColor = Cores.Perigo,
            focusedLabelColor = Cores.Destaque,
            unfocusedLabelColor = Cores.Cinza,
            errorLabelColor = Cores.Perigo,
            focusedPlaceholderColor = Cores.CinzaEscuro,
            unfocusedPlaceholderColor = Cores.CinzaEscuro,
            focusedLeadingIconColor = Cores.Destaque,
            unfocusedLeadingIconColor = Cores.Cinza,
            errorLeadingIconColor = Cores.Perigo,
            errorSupportingTextColor = Cores.Perigo
        )
    )
}

@Composable
fun ChipOpcao(
    texto: String,
    selecionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .clip(forma)
            .background(if (selecionado) Cores.ChipSelecionado else Cores.Cartao)
            .border(1.dp, if (selecionado) Cores.Destaque else Cores.Borda, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = if (selecionado) Cores.Destaque else Cores.Cinza,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Estrelas de 1 a 5. Se aoSelecionar for informado, tocar numa estrela muda a nota. */
@Composable
fun EstrelasNota(nota: Int, tamanho: Dp = 16.dp, aoSelecionar: ((Int) -> Unit)? = null) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= nota) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Nota $i",
                tint = if (i <= nota) Cores.Estrela else Cores.EstrelaVazia,
                modifier = Modifier
                    .size(tamanho)
                    .then(if (aoSelecionar != null) Modifier.clickable { aoSelecionar(i) } else Modifier)
            )
        }
    }
}

/** Etiqueta colorida que explica por que o jogo está em tendência. */
@Composable
fun BadgeTendencia(tendencia: Tendencia) {
    val cor = when (tendencia) {
        Tendencia.EM_ALTA -> Color(0xFFFF7A45)
        Tendencia.VOLTOU -> Cores.Roxo
        Tendencia.LANCAMENTO -> Cores.Destaque
        Tendencia.SEMPRE_EM_ALTA -> Cores.Estrela
    }
    Text(
        text = "${tendencia.emoji} ${tendencia.rotulo}",
        color = cor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(cor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/** Quadradinho colorido com as iniciais do jogo (a cor depende do nome). */
@Composable
fun AvatarJogo(nome: String, tamanho: Dp = 48.dp) {
    val paleta = listOf(Color(0xFF00B8A9), Color(0xFF3B82F6), Color(0xFFE76F51), Color(0xFF8B5CF6))
    val cor = paleta[nome.hashCode().ushr(1) % paleta.size]
    val iniciais = nome.split(Regex("\\s+")).take(3).mapNotNull { it.firstOrNull() }.joinToString("").uppercase()
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.horizontalGradient(listOf(cor, cor.copy(alpha = 0.65f)))),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciais, color = Color.White, fontSize = (tamanho.value * 0.29f).sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** Círculo com o emoji escolhido como avatar de uma pessoa. */
@Composable
fun AvatarPessoa(emoji: String, tamanho: Dp = 48.dp) {
    Box(
        modifier = Modifier
            .size(tamanho)
            .clip(CircleShape)
            .background(Cores.Roxo.copy(alpha = 0.22f))
            .border(1.dp, Cores.Roxo.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (tamanho.value * 0.5f).sp)
    }
}

@Composable
fun CabecalhoTela(
    sobretitulo: String,
    titulo: String,
    subtitulo: String? = null,
    acao: (@Composable () -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(sobretitulo, color = Cores.Destaque, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(titulo, color = Cores.Texto, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            if (subtitulo != null) {
                Text(subtitulo, color = Cores.Cinza, fontSize = 13.sp)
            }
        }
        if (acao != null) acao()
    }
}

@Composable
fun TituloSecao(texto: String) {
    Text(texto, color = Cores.Texto, fontSize = 18.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun RotuloCampo(texto: String) {
    Text(texto.uppercase(), color = Cores.Cinza, fontSize = 12.sp, fontWeight = FontWeight.Medium)
}

@Composable
fun EstadoVazio(titulo: String, texto: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo, color = Cores.Texto, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(texto, color = Cores.Cinza, fontSize = 13.sp)
    }
}

/** Cartãozinho com ícone + valor + rótulo (usado em Perfil, Detalhe do Amigo etc.). */
@Composable
fun MiniEstatistica(icone: ImageVector, valor: String, rotulo: String, modifier: Modifier = Modifier) {
    CartaoEscuro(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Cores.Destaque.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icone, contentDescription = null, tint = Cores.Destaque, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(valor, color = Cores.Texto, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(2.dp))
            Text(rotulo.uppercase(), color = Cores.Cinza, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ConfirmarDialog(
    titulo: String,
    texto: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    textoConfirmar: String = "Remover"
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(texto) },
        confirmButton = {
            TextButton(onClick = onConfirmar) { Text(textoConfirmar, color = Cores.Perigo) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
