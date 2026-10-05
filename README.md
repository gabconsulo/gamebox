# Letterbox Games

App Android (Kotlin + Jetpack Compose) para registrar os jogos que você já jogou, dar notas,
ver o que está em alta e acompanhar os amigos.

Trabalho 2 — MAF. Integrantes do trio: _preencher com os nomes_

## Como rodar

1. Abra a pasta no Android Studio (`File > Open`).
2. Espere o Gradle Sync terminar.
3. Escolha um emulador ou celular e clique em Run ▶.

Os dados ficam só na memória: ao fechar o app, tudo volta ao início.
Jogos, amigos e atividades iniciais são dados de exemplo fictícios.

## Telas

| # | Tela | Rota |
|---|---|---|
| 1 | Trending | `trending` |
| 2 | Meus Jogos (lista 1) | `meusJogos` |
| 3 | Adicionar Jogo | `adicionarJogo` |
| 4 | Detalhe do Jogo | `detalheJogo/{id}` |
| 5 | Amigos (lista 2) | `amigos` |
| 6 | Detalhe do Amigo | `detalheAmigo/{id}` |
| 7 | Atividade | `atividade` |
| 8 | Perfil (modo claro/escuro) | `perfil` |

## Onde está cada requisito

- Navegação (`NavHost`, `Rotas`, barra inferior): `navigation/AppNavigation.kt`
- `TopAppBar` com voltar: `TelaComTopBar` em `ui/components/Componentes.kt`
- Data classes `Jogo` e `Amigo`: pasta `model/`
- Listas com `mutableStateListOf`: `state/EstadoApp.kt`
- Telas: pasta `screens/` (uma por arquivo)
- O que o detalhe faz a mais: seção 4 do `PROCESSO.md`