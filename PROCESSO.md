# GABRIEL CONSULO
# GUSTAVO MACEDO
# MATHEUS HENRIQUE FARIAS DE JESUS


# Processo e decisões do trio — Trabalho 2 (MAF)

**App:** Letterbox Games — um diário de jogos social, parecido com o Letterboxd, mas para games.
**Integrantes:** Gabriel Consulo, Gustavo Macedo e Matheus Henrique Farias de Jesus.

---

## 1. Como estava o projeto no Trabalho 1 e o que mudou

No Trabalho 1 o app tinha **3 telas**: lista de jogos, formulário de novo jogo e perfil. A troca de telas era
feita por um `enum` (`TelaAtual`) com um `when`, ou seja, não era navegação de verdade, só um estado que
decidia qual tela desenhar. Os jogos ficavam salvos no próprio celular (`SharedPreferences`).

![As 3 telas do Trabalho 1](prints/telastrabalho1.png)

No Trabalho 2 mudamos a base do app:

- A troca de telas passou a usar **`NavHost` e `NavController`**, com todas as rotas num objeto `Rotas`.
- Criamos uma **barra de navegação inferior** de verdade, com 5 áreas.
- O app foi de 3 para **8 telas**.
- Os dados passaram a ficar **só na memória** (`mutableStateListOf`), como o enunciado pede. Tiramos o
  `SharedPreferences` do Trabalho 1.
- Todo botão e todo item clicável agora faz alguma coisa (navega, abre popup, edita ou remove).
- Criamos uma segunda lista de itens (**Amigos**) e uma terceira data class (**Atividade**) que liga jogos e amigos.
- Adicionamos **modo escuro e modo claro** (bônus).

![App atual com a barra de navegação inferior](prints/telatrending.png)

---

## 2. Passo a passo do que fizemos

1. **Planejamos as telas** e o que cada uma faria, olhando o enunciado e a checklist do professor.
2. **Criamos o modelo de dados:** `Jogo`, `Amigo` e `Atividade`.
3. **Criamos a classe `EstadoApp`**, que guarda todas as listas e as regras (adicionar, remover, editar).
4. **Montamos a navegação:** objeto `Rotas`, `NavHost` e a barra inferior.
5. **Construímos as telas** uma por uma, começando pelas listas e depois pelos detalhes.
6. **Criamos o tema claro e escuro** e ligamos ao interruptor do Perfil.
7. **Testamos no emulador/celular**, clicando em todos os botões.
8. **Tiramos os prints**, escrevemos esta documentação e subimos tudo para um repositório novo no GitHub.

---

## 3. Por que essas telas e o que cada uma faz

| Tela | O que faz | Por que escolhemos |
|---|---|---|
| **Trending** | Mostra os jogos do momento (em alta, voltaram, lançamentos, sempre em alta). Tem filtros por motivo, um carrossel "Hype da semana" e o botão **Ver detalhes**, que abre o Detalhe do Jogo | É a porta de entrada: descobrir o que jogar |
| **Meus Jogos** | Lista dos jogos já jogados. Dá para ordenar por recentes, A–Z e nota. O lápis abre um popup para editar e a lixeira remove | É a lista principal do app e vem do Trabalho 1 |
| **Adicionar Jogo** | Formulário com nome, plataforma, horas, nota e resenha | Já existia no Trabalho 1; ganhou campos novos |
| **Detalhe do Jogo** | Descrição, nota média e ações sobre o jogo | Detalhe da lista de jogos |
| **Amigos** | Lista de amigos com busca. Adicionar amigo por formulário e remover | Segunda lista obrigatória, com outra data class |
| **Detalhe do Amigo** | Estatísticas, compatibilidade de gosto, jogos em comum e atividade | Detalhe da lista de amigos (o enunciado exige um detalhe por lista) |
| **Atividade** | Feed com o que você e os amigos jogaram, com filtros, curtir e "+1 h" | Dá vida ao app e usa os dados dos amigos |
| **Perfil** | Foto ou ícone, nome, sobre, estatísticas e modo escuro/claro | Já existia no Trabalho 1; ganhou foto, edição e tema |

