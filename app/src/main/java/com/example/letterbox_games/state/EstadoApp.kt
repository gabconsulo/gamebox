package com.example.letterbox_games.state

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.letterbox_games.model.Amigo
import com.example.letterbox_games.model.Atividade
import com.example.letterbox_games.model.ItemFeed
import com.example.letterbox_games.model.Jogo
import com.example.letterbox_games.model.Tendencia
import com.example.letterbox_games.model.UM_DIA_EM_MS
import com.example.letterbox_games.model.UMA_HORA_EM_MS

/**
 * Guarda TODOS os dados do app em memória (mutableStateListOf).
 * Uma única instância é criada na MainActivity e repassada às telas pelo AppNavigation, então
 * uma alteração feita numa tela (ex.: remover um amigo) aparece na hora em todas as outras.
 * Os dados se perdem ao fechar o app — persistência fica para o próximo trabalho.
 */
class EstadoApp {
    val jogos = mutableStateListOf<Jogo>().apply { addAll(Exemplos.jogos()) }
    val amigos = mutableStateListOf<Amigo>().apply { addAll(Exemplos.amigos()) }
    val atividades = mutableStateListOf<Atividade>().apply { addAll(Exemplos.atividades()) }
    val curtidas = mutableStateListOf<String>()

    // Perfil
    var nomePerfil by mutableStateOf("Gabriel")
    var sobre by mutableStateOf("Jogador de fim de semana. Apaixonado por RPGs e jogos indie.")
    var avatarPerfil by mutableStateOf("🎮")
    var fotoPerfil by mutableStateOf<Uri?>(null)

    /** null = seguir o tema do sistema; true/false = escolha feita no Perfil. */
    var modoEscuro by mutableStateOf<Boolean?>(null)

    // ---------- Consultas ----------

    val meusJogos: List<Jogo> get() = jogos.filter { it.jogado }

    val jogosEmAlta: List<Jogo>
        get() = jogos.filter { it.tendencia != null }.sortedByDescending { it.variacaoJogadores }

    fun jogoPorId(id: String?): Jogo? = jogos.firstOrNull { it.id == id }

    fun amigoPorId(id: String?): Amigo? = amigos.firstOrNull { it.id == id }

    fun atividadesDoAmigo(amigoId: String): List<Atividade> =
        atividades.filter { it.amigoId == amigoId }.sortedByDescending { it.quando }

    /** Amigos que já jogaram este jogo, com a última nota de cada um. */
    fun amigosQueJogaram(jogoId: String): List<Pair<Amigo, Atividade>> =
        atividades
            .filter { it.jogoId == jogoId }
            .sortedByDescending { it.quando }
            .distinctBy { it.amigoId }
            .mapNotNull { atividade -> amigoPorId(atividade.amigoId)?.let { it to atividade } }

    /**
     * Nota média calculada: junta a nota da comunidade (ponderada pelo nº de avaliações),
     * as notas dos seus amigos e a sua própria nota.
     */
    fun notaMedia(jogo: Jogo): Double? {
        val extras = amigosQueJogaram(jogo.id).map { it.second.nota } +
            (if (jogo.jogado && jogo.minhaNota > 0) listOf(jogo.minhaNota) else emptyList())
        val votos = jogo.avaliacoes + extras.size
        if (votos == 0) return null
        return (jogo.notaComunidade * jogo.avaliacoes + extras.sum()) / votos
    }

    /** Partidas suas e dos amigos misturadas, da mais recente para a mais antiga. */
    fun feed(): List<ItemFeed> {
        val minhas = meusJogos.map { jogo ->
            ItemFeed(
                id = "eu-${jogo.id}",
                autor = nomePerfil,
                avatar = avatarPerfil,
                ehVoce = true,
                amigoId = null,
                jogo = jogo,
                nota = jogo.minhaNota,
                comentario = jogo.resenha,
                quando = jogo.jogadoEm ?: 0L
            )
        }
        val dosAmigos = atividades.mapNotNull { atividade ->
            val amigo = amigoPorId(atividade.amigoId) ?: return@mapNotNull null
            val jogo = jogoPorId(atividade.jogoId) ?: return@mapNotNull null
            ItemFeed(
                id = atividade.id,
                autor = amigo.nome,
                avatar = amigo.avatar,
                ehVoce = false,
                amigoId = amigo.id,
                jogo = jogo,
                nota = atividade.nota,
                comentario = atividade.comentario,
                quando = atividade.quando
            )
        }
        return (minhas + dosAmigos).sortedByDescending { it.quando }
    }

    // ---------- Meus jogos ----------

    /** Adiciona um jogo novo aos meus jogos; se já existir um com o mesmo nome, marca esse como jogado. */
    fun registrarJogo(nome: String, plataforma: String, nota: Int, horas: Int, resenha: String) {
        val existente = jogos.firstOrNull { it.nome.equals(nome, ignoreCase = true) }
        if (existente != null) {
            salvarMeuJogo(existente.id, nota, horas, resenha)
        } else {
            jogos.add(
                Jogo(
                    nome = nome,
                    plataforma = plataforma,
                    descricao = "Jogo adicionado por você à sua biblioteca.",
                    personalizado = true,
                    minhaNota = nota,
                    horas = horas,
                    resenha = resenha,
                    jogadoEm = System.currentTimeMillis()
                )
            )
        }
    }

