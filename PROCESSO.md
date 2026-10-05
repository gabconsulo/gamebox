# Processo e decisões do trio — Trabalho 2 (MAF)

> **Rascunho.** Os textos abaixo descrevem o que o código faz e as decisões técnicas. Antes de entregar,
> o trio precisa **reescrever com as próprias palavras** (o enunciado pede isso), **tirar os prints no
> emulador** nos lugares marcados com 📸 e preencher a seção 5 com o que realmente aconteceu.
> Sem imagem do app funcionando, a documentação não vale.

## 1. Como estava o projeto no Trabalho 1 e o que mudou

**Trabalho 1.** O app tinha 3 telas (lista de jogos, formulário de novo jogo e perfil) trocadas por
um `enum` com `when`, sem navegação de verdade. Os dados ficavam num arquivo de preferências do celular.

**Trabalho 2.** Mudamos a base do app:

- A troca de telas passou a usar `NavHost` e `NavController`, com as rotas em um objeto `Rotas`.
- Criamos uma barra de navegação inferior de verdade (`NavigationBar`) com 5 áreas.
- O app passou de 3 para 8 telas.
- As listas agora ficam em memória (`mutableStateListOf`), como o enunciado pede.
- Todo botão e todo item clicável faz alguma coisa: navega, abre um popup, edita ou remove.
- Adicionamos modo escuro e modo claro (bônus).

📸 _Print do Trabalho 1 (as 3 telas antigas)._
📸 _Print do app atual com a barra de navegação inferior._

## 2. Por que essas telas e o que cada uma faz

O tema é um diário de jogos social, parecido com o Letterboxd, mas para games.

| Tela | O que faz | Por que escolhemos |
|---|---|---|
| **Trending** | Mostra os jogos do momento e deixa filtrar por motivo (em alta, voltou, lançamento, sempre em alta). O botão "Ver detalhes" abre o Detalhe do Jogo | É a porta de entrada: descobrir o que jogar |
| **Meus Jogos** | Lista dos jogos já jogados, com ordenação por recentes, A–Z e nota. Lápis abre popup de edição; lixeira remove | É a lista principal do app (vem do Trabalho 1) |
| **Adicionar Jogo** | Formulário com nome, plataforma, horas, nota e resenha | Já existia no Trabalho 1; ganhou campos novos |
| **Detalhe do Jogo** | Descrição, nota média e ações sobre o jogo | Detalhe da lista de jogos |
| **Amigos** | Lista de amigos com busca; adicionar amigo por formulário; remover | Segunda lista obrigatória, com outra data class |
| **Detalhe do Amigo** | Estatísticas, compatibilidade de gosto, jogos em comum e atividade | Detalhe da lista de amigos (o enunciado exige um detalhe por lista) |
| **Atividade** | Feed com o que você e os amigos jogaram, com filtros, curtir e "+1 h" | Dá vida ao app e mostra o uso dos dados dos amigos |
| **Perfil** | Foto ou ícone, nome, sobre, estatísticas, modo escuro/claro | Já existia no Trabalho 1; ganhou foto, edição e tema |

Observação: o pedido do trio tinha 7 telas com só um detalhe. Como o enunciado exige **um detalhe para cada
lista**, acrescentamos o **Detalhe do Amigo** como 8ª tela.

📸 _Um print de cada uma das 8 telas._

## 3. Decisões de organização do código

- **Rotas em um objeto só (`Rotas`).** Todas as rotas são `const val String`. As que precisam de um
  item têm `{id}` (`detalheJogo/{id}`, `detalheAmigo/{id}`), e funções como `Rotas.detalheJogo(id)`
  montam a rota certa. Assim não há texto solto espalhado pelo código.
- **Um único `NavHost`** em `AppNavigation.kt`, com todas as telas. A barra inferior só aparece nas 5
  áreas principais; nas telas secundárias aparece a `TopAppBar` com o botão de voltar.
- **Onde ficam as listas.** Em uma classe `EstadoApp` (`state/EstadoApp.kt`), criada uma única vez na
  `MainActivity`. Ela tem as `mutableStateListOf` de jogos, amigos e atividades, e as funções que mudam
  esses dados (adicionar, remover, editar). As telas recebem os dados e funções por parâmetro, então
  uma mudança numa tela (por exemplo, remover um amigo) aparece em todas as outras na hora.
- **O detalhe busca o item pelo id.** A rota leva só o id; a tela de detalhe procura o item atual na
  lista. Por isso, se a nota de um jogo muda, o detalhe já mostra o valor novo.
- **Um jogo só, várias visões.** `Jogo` serve para o Trending (catálogo) e para Meus Jogos (quando tem
  `jogadoEm`). Assim "marcar como jogado" no detalhe faz o jogo aparecer em Meus Jogos sem copiar dados.