Nosso plano inicial tinha 7 telas e só um detalhe. Como o enunciado exige **um detalhe para cada lista**,
acrescentamos o **Detalhe do Amigo** como 8ª tela. O mínimo de 7 continua cumprido.

![Trending](prints/telatrending.png)
![Meus Jogos](prints/telajogos.png)
![Adicionar Jogo](prints/adicionarjogo.png)
![Detalhe do Jogo](prints/detalhedojogo.png)
![Amigos](prints/telaamigos.png)
![Detalhe do Amigo](prints/detalhedoamigo.png)
![Atividade](prints/telaatividade-atividadeamigos.png)
![Perfil](prints/telaperfil.png)

### Adicionar e remover nas duas listas

**Meus Jogos:** o botão **+** do topo abre o formulário e o jogo novo aparece na lista. A lixeira remove (com
confirmação). O lápis abre um popup para editar nota, horas e resenha.

**Amigos:** o ícone de pessoa no topo abre um formulário (nome, sobre e avatar). O novo amigo aparece na lista.
O botão de remover tira o amigo (com confirmação) e a atividade dele some do feed.

---

## 4. Decisões de organização do código

- **Rotas num objeto só (`Rotas`).** Todas as rotas são `const val String`. As que precisam de um item levam
  `{id}` (`detalheJogo/{id}`, `detalheAmigo/{id}`), e funções como `Rotas.detalheJogo(id)` montam a rota certa.
  Assim não há texto solto espalhado pelo código.
- **Um único `NavHost`** em `AppNavigation.kt`, com todas as telas. A barra inferior aparece só nas 5 áreas
  principais. Nas telas secundárias aparece a `TopAppBar` com o botão de voltar.
- **Onde ficam as listas.** Numa classe `EstadoApp` (`state/EstadoApp.kt`), criada uma única vez na
  `MainActivity`. Ela tem as `mutableStateListOf` de jogos, amigos e atividades e as funções que mudam esses
  dados. As telas recebem dados e funções por parâmetro, então uma mudança numa tela (por exemplo, remover um
  amigo) aparece em todas as outras na hora.
- **O detalhe busca o item pelo id.** A rota leva só o id, e a tela de detalhe procura o item atual na lista.
  Por isso, se a nota de um jogo muda, o detalhe já mostra o valor novo.
- **Um jogo só, várias visões.** `Jogo` serve para o Trending (catálogo) e para Meus Jogos (quando tem
  `jogadoEm`). Assim, "marcar como jogado" no detalhe faz o jogo aparecer em Meus Jogos sem copiar dados.
- **Componentes reutilizados** em `ui/components/Componentes.kt` (cartão, chips, estrelas, botão com gradiente
  etc.), para as telas ficarem iguais.
- **Modo escuro/claro.** Duas paletas em `ui/theme/Paleta.kt`. O objeto `Cores` lê a paleta atual, então o
  interruptor do Perfil muda o app inteiro.
- **`configChanges` no manifesto.** Como os dados ficam só em memória, sem isso girar a tela recriaria a
  Activity e apagaria tudo.

![Estrutura de pastas no Android Studio](prints/pastas.png)

---

## 5. Complexidade extra nas telas de Detalhes

O enunciado pede que pelo menos um detalhe faça mais do que reexibir os campos. Fizemos nos dois.

**Detalhe do Jogo**
- **Informação calculada:** a *nota média* junta a nota da comunidade (ponderada pelo número de avaliações),
  as notas dos amigos que jogaram e a nota do próprio usuário.
- **Edição na própria tela:** se o jogo já foi jogado, dá para mudar a nota, somar ou tirar horas e registrar
  uma sessão de +1 h. Se não foi jogado, dá para escolher a nota e marcar como jogado.
- **Combina as duas listas:** mostra os amigos que já jogaram aquele jogo, com a nota de cada um, e leva ao
  Detalhe do Amigo.

**Detalhe do Amigo**
- **Informações calculadas:** número de jogos, nota média que o amigo dá e plataforma favorita.
- **Compatibilidade de gosto:** cálculo de 0 a 100% pela diferença entre as notas do usuário e do amigo nos
  jogos que os dois jogaram (100 − diferença média × 25).
