package com.example.letterbox_games.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.letterbox_games.screens.AdicionarJogoScreen
import com.example.letterbox_games.screens.AmigosScreen
import com.example.letterbox_games.screens.AtividadeScreen
import com.example.letterbox_games.screens.DetalheAmigoScreen
import com.example.letterbox_games.screens.DetalheJogoScreen
import com.example.letterbox_games.screens.MeusJogosScreen
import com.example.letterbox_games.screens.PerfilScreen
import com.example.letterbox_games.screens.TrendingScreen
import com.example.letterbox_games.state.EstadoApp
import com.example.letterbox_games.ui.components.Cores

/** Todas as rotas do app em um só lugar. Rotas com {id} recebem o id do item por argumento. */
object Rotas {
    // Áreas principais (aparecem na barra inferior)
    const val TRENDING = "trending"
    const val MEUS_JOGOS = "meusJogos"
    const val AMIGOS = "amigos"
    const val ATIVIDADE = "atividade"
    const val PERFIL = "perfil"

    // Telas secundárias
    const val ADICIONAR_JOGO = "adicionarJogo"
    const val DETALHE_JOGO = "detalheJogo/{id}"
    const val DETALHE_AMIGO = "detalheAmigo/{id}"

    // Funções que montam a rota com o id de verdade
    fun detalheJogo(id: String) = "detalheJogo/$id"
    fun detalheAmigo(id: String) = "detalheAmigo/$id"
}

private data class ItemBarra(val rota: String, val rotulo: String, val icone: ImageVector)

private val itensBarra = listOf(
    ItemBarra(Rotas.TRENDING, "Trending", Icons.Filled.LocalFireDepartment),
    ItemBarra(Rotas.MEUS_JOGOS, "Jogos", Icons.Filled.SportsEsports),
    ItemBarra(Rotas.AMIGOS, "Amigos", Icons.Filled.Group),
    ItemBarra(Rotas.ATIVIDADE, "Atividade", Icons.Filled.Timeline),
    ItemBarra(Rotas.PERFIL, "Perfil", Icons.Filled.Person)
)

