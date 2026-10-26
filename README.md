# Letterbox Games

Aplicativo Android em Kotlin e Jetpack Compose para registrar jogos, dar notas e acompanhar amigos.

**Trabalho 3 — Persistência de Dados com Room**
Integrantes: Gabriel Consulo, Gustavo Macedo e Matheus Henrique Farias de Jesus.

## Como rodar

1. Abra a pasta no Android Studio (`File > Open`).
2. Configure o Android SDK e aguarde o Gradle Sync.
3. Escolha um emulador ou celular e clique em Run ▶.

O Room salva jogos e amigos localmente no aparelho. As listas são carregadas do banco quando abertas; adicionar, editar e remover jogos, e adicionar e remover amigos, gravam as mudanças no banco. O Detalhe do Jogo consulta o jogo pelo ID recebido na rota.

## Estrutura do Room

- `database/Entidades.kt`: entidades `JogoEntity` e `AmigoEntity`; os IDs internos são inteiros autogerados e `chave` preserva os IDs usados pelas rotas e relações já existentes no app.
- `database/Daos.kt`: um DAO para cada entidade, com inserir, buscar todos, atualizar, deletar e busca filtrada pela chave.
- `database/AppDatabase.kt`: banco Room versão 1, com Singleton.
- `state/EstadoApp.kt`: operações do banco em `Dispatchers.IO`, tratamento de erros com `Log.e` e atualização das listas observadas pela interface.

Os jogos e amigos fictícios continuam aparecendo na primeira abertura. Eles são inseridos uma única vez; depois, os dados do usuário são preservados entre aberturas do app.

## Telas

| # | Tela | Rota |
|---|---|---|
| 1 | Trending | `trending` |
| 2 | Meus Jogos | `meusJogos` |
| 3 | Adicionar Jogo | `adicionarJogo` |
| 4 | Detalhe do Jogo | `detalheJogo/{id}` |
| 5 | Amigos | `amigos` |
| 6 | Detalhe do Amigo | `detalheAmigo/{id}` |
| 7 | Atividade | `atividade` |
| 8 | Perfil | `perfil` |

## Processo e decisões

O passo a passo, as decisões de modelagem, a migração do estado em memória para o Room e a checklist técnica estão em [PROCESSO.md](PROCESSO.md).

As imagens atuais das telas estão na pasta `prints/`. **Falta gravar e adicionar ao repositório um vídeo curto demonstrando inserir um jogo, fechar completamente o app, reabri-lo e confirmar que o jogo continua salvo.** Também é necessário incluir as evidências da apresentação solicitadas pela disciplina; não foram criadas gravações fictícias.