- **Combina as duas listas:** mostra os jogos em comum, com as duas notas lado a lado.
- **Navegação secundária:** cada jogo abre o Detalhe do Jogo.

**Por que escolhemos isso:** porque são exatamente os dados que ligam jogos e amigos, o que faz o app parecer
uma rede social de jogos e não duas listas separadas.

![Detalhe do Jogo com a nota média](prints/detalhedojogo.png)
![Detalhe do Amigo com a compatibilidade](prints/detalhedoamigo.png)

### Modo escuro e claro (bônus)

Foi implementada um sistema de modo escuro.

--

## 6. Dificuldades que tivemos e como resolvemos

### 6.1 Rodar o app: o Windows bloqueou o Gradle

Ao tentar rodar o projeto, o Android Studio mostrou o erro
**`CreateProcess error=4551, Uma política de Controle de Aplicativo bloqueou este arquivo`**.
Isso não era problema do código. O recurso **Smart App Control** do Windows bloqueia programas sem assinatura
digital, e o Gradle e as ferramentas do Android usam vários. Descobrimos isso pesquisando o número do erro.

**Como resolvemos:** em *Segurança do Windows > Controle de aplicativos e navegador > Configurações do Smart App
Control* desligamos o recurso. Depois fechamos o Android Studio, abrimos de novo, sincronizamos o Gradle e o app
rodou. O Microsoft Defender continuou ligado.

Ao mesmo tempo, o Android Studio mostrou um aviso sugerindo **"Exclude folders"**, para o Defender não escanear
as pastas do Gradle e do SDK. Isso não apaga nada e só deixa o build mais rápido. Também apareceu o aviso
"Verificação rápida esperada" do Windows, que é só uma verificação de rotina e não tinha relação com o projeto.

### 6.2 Subir o projeto para o GitHub

Na primeira vez, o upload pelo site do GitHub **perdeu a estrutura de pastas**: os arquivos ficaram todos
soltos na raiz do repositório. Isso acontece quando se selecionam os arquivos em vez de arrastar a pasta inteira.

**Como resolvemos:** passamos a usar o Git de verdade (`git init`, `git add .`, `git commit` e `git push`),
que mantém a estrutura de pastas. Usamos `--force` uma vez, para trocar o conteúdo bagunçado pelo certo, só
nesse repositório novo.

### 6.3 Clonar o repositório de volta

Ao tentar clonar de novo, o Android Studio mostrou **"The directory already exists and it is not empty"**.
Ele recusa clonar numa pasta que já tem arquivos. Entendemos que a pasta não tinha sumido, o clone é que
estava apontando para ela.

**Como resolvemos:** abrimos a pasta que já existia com `File > Open` ou clonamos numa pasta nova com outro
nome.

### 6.4 Passar o item certo para a tela de detalhe

No começo a ideia era copiar o objeto inteiro para a tela de detalhe, mas isso deixaria o detalhe
desatualizado se algo mudasse. **Solução:** passar só o `id` na rota e fazer a tela de detalhe buscar o item
na lista a cada atualização. Assim, mudar a nota no detalhe aparece na hora em Meus Jogos.

### 6.5 O que acontece se o item é removido com o detalhe aberto

Se o usuário remove um jogo ou um amigo estando na tela de detalhe dele, a tela ficaria tentando mostrar algo
que não existe mais e poderia travar. **Solução:** voltar para a tela anterior primeiro (`popBackStack`) e só
então remover. A tela de detalhe também trata o caso de o item ser nulo, mostrando uma tela vazia em vez de
quebrar.

### 6.6 Barra inferior empilhando telas

Se a navegação pela barra inferior usasse `navigate` simples, cada toque empilharia uma tela nova e o botão
voltar teria que ser apertado várias vezes. **Solução:** usar `launchSingleTop`, `popUpTo` e `saveState` ao
trocar de aba, para cada área aparecer só uma vez.

### 6.7 Cores nos dois modos (claro e escuro)