    /** Marca como jogado (se ainda não era) e grava nota, horas e resenha. */
    fun salvarMeuJogo(id: String, nota: Int, horas: Int, resenha: String) {
        val indice = jogos.indexOfFirst { it.id == id }
        if (indice < 0) return
        val jogo = jogos[indice]
        jogos[indice] = jogo.copy(
            minhaNota = nota,
            horas = horas,
            resenha = resenha,
            jogadoEm = jogo.jogadoEm ?: System.currentTimeMillis()
        )
    }

    /** Registra mais uma hora de jogo agora (o jogo sobe para o topo da Atividade). */
    fun registrarSessao(id: String) {
        val indice = jogos.indexOfFirst { it.id == id }
        if (indice < 0) return
        jogos[indice] = jogos[indice].copy(horas = jogos[indice].horas + 1, jogadoEm = System.currentTimeMillis())
    }

    /**
     * Tira o jogo dos meus jogos. Jogo do catálogo continua existindo (volta a ser "não jogado");
     * jogo que o próprio usuário criou é apagado de vez.
     */
    fun removerDosMeusJogos(id: String) {
        val jogo = jogoPorId(id) ?: return
        if (jogo.personalizado) {
            jogos.removeAll { it.id == id }
            atividades.removeAll { it.jogoId == id }
        } else {
            val indice = jogos.indexOfFirst { it.id == id }
            jogos[indice] = jogo.copy(minhaNota = 0, horas = 0, resenha = "", jogadoEm = null)
        }
    }

    // ---------- Amigos ----------

    fun adicionarAmigo(nome: String, avatar: String, bio: String) {
        amigos.add(0, Amigo(nome = nome, avatar = avatar, bio = bio))
    }

    fun removerAmigo(id: String) {
        amigos.removeAll { it.id == id }
        atividades.removeAll { it.amigoId == id }
    }

    // ---------- Atividade ----------

    fun alternarCurtida(itemId: String) {
        if (itemId in curtidas) curtidas.remove(itemId) else curtidas.add(itemId)
    }

    /** Dados de exemplo para o app não abrir vazio (fictícios, só para a demonstração). */
    private object Exemplos {
        private val agora = System.currentTimeMillis()
        private fun diasAtras(dias: Int) = agora - dias * UM_DIA_EM_MS
        private fun horasAtras(horas: Int) = agora - horas * UMA_HORA_EM_MS