- **Componentes reutilizados** em `ui/components/Componentes.kt` (cartão, chips, estrelas, botão com
  gradiente, etc.) para as telas ficarem iguais.
- **Modo escuro/claro.** Duas paletas em `ui/theme/Paleta.kt`. O objeto `Cores` lê a paleta atual, então
  o interruptor do Perfil muda o app inteiro.
- **`configChanges` no manifesto.** Como os dados ficam só em memória, sem isso girar a tela recriaria a
  Activity e apagaria tudo.

📸 _Print da estrutura de pastas no Android Studio._

## 4. Complexidade extra nas telas de Detalhes

O enunciado pede que pelo menos um detalhe faça mais do que reexibir os campos. Fizemos nos dois.

**Detalhe do Jogo**
- **Informação calculada:** a *nota média* junta a nota da comunidade (ponderada pelo número de
  avaliações), as notas dos amigos que jogaram e a nota do próprio usuário.
- **Edição na própria tela:** se o jogo já foi jogado, dá para mudar a nota, somar ou tirar horas e
  registrar uma sessão de +1 h. Se não foi jogado, dá para escolher a nota e marcar como jogado.
- **Combina as duas listas:** mostra os amigos que já jogaram aquele jogo, com a nota de cada um, e
  leva ao Detalhe do Amigo.

**Detalhe do Amigo**
- **Informações calculadas:** número de jogos, nota média que o amigo dá e plataforma favorita.
- **Compatibilidade de gosto:** cálculo de 0 a 100% pela diferença entre as notas do usuário e do amigo
  nos jogos que os dois jogaram (100 − diferença média × 25).
- **Combina as duas listas:** lista de jogos em comum, com as duas notas lado a lado.
- **Navegação secundária:** cada jogo abre o Detalhe do Jogo.

**Por que escolhemos isso:** porque são exatamente os dados que ligam jogos e amigos, o que faz o app
parecer uma rede social de jogos e não duas listas separadas.

📸 _Prints do Detalhe do Jogo (jogado e não jogado) e do Detalhe do Amigo com a compatibilidade._
📸 _Vídeo curto ou sequência de prints mostrando a nota mudando no detalhe e refletindo em Meus Jogos._

## 5. Dificuldades e como resolvemos

> **O trio precisa preencher esta seção com o que realmente aconteceu** (quem teve dificuldade, em que
> ponto, como resolveu). O enunciado pergunta isso diretamente.

Pontos que vale comentar, se foram mesmo um problema para vocês:

- Passar o item certo para a tela de detalhe (usar o id na rota em vez de copiar o objeto).
- Fazer a barra inferior não empilhar telas repetidas ao trocar de aba.
- O que acontece quando um item é removido enquanto a tela de detalhe dele está aberta.
- Adaptar as cores para funcionar nos dois modos (claro e escuro).

## 6. Roteiro de teste antes de entregar (checklist do professor)

Marque conforme testar no emulador:

- [ ] O app abre e navega sem travar.
- [ ] Barra inferior: Trending, Jogos, Amigos, Atividade e Perfil abrem a tela certa.
- [ ] Trending: chips filtram; "Ver detalhes" e o toque no card abrem o Detalhe do Jogo correto.
- [ ] Meus Jogos: **adicionar** um jogo (botão + no topo) e ele aparece na lista.
- [ ] Meus Jogos: **remover** um jogo pela lixeira (com confirmação) e ele some.
- [ ] Meus Jogos: ordenar por Recentes, A–Z e Nota muda a ordem.
- [ ] Meus Jogos: lápis abre o popup e salvar muda nota, horas e resenha.
- [ ] Detalhe do Jogo: abrir dois jogos diferentes mostra dados diferentes.
- [ ] Detalhe do Jogo: mudar a nota/horas e marcar como jogado funcionam; nota média mostra número.
- [ ] Amigos: **adicionar** um amigo (popup) e ele aparece na lista (inclusive com nome vazio/repetido dando erro).
- [ ] Amigos: **remover** um amigo e ele some (e some do feed de Atividade).
- [ ] Detalhe do Amigo: abrir dois amigos diferentes mostra dados diferentes; jogos abrem o Detalhe do Jogo.
- [ ] Atividade: filtros, curtir, "+1 h agora" e "Ver perfil" funcionam.
- [ ] Perfil: editar perfil, adicionar/remover foto e o interruptor de modo escuro funcionam.
- [ ] Botão de voltar da `TopAppBar` funciona nas 3 telas secundárias.
- [ ] Girar a tela não apaga os dados.