As cores do Trabalho 1 estavam escritas direto nas telas (`Color(0xFF...)`), e isso só funcionava no escuro.
**Solução:** criar duas paletas e um objeto `Cores` que lê a paleta atual, e trocar todas as cores das telas
por ele. O ciano do tema escuro ficava ilegível sobre fundo branco, então usamos um tom mais escuro no modo claro.

### 6.8 Os dados somem ao girar a tela

Como os dados ficam só em memória, girar o celular recriava a tela e a lista voltava ao começo.
**Solução:** declarar `configChanges` no manifesto, para a Activity não ser recriada na rotação.

### 6.9 Primeira vez abrindo o projeto

A primeira sincronização do Gradle demora vários minutos e baixa muita coisa. No começo parecia que o
Android Studio tinha travado, mas era só esperar. O emulador também é lento na primeira vez que abre.

---

## 7. Como testamos

Passamos por todas as telas e botões no emulador/celular:

O app abre e navega sem travar.
Barra inferior: Trending, Jogos, Amigos, Atividade e Perfil abrem a tela certa.
Trending: chips filtram; "Ver detalhes" e o toque no card abrem o Detalhe do Jogo correto.
Meus Jogos: adicionar um jogo (botão +) e ele aparece na lista.
Meus Jogos: remover um jogo pela lixeira (com confirmação) e ele some.
Meus Jogos: ordenar por Recentes, A–Z e Nota muda a ordem. 
Meus Jogos: o lápis abre o popup e salvar muda nota, horas e resenha.
Detalhe do Jogo: abrir dois jogos diferentes mostra dados diferentes. 
Detalhe do Jogo: mudar nota e horas e marcar como jogado funcionam; a nota média mostra um número.
Amigos: adicionar um amigo (popup) e ele aparece (nome vazio ou repetido dá erro).
Amigos: remover um amigo e ele some (e some do feed de Atividade).
Detalhe do Amigo: abrir dois amigos diferentes mostra dados diferentes; os jogos abrem o Detalhe do Jogo.
Atividade: filtros, curtir, "+1 h agora" e "Ver perfil" funcionam.
Perfil: editar perfil, adicionar/remover foto e o interruptor de modo escuro funcionam.
O botão de voltar da `TopAppBar` funciona nas 3 telas secundárias. 


---

## 8. Observação do Trabalho 2

Na versão do Trabalho 2, os jogos, amigos e atividades eram **dados de exemplo fictícios**, só para a
demonstração. Naquele trabalho, os dados ficavam na memória e voltavam ao estado inicial ao fechar o app.

---

# Trabalho 3 — Persistência de Dados com Room

## 9. O que mudou desde o Trabalho 2

No Trabalho 2, jogos e amigos ficavam em `mutableStateListOf` e eram perdidos ao fechar o app. No Trabalho 3, mantivemos as telas, os formulários e as rotas, mas passamos a carregar e gravar as listas de jogos e amigos no Room. As operações de banco são executadas em `Dispatchers.IO`; as telas Meus Jogos e Amigos recarregam seus dados ao abrir.

## 10. Entidades, DAOs e consultas

Criamos `JogoEntity` e `AmigoEntity` em `database/Entidades.kt`. Cada entidade tem um `id: Int` com `@PrimaryKey(autoGenerate = true)` e uma `chave` de texto única. A chave preserva os IDs que o app já usa nas rotas e nas relações com as atividades, sem exigir mudanças nas telas ou nos dados de exemplo. Em JogoEntity guardamos os campos usados pelo catálogo e pela biblioteca; em AmigoEntity guardamos nome, avatar e bio.

Criamos `JogoDao` e `AmigoDao` em `database/Daos.kt`. Cada DAO tem inserir, buscar todos, atualizar e deletar, além de `buscarPorId(chave)` para localizar um registro na navegação. O `AppDatabase` reúne as duas entidades e expõe os DAOs usando Singleton.

## 11. Migração e decisões do processo

Em vez de trocar os IDs de texto já usados no app por IDs numéricos, mantivemos esses IDs como `chave` e deixamos o Room gerar IDs inteiros internos. Isso permitiu persistir os dados sem alterar o fluxo atual das telas. Os dados de exemplo são gravados na primeira abertura; nas próximas, as listas são carregadas do banco, sem recriar os exemplos por cima das alterações do usuário.