        fun jogos() = listOf(
            Jogo(
                id = "silksong", nome = "Hollow Knight: Silksong", plataforma = "PC", genero = "Metroidvania", ano = 2025,
                descricao = "Hornet parte em uma nova jornada pelo reino de Pharloom, cheio de chefes, segredos e criaturas perigosas.",
                notaComunidade = 4.7, avaliacoes = 18400, tendencia = Tendencia.LANCAMENTO, variacaoJogadores = 85
            ),
            Jogo(
                id = "hades2", nome = "Hades II", plataforma = "PC", genero = "Roguelike", ano = 2025,
                descricao = "Melinoë, princesa do Submundo, enfrenta o Titã do Tempo em combates rápidos que mudam a cada tentativa.",
                notaComunidade = 4.6, avaliacoes = 9300, tendencia = Tendencia.LANCAMENTO, variacaoJogadores = 40
            ),
            Jogo(
                id = "clair", nome = "Clair Obscur: Expedition 33", plataforma = "PS5", genero = "RPG", ano = 2025,
                descricao = "RPG por turnos com ações em tempo real. Uma expedição tenta destruir a Pintora antes que ela apague mais uma geração.",
                notaComunidade = 4.8, avaliacoes = 15100, tendencia = Tendencia.EM_ALTA, variacaoJogadores = 62
            ),
            Jogo(
                id = "balatro", nome = "Balatro", plataforma = "Switch", genero = "Roguelike", ano = 2024,
                descricao = "Um jogo de cartas inspirado no pôquer em que você monta combinações cada vez mais absurdas com curingas.",
                notaComunidade = 4.5, avaliacoes = 12200, tendencia = Tendencia.EM_ALTA, variacaoJogadores = 28
            ),
            Jogo(
                id = "cyberpunk", nome = "Cyberpunk 2077", plataforma = "PC", genero = "RPG", ano = 2020,
                descricao = "RPG de mundo aberto em Night City, uma metrópole obcecada por poder, tecnologia e modificações corporais.",
                notaComunidade = 4.3, avaliacoes = 21000, tendencia = Tendencia.VOLTOU, variacaoJogadores = 47
            ),
            Jogo(
                id = "bg3", nome = "Baldur's Gate 3", plataforma = "PC", genero = "RPG", ano = 2023,
                descricao = "Reúna um grupo e enfrente uma ameaça misteriosa em um RPG baseado em Dungeons & Dragons, cheio de escolhas.",
                notaComunidade = 4.9, avaliacoes = 30500, tendencia = Tendencia.VOLTOU, variacaoJogadores = 33
            ),
            Jogo(
                id = "minecraft", nome = "Minecraft", plataforma = "PC", genero = "Sandbox", ano = 2011,
                descricao = "Explore, construa e sobreviva em um mundo de blocos gerado aleatoriamente, sozinho ou com amigos.",
                notaComunidade = 4.6, avaliacoes = 50200, tendencia = Tendencia.SEMPRE_EM_ALTA, variacaoJogadores = 5
            ),
            Jogo(
                id = "stardew", nome = "Stardew Valley", plataforma = "Switch", genero = "Simulação", ano = 2016,
                descricao = "Herde uma fazenda, cultive, crie animais, pesque e faça amizade com os moradores da cidade.",
                notaComunidade = 4.8, avaliacoes = 28700, tendencia = Tendencia.SEMPRE_EM_ALTA, variacaoJogadores = 8,
                minhaNota = 5, horas = 80, resenha = "Meu jogo para relaxar. Perco a noção do tempo.", jogadoEm = diasAtras(10)
            ),
            Jogo(
                id = "eldenring", nome = "Elden Ring", plataforma = "PC", genero = "RPG", ano = 2022,
                descricao = "Explore as Terras Intermédias em um mundo aberto enorme, criado pela FromSoftware, com chefes desafiadores.",
                notaComunidade = 4.7, avaliacoes = 41000, tendencia = Tendencia.SEMPRE_EM_ALTA, variacaoJogadores = 12,
                minhaNota = 5, horas = 120, resenha = "Mundo aberto enorme e desafiador. Cada canto esconde uma surpresa.", jogadoEm = diasAtras(3)
            ),
            Jogo(
                id = "hollowknight", nome = "Hollow Knight", plataforma = "Switch", genero = "Metroidvania", ano = 2017,
                descricao = "Desça até o reino subterrâneo de Hallownest e explore cavernas, cidades e chefes inesquecíveis.",
                notaComunidade = 4.8, avaliacoes = 26400,
                minhaNota = 5, horas = 45, resenha = "Exploração e chefes memoráveis. A trilha sonora é linda.", jogadoEm = diasAtras(20)
            ),
            Jogo(
                id = "gow", nome = "God of War Ragnarök", plataforma = "PS5", genero = "Ação", ano = 2022,
                descricao = "Kratos e Atreus enfrentam o Ragnarök em uma aventura pela mitologia nórdica.",
                notaComunidade = 4.7, avaliacoes = 19800,
                minhaNota = 4, horas = 38, resenha = "História forte e combate muito satisfatório.", jogadoEm = diasAtras(6)
            ),
            Jogo(
                id = "celeste", nome = "Celeste", plataforma = "Switch", genero = "Plataforma", ano = 2018,
                descricao = "Ajude Madeline a escalar a montanha Celeste em fases curtas e desafiadoras com uma história tocante.",
                notaComunidade = 4.7, avaliacoes = 17600,
                minhaNota = 4, horas = 12, resenha = "Difícil, mas cada fase ensina algo.", jogadoEm = horasAtras(20)
            )
        )

        fun amigos() = listOf(
            Amigo("ana", "Ana", "🦊", "Ama metroidvanias e jogos indie."),
            Amigo("bruno", "Bruno", "🐙", "Só RPG, só platina."),
            Amigo("carla", "Carla", "🎧", "Joga de tudo, principalmente co-op."),
            Amigo("diego", "Diego", "🚀", "Competitivo, mas também curte indie.")
        )

        fun atividades() = listOf(
            Atividade("a1", "ana", "silksong", 5, "Melhor do ano!", diasAtras(1)),
            Atividade("a2", "ana", "hollowknight", 5, "Clássico absoluto.", diasAtras(12)),
            Atividade("a3", "ana", "celeste", 5, "", diasAtras(25)),
            Atividade("a4", "bruno", "eldenring", 4, "Difícil, mas vale cada tentativa.", diasAtras(2)),
            Atividade("a5", "bruno", "bg3", 5, "Incrível, joguei três vezes.", diasAtras(8)),
            Atividade("a6", "bruno", "cyberpunk", 4, "", diasAtras(15)),
            Atividade("a7", "carla", "stardew", 5, "Relaxante demais.", diasAtras(4)),
            Atividade("a8", "carla", "hades2", 4, "Viciante.", horasAtras(6)),
            Atividade("a9", "carla", "minecraft", 4, "", diasAtras(9)),
            Atividade("a10", "diego", "clair", 5, "Final perfeito!", diasAtras(3)),
            Atividade("a11", "diego", "celeste", 4, "", diasAtras(11)),
            Atividade("a12", "diego", "eldenring", 5, "Platinei.", diasAtras(18))
        )
    }
}
