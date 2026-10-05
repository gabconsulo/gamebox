# Letterbox Games

App Android (Kotlin + Jetpack Compose) para registrar os jogos que você já jogou, dar notas,
acompanhar o que está em alta e ver o que os amigos estão jogando.

Trabalho 2 da A2 — **MAF (Mínimo Aplicativo Funcional)**, continuação do Trabalho 1.

> Integrantes do trio: _preencher com os nomes_

## Como rodar

1. Instale o **Android Studio** (versão recente, com suporte ao AGP 9.x) e abra esta pasta (`File > Open`).
2. Aguarde o **Gradle Sync** terminar (ele baixa as dependências sozinho).
3. Escolha um emulador (ou um celular com depuração USB) e clique em **Run ▶**.

Requisitos: JDK 17 ou superior (o Android Studio já traz um) e Android 7.0 (API 24) ou mais novo.

Os dados (jogos, amigos, atividades) ficam **apenas na memória**: ao fechar o app, tudo volta ao
estado inicial. Isso é proposital — guardar os dados de forma permanente é assunto do próximo
trabalho. Os jogos, amigos e atividades que aparecem ao abrir o app são **dados de exemplo
fictícios**, só para a demonstração.

## Telas (8)

Barra inferior (5 áreas) + 3 telas secundárias:

| # | Tela | Rota | O que faz |
|---|---|---|---|
| 1 | **Trending** | `trending` | Jogos do momento (em alta, voltaram, lançamentos, sempre em alta), filtros e botão "Ver detalhes" |
| 2 | **Meus Jogos** | `meusJogos` | **Lista 1.** Jogos jogados, ordenar por recentes / A–Z / nota, editar em popup, remover |
| 3 | **Adicionar Jogo** | `adicionarJogo` | Formulário (nome, plataforma, horas, nota, resenha) |
| 4 | **Detalhe do Jogo** | `detalheJogo/{id}` | **Detalhe 1.** Descrição, nota média calculada, registro editável, amigos que jogaram |
| 5 | **Amigos** | `amigos` | **Lista 2.** Busca, adicionar amigo (popup com formulário), remover |
| 6 | **Detalhe do Amigo** | `detalheAmigo/{id}` | **Detalhe 2.** Estatísticas, compatibilidade de gosto, jogos em comum, atividade |
| 7 | **Atividade** | `atividade` | Feed seu + dos amigos, filtros, curtir, registrar +1 h |
| 8 | **Perfil** | `perfil` | Foto/ícone, editar perfil, estatísticas, modo escuro/claro, sobre o app |

## Requisitos do Trabalho 2 — onde está cada um

| Requisito | Onde |
|---|---|
| NavHost central + objeto `Rotas` | `navigation/AppNavigation.kt` |
| `NavigationBar` funcionando | `BarraInferior` em `AppNavigation.kt` |
| `TopAppBar` com voltar (`popBackStack`) | `TelaComTopBar` em `ui/components/Componentes.kt` (Adicionar Jogo, Detalhe do Jogo, Detalhe do Amigo) |
| 2 data classes | `Jogo` (`model/Jogo.kt`) e `Amigo` (`model/Amigo.kt`); `Atividade` liga as duas |
| Listas reativas (`mutableStateListOf`) | `state/EstadoApp.kt` |
| 2 listas com `LazyColumn` + `Card` | `MeusJogosScreen.kt` e `AmigosScreen.kt` |
| Adicionar e remover em cada lista | Jogos: `AdicionarJogoScreen` + lixeira. Amigos: popup de adicionar + botão de remover |
| 2 detalhes com o item certo (por id) | `DetalheJogoScreen.kt` e `DetalheAmigoScreen.kt` |
| Detalhe com algo a mais | Ver `PROCESSO.md` (seção 4) |
| Bônus: modo escuro e claro | Interruptor no Perfil; paletas em `ui/theme/Paleta.kt` |

## Estrutura do código

```
app/src/main/java/com/example/letterbox_games/
├── MainActivity.kt          # cria o estado e aplica o tema (claro/escuro)
├── model/                   # data classes: Jogo, Amigo, Atividade
├── state/EstadoApp.kt       # todas as listas e regras do app, em memória
├── navigation/AppNavigation.kt   # Rotas, NavHost e barra inferior
├── ui/theme/                # paletas clara/escura e tema
├── ui/components/           # componentes reutilizados pelas telas
└── screens/                 # uma tela por arquivo
```