As telas de lista usam `LaunchedEffect(Unit)` para solicitar a leitura ao entrar. O detalhe do jogo recebe a chave pela rota e consulta o DAO. Inserção, atualização e remoção também são feitas no banco. Erros de SQLite são registrados com `Log.e`.

Uma dificuldade encontrada foi integrar o KSP com a versão atual do Android Gradle Plugin, que já inclui o Kotlin: a configuração inicial do projeto recusou a inclusão automática das fontes geradas pelo KSP. Para permitir essa integração, configuramos `android.disallowKotlinSourceSets=false` em `gradle.properties`. Depois, a compilação não pôde ser verificada porque este ambiente não tem o Android SDK configurado; por isso, ainda não temos confirmação de execução do app nem do teste de persistência no aparelho.

## 12. Checklist técnica do Trabalho 3

- [x] Dependências Room (`room-runtime`, `room-ktx`) e processador `room-compiler` via KSP.
- [x] Duas entidades com chave primária inteira autogerada.
- [x] Dois DAOs com inserir, buscar todos, atualizar e deletar suspend.
- [x] Consultas adicionais por chave nos dois DAOs.
- [x] `AppDatabase` versão 1 com Singleton.
- [x] Leituras das telas de lista ao abrir e detalhe do jogo consultado pelo ID da rota.
- [x] Operações do DAO executadas em coroutines de `Dispatchers.IO`, com registro de erros.
- [x] README com instruções para abrir e executar o app.
- [ ] Executar no emulador/celular: adicionar um jogo, fechar completamente o app, reabrir e confirmar a persistência.
- [ ] Gravar e anexar ao repositório o vídeo curto desse teste de persistência, solicitado para a apresentação.

O teste de build foi tentado neste ambiente, mas não pôde ser concluído porque não foi encontrado um Android SDK configurado (`ANDROID_HOME` ou `local.properties`). A prova de persistência e o vídeo precisam ser feitos em um ambiente com Android SDK e emulador/celular.

## 13. Problemas comuns ao adicionar o Room

Alguns erros que podem aparecer ao configurar o Room e como conferir:

- **KSP não encontra ou não gera as classes do Room:** conferir se o plugin KSP está aplicado no módulo `app` e se a versão do KSP corresponde à versão do Kotlin do projeto. O compilador do Room deve ser adicionado com `ksp(...)`, não com `implementation(...)`.
- **Erro sobre fontes Kotlin geradas pelo KSP:** neste projeto, o primeiro build mostrou que o Kotlin embutido no Android Gradle Plugin não permitia registrar essas fontes pelo caminho padrão. Foi adicionada a configuração `android.disallowKotlinSourceSets=false` em `gradle.properties`. O build ainda precisa ser confirmado com um Android SDK configurado.
- **Android SDK não encontrado:** a tentativa de build também parou porque este ambiente não tem `ANDROID_HOME` nem `local.properties` com `sdk.dir`. No Android Studio, configurar o SDK instalado e sincronizar novamente. Esse erro não confirma nem indica falha nas entidades ou nos DAOs.
- **Room não aceita um campo do modelo:** tipos como enum precisam ser convertidos para um tipo que o Room armazena. Aqui, `Tendencia` é guardada como texto e convertida de volta ao modelo.
- **Consulta bloqueia a interface:** consultas e outras funções `suspend` do DAO devem ser chamadas numa coroutine fora da thread principal. Neste app, as operações são iniciadas em `Dispatchers.IO`.
- **Erro de migração após mudar uma entidade:** se o formato do banco mudar depois de uma versão já instalada, é necessário aumentar a versão do `AppDatabase` e fornecer uma migração, ou limpar os dados do app durante o desenvolvimento. Ainda não foi necessária uma migração neste projeto, que está na versão 1.

Os itens sobre campos não aceitos e migrações são situações comuns para verificar; não foram erros observados neste projeto. Até configurar o SDK e conseguir compilar e executar o app, ainda não dá para confirmar o funcionamento final do Room no aparelho.