/** Troca de aba da barra inferior sem empilhar telas repetidas e guardando o estado de cada aba. */
private fun NavHostController.irParaAreaPrincipal(rota: String) {
    navigate(rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation(estado: EstadoApp) {
    val navController = rememberNavController()
    val modoEscuro = estado.modoEscuro ?: isSystemInDarkTheme()

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route
    val mostrarBarra = itensBarra.any { it.rota == rotaAtual }

    Scaffold(
        containerColor = Cores.FundoTopo,
        // Cada tela cuida dos próprios insets (statusBarsPadding ou TopAppBar).
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(
                    rotaAtual = rotaAtual,
                    aoSelecionar = { navController.irParaAreaPrincipal(it) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.TRENDING,
            modifier = Modifier.padding(padding)
        ) {
            // 1) Trending
            composable(Rotas.TRENDING) {
                TrendingScreen(
                    jogosEmAlta = estado.jogosEmAlta,
                    onAbrirDetalhe = { navController.navigate(Rotas.detalheJogo(it)) }
                )
            }

            // 2) Meus Jogos
            composable(Rotas.MEUS_JOGOS) {
                MeusJogosScreen(
                    jogos = estado.meusJogos,
                    onCarregar = { estado.carregarJogos() },
                    onAdicionar = { navController.navigate(Rotas.ADICIONAR_JOGO) },
                    onAbrirDetalhe = { navController.navigate(Rotas.detalheJogo(it)) },
                    onSalvarEdicao = { id, nota, horas, resenha -> estado.salvarMeuJogo(id, nota, horas, resenha) },
                    onRemover = { estado.removerDosMeusJogos(it) }
                )
            }

            // 3) Adicionar Jogo
            composable(Rotas.ADICIONAR_JOGO) {
                AdicionarJogoScreen(
                    nomesJaJogados = estado.meusJogos.map { it.nome },
                    onVoltar = { navController.popBackStack() },
                    onSalvar = { nome, plataforma, nota, horas, resenha ->
                        estado.registrarJogo(nome, plataforma, nota, horas, resenha)
                        navController.popBackStack()
                    }
                )
            }

            // 4) Detalhe do Jogo
            composable(
                route = Rotas.DETALHE_JOGO,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entrada ->
                val id = entrada.arguments?.getString("id").orEmpty()
                LaunchedEffect(id) {
                    estado.jogoDoDetalhe = null
                    estado.carregarJogoPorId(id)
                }
                val jogo = estado.jogoDoDetalhe
                DetalheJogoScreen(
                    jogo = jogo,
                    notaMedia = if (jogo != null) estado.notaMedia(jogo) else null,
                    amigosQueJogaram = estado.amigosQueJogaram(id),
                    onVoltar = { navController.popBackStack() },
                    onAbrirAmigo = { navController.navigate(Rotas.detalheAmigo(it)) },
                    onMarcarJogado = { nota -> estado.salvarMeuJogo(id, nota, 0, "") },
                    onAtualizar = { nota, horas -> estado.salvarMeuJogo(id, nota, horas, jogo?.resenha.orEmpty()) },
                    onRegistrarSessao = { estado.registrarSessao(id) },
                    onRemover = {
                        // Jogo criado pelo usuário é apagado de vez, então a tela precisa sair.
                        if (jogo?.personalizado == true) navController.popBackStack()
                        estado.removerDosMeusJogos(id)
                    }
                )
            }

            // 5) Amigos
            composable(Rotas.AMIGOS) {
                AmigosScreen(
                    amigos = estado.amigos,
                    atividades = estado.atividades,
                    onCarregar = { estado.carregarAmigos() },
                    onAbrirDetalhe = { navController.navigate(Rotas.detalheAmigo(it)) },
                    onAdicionar = { nome, avatar, bio -> estado.adicionarAmigo(nome, avatar, bio) },
                    onRemover = { estado.removerAmigo(it) }
                )
            }

            // Detalhe do Amigo (detalhe da lista de amigos)
            composable(
                route = Rotas.DETALHE_AMIGO,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entrada ->
                val id = entrada.arguments?.getString("id").orEmpty()
                DetalheAmigoScreen(
                    amigo = estado.amigoPorId(id),
                    atividadesDoAmigo = estado.atividadesDoAmigo(id),
                    todosJogos = estado.jogos,
                    onVoltar = { navController.popBackStack() },
                    onAbrirJogo = { navController.navigate(Rotas.detalheJogo(it)) },
                    onRemover = { estado.removerAmigo(id) { navController.popBackStack() } }
                )
            }

            // 6) Atividade
            composable(Rotas.ATIVIDADE) {
                AtividadeScreen(
                    itens = estado.feed(),
                    curtidas = estado.curtidas,
                    onCurtir = { estado.alternarCurtida(it) },
                    onAbrirJogo = { navController.navigate(Rotas.detalheJogo(it)) },
                    onAbrirAmigo = { navController.navigate(Rotas.detalheAmigo(it)) },
                    onRegistrarSessao = { estado.registrarSessao(it) }
                )
            }

            // 7) Perfil
            composable(Rotas.PERFIL) {
                PerfilScreen(
                    nome = estado.nomePerfil,
                    sobre = estado.sobre,
                    avatar = estado.avatarPerfil,
                    fotoPerfil = estado.fotoPerfil,
                    modoEscuro = modoEscuro,
                    meusJogos = estado.meusJogos,
                    quantidadeAmigos = estado.amigos.size,
                    onSalvarPerfil = { nome, sobre, avatar ->
                        estado.nomePerfil = nome
                        estado.sobre = sobre
                        estado.avatarPerfil = avatar
                    },
                    onFotoEscolhida = { estado.fotoPerfil = it },
                    onAlternarModo = { estado.modoEscuro = it }
                )
            }
        }
    }
}

@Composable
private fun BarraInferior(rotaAtual: String?, aoSelecionar: (String) -> Unit) {
    NavigationBar(containerColor = Cores.Barra) {
        itensBarra.forEach { item ->
            NavigationBarItem(
                selected = rotaAtual == item.rota,
                onClick = { aoSelecionar(item.rota) },
                icon = { Icon(item.icone, contentDescription = item.rotulo) },
                label = { Text(item.rotulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Cores.SobreDestaque,
                    selectedTextColor = Cores.Destaque,
                    indicatorColor = Cores.Destaque,
                    unselectedIconColor = Cores.Cinza,
                    unselectedTextColor = Cores.Cinza
                )
            )
        }
    }
}
