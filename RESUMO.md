# Java Multithread

---

## Aula 01 - Thread

**`Thread_1.java` / `MeuRunnable.java`**

Exemplo introdutório de criação de threads em Java, mostrando as formas básicas de instanciar e executar.

- `Thread.currentThread()` — retorna a referência da thread que está executando aquele trecho de código naquele momento.
- `Thread.currentThread().getName()` — retorna o nome da thread atual (ex: "main", "Thread-0").
- Implementar `Runnable` (`MeuRunnable`) — forma de definir a tarefa a ser executada, separando a lógica da tarefa da classe `Thread` em si.
- `new Thread(runnable)` — cria uma nova thread associada a um `Runnable`.
- `thread.start()` — inicia a execução da thread em uma **nova thread de execução** (o `run()` roda em paralelo).
- `thread.run()` (comentado no exemplo) — chamar `run()` diretamente **não cria uma nova thread**, apenas executa o método como uma chamada comum, na thread atual.
- Reaproveitamento do mesmo `Runnable` em múltiplas `Thread`s (`t0` e `t2` usam `meuRunnable`) — mostra que um `Runnable` pode ser reutilizado por diferentes threads.
- Uso de lambda como `Runnable` (`t1`) — forma simplificada de criar a tarefa sem precisar de uma classe separada.
- ⚠️ Chamar `start()` duas vezes na mesma instância de `Thread` lança `IllegalThreadStateException` — uma thread só pode ser iniciada uma vez (comentário no código alerta sobre isso, o erro ocorre ao chamar `start()` mais de uma vez na *mesma* thread).

---

## Aula 02 - synchronized

**`Synchronized_1.java`**

Exemplo mostrando as diferentes formas de usar `synchronized` e o efeito disso na concorrência.

- `synchronized` em método de instância (`public synchronized void run()`) — usa o objeto atual (`this`) como monitor/lock; só uma thread por vez executa o método para aquela instância.
- `synchronized (Synchronized_1.class)` (comentado) — lock a nível de classe, usando o próprio objeto `Class` como monitor; útil quando o recurso compartilhado é estático, não ligado a uma instância específica.
- `synchronized (objeto)` com locks nomeados (`lock1`, `lock2`, comentado) — exemplifica uso de monitores independentes, mas o código deixa claro (via comentário) que isso é um **anti-padrão** aqui: como os dois locks protegem a mesma variável (`i`), eles não impedem concorrência entre si e geram race condition. Serve como exemplo didático de uso incorreto.
- Reforça que `synchronized` **serializa o acesso** ao recurso (cria uma fila), garantindo thread-safety mas eliminando o paralelismo real naquele trecho.
- Reforça que a ordem de execução entre threads concorrentes **não é determinística** — depende do escalonador da JVM/SO, não da ordem de `.start()`.
- Comentário também ilustra a diferença entre chamar `.run()` direto (execução sequencial, sem threads reais) vs `.start()` (execução concorrente de fato).

**`Synchronized_2.java`**

Exemplo mostrando **boa prática**: manter o bloco `synchronized` o menor possível.

- `synchronized (this)` protegendo **apenas** a leitura/escrita da variável compartilhada `i` e o cálculo de `j`.
- Operações pesadas (`Math.pow`, `Math.sqrt`) ficam **fora** do bloco sincronizado, pois não dependem do estado compartilhado — isso permite que essas operações rodem em paralelo entre as threads, mesmo que o acesso a `i` seja serializado.
- Ilustra o princípio de minimizar a seção crítica para reduzir contenção e maximizar paralelismo.

---

## Aula 03 - Coleções Sincronizadas(Thread-safe)

**`SincronizarColecoes.java`**

Exemplo mostrando como tornar coleções padrão do Java (que não são thread-safe por padrão) seguras para uso concorrente.

- `Collections.synchronizedList(lista)` — envolve a lista em um wrapper que sincroniza (internamente, usando `synchronized`) todos os métodos de acesso, tornando-a segura para múltiplas threads.
- Existem variantes equivalentes pra outros tipos de coleção (comentadas no código):
    - `Collections.synchronizedCollection(...)`
    - `Collections.synchronizedMap(...)`
    - `Collections.synchronizedSet(...)`
    - Importante escolher a versão correspondente ao tipo real da coleção.
- `Thread.sleep(500)` — pausa a thread atual (main) por 500ms; usado aqui como forma **simples (porém não confiável)** de esperar que as outras threads terminem antes de imprimir o resultado. É um "gambiarra" didática — não garante que as 3 threads realmente finalizaram, só torna isso provável.
- Reforça que `ArrayList` comum **não é thread-safe**: adicionar itens concorrentemente sem sincronização pode causar `ConcurrentModificationException` ou corrupção interna da estrutura.
- Mostra que mesmo com a coleção sincronizada, a **ordem de inserção** ainda depende do escalonamento das threads (não determinística).

---

## Aula 04- Coleções para Concorrência

**`ColecoesParaConcorrencia.java`**

Exemplo apresentando coleções da API `java.util.concurrent`, que já são thread-safe "nativamente" (sem precisar de wrapper como `Collections.synchronizedXXX`) e com melhor performance em cenários concorrentes.

- `CopyOnWriteArrayList` (comentado) — implementação de `List` thread-safe que, a cada escrita (add/remove), copia o array interno inteiro. É "pesada" para escrita, mas ótima para cenários de **muita leitura e pouca escrita**, já que leituras não precisam de lock.
- `ConcurrentHashMap` (comentado) — implementação de `Map` thread-safe com lock segmentado (particionado internamente), permitindo alta concorrência tanto em leitura quanto em escrita, sem bloquear o mapa inteiro.
- `LinkedBlockingQueue` (usado no exemplo ativo) — implementação de `BlockingQueue`, uma fila thread-safe que também oferece operações bloqueantes (ex: `take()` espera até haver item disponível, `put()` espera até haver espaço se a fila tiver capacidade limitada). Aqui é usada de forma simples só com `add()`.
- `fila.add(...)` — insere elemento na fila de forma thread-safe, sem precisar de `synchronized` manual.
- `Thread.sleep(500)` — mesmo propósito do exemplo anterior: aguardar (de forma não garantida) que as threads terminem antes de imprimir o resultado.
- Reforça o conceito central da aula: existem coleções prontas da biblioteca padrão do Java pensadas para concorrência, evitando que você precise sincronizar manualmente (`synchronized`) coleções comuns.

---

## Aula 05 - Classes atômicas

**`ClassesAtomicas.java`**

Exemplo apresentando as classes atômicas do pacote `java.util.concurrent.atomic`, que garantem operações thread-safe sem precisar de `synchronized`, usando internamente instruções CAS (Compare-And-Swap) da CPU.

- `AtomicInteger` / `AtomicLong` (comentados) — wrappers atômicos para `int`/`long`, com métodos como `incrementAndGet()` (incrementa e retorna o novo valor de forma atômica, equivalente a um `i++` thread-safe sem lock).
- `AtomicBoolean` (comentado) — wrapper atômico para `boolean`. `compareAndExchange(esperado, novo)` — operação CAS: se o valor atual for igual a `esperado`, troca para `novo`; retorna o valor **anterior** (antes da troca). Base de como as classes atômicas garantem atomicidade sem lock.
- `AtomicReference<T>` (usado no exemplo ativo) — wrapper atômico para referências de objetos genéricos, útil quando o dado compartilhado não é um número/boolean, mas um objeto qualquer.
- `r.getAndSet(novoObjeto)` — troca a referência armazenada por uma nova, de forma atômica, e retorna a referência **antiga**.
- Reforça o conceito central: classes atômicas resolvem problemas de concorrência em variáveis simples (contadores, flags, referências) com melhor performance que `synchronized`, pois evitam o custo de bloqueio/fila, usando instruções de hardware (CAS) em vez de locks.

---

## Aula 06 - volatile

**`Volatile1.java`**

Exemplo introdutório mostrando o problema de **visibilidade de memória** entre threads sem uso de `volatile`.

- Variáveis `numero` e `preparado` são compartilhadas entre a thread `main` e a thread `t0`, mas **sem nenhuma sincronização** (nem `synchronized`, nem `volatile`).
- `Thread.yield()` — sugestão ao escalonador de que a thread atual pode ceder a vez para outras threads prontas para execução; usado aqui dentro do `while` como uma forma "educada" de esperar ocupando menos CPU que um busy-wait puro (mas ainda é busy-wait).
- Ilustra o **risco de reordenação/cache de CPU**: sem `volatile`, não há garantia de que a alteração de `preparado` feita pela thread `main` seja "vista" pela thread `t0` em tempo hábil (ou mesmo nunca) — cada thread pode estar enxergando uma cópia em cache da variável, causando loop infinito ou leitura de valores desatualizados.
- Serve como exemplo didático de **por que `volatile` é necessário**, mostrando o cenário problemático antes de corrigir.

**`Volatile2.java`**

Exemplo mostrando a correção do problema anterior usando `volatile`, com um teste de estresse para evidenciar a diferença.

- `volatile` nas variáveis `numero` e `preparado` — garante **visibilidade** entre threads: toda escrita é imediatamente visível para outras threads (a variável não fica "presa" em cache/registrador de uma thread), e evita reordenações do compilador/JIT ao redor dessas variáveis.
- Comentários mostram a versão **sem** `volatile` para comparação — ao rodar assim, eventualmente uma thread pode ler `numero != 42` mesmo depois de `preparado` já ser `true`, lançando a exceção (evidenciando o bug de visibilidade).
- `Thread.getState()` / `Thread.State` — permite consultar o estado atual da thread (`NEW`, `RUNNABLE`, `TERMINATED`, etc.); aqui usado para esperar (via busy-wait) até que todas as threads do ciclo tenham terminado (`State.TERMINATED`), antes de resetar as variáveis e repetir o loop `while (true)`.
- Estrutura de loop infinito criando 3 threads repetidamente — usada como forma de **estressar o cenário** e aumentar a chance de expor a condição de corrida/visibilidade caso `volatile` fosse removido.
- Reforça que `volatile` resolve visibilidade, mas **não é sinônimo de atomicidade** (diferente das classes `Atomic*` da aula anterior) — não impede race conditions em operações compostas (como `i++`), só garante leitura/escrita direta na memória principal.

---

## Aula 07 - Executor

**`Executors_SingleThread_Callable.java`**

Exemplo introduzindo a API de `Executor`/`ExecutorService`, forma recomendada de gerenciar threads em vez de criá-las manualmente com `new Thread(...)`.

- `Executors.newSingleThreadExecutor()` — cria um pool de threads com **apenas uma thread**, que executa as tarefas submetidas sequencialmente, uma de cada vez, reaproveitando a mesma thread.
- `Callable<T>` — similar ao `Runnable`, mas o método `call()` **pode retornar um valor** e lançar exceções checadas (diferente do `run()` do `Runnable`, que não retorna nada e não pode lançar checked exceptions).
- `executor.submit(callable)` — envia a tarefa para execução no pool e retorna imediatamente um `Future<T>`, representando o resultado que ainda será computado.
- `Future<T>` — representa o resultado futuro de uma tarefa assíncrona:
    - `future.isDone()` — verifica (sem bloquear) se a tarefa já terminou.
    - `future.get()` (comentado) — bloqueia a thread atual até a tarefa terminar e retorna o resultado; pode esperar indefinidamente.
    - `future.get(timeout, TimeUnit)` — mesma ideia, mas com um limite de tempo de espera; lança `TimeoutException` se o tempo estourar antes da tarefa terminar.
- `executor.shutdown()` (comentado) — inicia um desligamento "gracioso": não aceita novas tarefas, mas deixa as já em execução/na fila terminarem.
- `executor.awaitTermination(tempo, TimeUnit)` (comentado) — bloqueia até o executor terminar todas as tarefas ou até o timeout ser atingido.
- `executor.shutdownNow()` — desligamento imediato: tenta interromper as tarefas em execução e descarta as que estão na fila aguardando.
- Uso de `try/finally` garantindo que o executor seja finalizado mesmo em caso de exceção — boa prática para não deixar threads "penduradas" (thread leak).

---

## Aula 08 - Executors
**`Executors_MultiThread.java`**

Exemplo mostrando pools de múltiplas threads e formas de submeter/coletar várias tarefas de uma vez.

- `Executors.newFixedThreadPool(n)` (comentado) — cria um pool com número **fixo** de threads; se todas estiverem ocupadas, novas tarefas ficam numa fila aguardando. Bom para controlar o uso de recursos de forma previsível.
- `Executors.newCachedThreadPool()` (usado no exemplo ativo) — cria um pool que cria threads sob demanda e as reaproveita quando ociosas; threads ociosas por muito tempo (60s por padrão) são encerradas. Bom para muitas tarefas de curta duração, mas pode criar threads demais se não houver controle.
- `executor.invokeAll(lista)` — submete uma lista de `Callable`s de uma vez e **bloqueia** até que **todas** terminem, retornando uma `List<Future<T>>` com os resultados na mesma ordem da lista original.
- `executor.invokeAny(lista)` (comentado) — submete a lista, mas retorna assim que **a primeira** tarefa terminar com sucesso (as demais são canceladas); retorna diretamente o valor `T`, sem `Future`.
- `future.get()` dentro do `for` — bloqueia (uma a uma) até obter o resultado de cada tarefa da lista retornada por `invokeAll`.
- Comentários mostram alternativa de submeter manualmente com múltiplos `executor.submit(...)` chamando `.get()` — evidenciando a diferença entre gerenciar futures individualmente vs usar `invokeAll`/`invokeAny` para lidar com um lote de tarefas.
- Mesmo padrão de `try/finally` com `shutdown()`/`shutdownNow()` da aula anterior, reforçando boa prática de encerramento do executor.

---

## Aula 09 - Scheduled

**`Executors_Scheduled.java`**

Exemplo apresentando `ScheduledExecutorService`, usado para agendar tarefas para execução futura ou repetida em intervalos.

- `Executors.newScheduledThreadPool(n)` — cria um pool de threads voltado para tarefas agendadas, com `n` threads disponíveis para executar as tarefas quando chegar a hora.
- `executor.schedule(tarefa, delay, unidade)` (comentado) — agenda a tarefa para rodar **uma única vez**, após o delay especificado.
- `executor.scheduleAtFixedRate(tarefa, delayInicial, periodo, unidade)` (comentado) — agenda execução repetida em intervalos **fixos a partir do início de cada execução**; se uma execução demorar mais que o período, a próxima começa logo em seguida (sem esperar o período completo), podendo até "empilhar" execuções.
- `executor.scheduleWithFixedDelay(tarefa, delayInicial, delay, unidade)` (usado no exemplo ativo) — agenda execução repetida, mas o intervalo é contado a partir do **término** da execução anterior; garante um "respiro" fixo entre o fim de uma execução e o início da próxima, independente de quanto tempo a tarefa levou.
- `executor.shutdown()` chamado logo após o agendamento — mostra que `shutdown()` **não cancela** tarefas já agendadas/recorrentes, apenas impede novas submissões; a tarefa continua rodando repetidamente mesmo com o executor "desligado" para novas tarefas.
- `Thread.sleep(2000)` dentro da tarefa — simula uma tarefa demorada (2s), útil para observar na prática a diferença entre `scheduleAtFixedRate` (que dispararia mais rápido, ignorando o tempo de execução) e `scheduleWithFixedDelay` (que sempre respeita o delay pós-execução).
- `LocalTime.now()` — usado para imprimir o horário exato de cada execução, facilitando visualizar os intervalos entre as tarefas.

---

## Aula 10 - CyclicBarrier

**`CyclicBarrier_1.java`**

Exemplo introdutório de `CyclicBarrier`, usado para fazer múltiplas threads se esperarem mutuamente até todas atingirem um mesmo ponto de execução.

- `new CyclicBarrier(3)` — cria uma barreira que "abre" somente quando **3 threads** chamarem `.await()` nela.
- `cyclicBarrier.await()` — bloqueia a thread até que todas as threads esperadas cheguem na barreira; quando a última chega, todas são liberadas ao mesmo tempo.
- Cada `Runnable` (`r1`, `r2`, `r3`) calcula uma parte de uma conta de forma independente, chama `await()`, e só depois de todas terminarem os cálculos e "se encontrarem" na barreira é que seguem para o print final ("Terminei o processamento").
- Tratamento de exceções do `await()`: `InterruptedException` e `BrokenBarrierException` — a barreira "quebra" (fica inutilizável) se uma thread for interrompida ou der timeout enquanto espera; `Thread.currentThread().interrupt()` reestabelece o status de interrupção da thread (boa prática ao capturar `InterruptedException`).
- Reforça o conceito central: `CyclicBarrier` sincroniza um **ponto de encontro** entre N threads, diferente de `synchronized`/locks, que controlam acesso a um recurso.

**`CyclicBarrier_2.java`**

Evolução do exemplo anterior, mostrando a **ação final** (barrier action) do `CyclicBarrier` — código executado automaticamente quando a barreira é liberada.

- `new CyclicBarrier(3, finalizacao)` — construtor que aceita um segundo `Runnable`, executado **uma única vez** por uma das threads (a última a chegar), assim que a barreira é liberada, antes de as demais threads continuarem.
- `BlockingQueue<Double> resultados` — usada para acumular os resultados parciais calculados por cada thread, de forma thread-safe, para depois serem somados na ação final.
- `resultados.poll()` — remove e retorna um item da fila (retorna `null` se estiver vazia, ao contrário de `take()`, que bloquearia).
- A ação final (`finalizacao`) soma os 3 resultados parciais e imprime o resultado — reforça o uso prático de `CyclicBarrier` para sincronizar etapas de um processamento paralelo (map-reduce simples: cada thread calcula uma parte, e ao final tudo é combinado).

**`CyclicBarrier_3.java`**

Evolução mostrando a característica **cíclica** do `CyclicBarrier` (diferente de `CountDownLatch`, que só pode ser usado uma vez).

- Mesma estrutura da `CyclicBarrier_2`, mas cada `Runnable` roda dentro de um `while (true)` — depois de passar pela barreira, a thread volta a calcular seu resultado e chama `await()` novamente, repetindo o ciclo indefinidamente.
- Isso demonstra a propriedade que dá nome à classe: **após liberar as threads, a barreira é automaticamente resetada** e pode ser reutilizada para uma nova rodada de sincronização, sem precisar criar uma nova instância.
- `restart()` — método auxiliar que dá um `sleep()` inicial e submete as 3 tarefas ao executor a partir do zero (usado apenas na primeira execução, no `main`).
- `sleep()` — pausa de 1s entre ciclos, dando tempo de visualizar cada rodada no console antes da próxima começar.
- `resultadoFinal` como variável estática acumulando valor entre ciclos (ao invés de resetar a cada rodada) — mostra o estado sendo somado a cada vez que a barreira libera, evidenciando o comportamento contínuo/cíclico do processamento.
- Comentário `//restart()` dentro da ação de sumarização — indica que, se descomentado, cada ciclo poderia resubmeter novas tarefas ao executor após a soma (variação do padrão, não usada aqui).

---

## aula 11 - CountDownlatch

**`CountDownLatch_1.java`**

Exemplo introdutório de `CountDownLatch`, usado para fazer uma (ou mais) thread(s) esperar até que um determinado número de eventos/tarefas aconteça.

- `new CountDownLatch(3)` — cria um "contador" que começa em 3; threads que chamam `.await()` ficam bloqueadas até esse contador chegar a zero.
- `latch.countDown()` — decrementa o contador em 1; chamado por `r1` toda vez que ele executa (uma vez por segundo, via `scheduleAtFixedRate`).
- `latch.await()` — bloqueia a thread chamadora (aqui, a `main`) até o contador chegar a zero.
- Diferente do `CyclicBarrier`, o `CountDownLatch` **não é reutilizável automaticamente** — uma vez zerado, não reseta sozinho. Por isso o código cria manualmente uma **nova instância** (`latch = new CountDownLatch(3)`) a cada ciclo do `while (true)`, simulando um comportamento cíclico "na mão".
- `volatile int i` — variável compartilhada entre a thread `main` (que a altera) e a thread do executor (que a lê dentro de `r1`); `volatile` garante visibilidade dessa mudança entre as threads.
- Fluxo do exemplo: a `main` fica esperando (`await()`) até `r1` rodar 3 vezes (decrementando o latch a cada execução), depois sorteia um novo valor de `i` e recria o latch para o próximo ciclo.
  **`CountDownLatch_2.java`**

Evolução mostrando múltiplas tarefas coordenadas por um mesmo `CountDownLatch`, todas rodando via `Executor` (sem a `main` participar da coordenação).

- `r1` — mesma função de antes: calcula e imprime, chamando `latch.countDown()` a cada execução (roda via `scheduleAtFixedRate`, ou seja, dispara a cada 1s independente de terminar antes).
- `r2`, `r3`, `r4` — cada uma chama `await()` **antes** de fazer sua parte, ou seja, todas ficam esperando o latch zerar (3 execuções de `r1`) para então: `r2` sorteia novo valor de `i`; `r3` recria o latch (`new CountDownLatch(3)`) para o próximo ciclo; `r4` apenas imprime uma mensagem de conclusão.
- Todas (`r2`, `r3`, `r4`) são agendadas com `scheduleWithFixedDelay(..., 0, 1, TimeUnit.SECONDS)` — cada uma tenta rodar a cada 1s, mas fica bloqueada em `await()` até o latch abrir; após reagir, entram em "cooldown" de 1s antes da próxima tentativa.
- Não há `while (true)` explícito controlando o ciclo — a repetição acontece porque as tarefas continuam sendo reagendadas pelo executor indefinidamente (características do `scheduleWithFixedDelay`/`scheduleAtFixedRate`).
- ⚠️ Ponto de atenção: existe uma **race condition potencial** — `r3` recria o `latch` (nova instância) enquanto `r1` pode estar chamando `.countDown()` no latch antigo/novo dependendo do timing. O exemplo funciona na prática pelo timing dos delays, mas evidencia a fragilidade de coordenar múltiplas tarefas concorrentes reatribuindo uma variável compartilhada sem sincronização adicional.

---

## 📌 Nota: CyclicBarrier vs CountDownLatch — quando usar qual

Apesar de parecidos (ambos coordenam threads em torno de uma contagem), eles resolvem problemas diferentes:

| | **CyclicBarrier** | **CountDownLatch** |
|---|---|---|
| **Quem conta** | As mesmas threads que esperam (`await()`) também são as que "avisam" que terminaram — a chamada de `await()` conta como um aviso e bloqueia ao mesmo tempo. | Threads distintas podem contar (`countDown()`) e esperar (`await()`) — não precisam ser as mesmas, nem em igual quantidade. |
| **Relação entre participantes** | **Simétrica**: todo mundo que participa faz a mesma coisa (`await()`). O número de partes é sempre igual ao número de threads que se encontram. | **Assimétrica**: pode ter poucas threads decrementando (ou até uma só, várias vezes) e N threads esperando o resultado, sem relação fixa entre as quantidades. |
| **Reutilização** | **Reutilizável automaticamente** — depois de liberar o grupo, a barreira reseta sozinha e pode ser usada de novo (por isso "cyclic"). | **Não reutilizável** — uma vez zerado, não reseta. Se precisar de um novo ciclo, é necessário criar uma nova instância manualmente. |
| **Ação ao liberar** | Pode disparar uma ação final (`Runnable`) automaticamente, executada uma vez por uma das threads, antes de liberar o grupo. | Não tem ação embutida — quem estava em `await()` simplesmente continua sua execução normalmente. |
| **Analogia** | Um grupo de amigos que combina de se encontrar num ponto — todos esperam uns aos outros, e só andam depois que o último chega. | Uma contagem regressiva de lançamento — várias partes podem "reportar prontidão" (não necessariamente as mesmas que esperam o lançamento), e quem está esperando só é liberado quando a contagem zera. |

**Quando usar qual:**
- Use **`CyclicBarrier`** quando um grupo fixo de threads precisa se sincronizar entre si repetidamente em pontos de encontro (ex: cada thread calcula uma parte de um problema, e todas precisam terminar antes de seguir juntas para a próxima etapa — como nos exemplos das aulas 10).
- Use **`CountDownLatch`** quando a relação entre "quem conta" e "quem espera" é flexível ou assimétrica — por exemplo, uma única fonte de eventos sendo contada várias vezes, com múltiplas threads (que não participam da contagem) apenas aguardando esse marco ser atingido para agir (como nos exemplos da aula 11).
 
---

## Aula 12 Semaphore

**`Semaphore_1.java`**

Exemplo introdutório de `Semaphore`, usado para limitar quantas threads podem acessar um recurso/trecho de código **simultaneamente**.

- `new Semaphore(3)` — cria um semáforo com **3 permissões** disponíveis; no máximo 3 threads podem estar "dentro" do trecho protegido ao mesmo tempo.
- `semaforo.acquire()` — solicita uma permissão; se não houver disponível, a thread **bloqueia** até alguma permissão ser liberada. Ao conseguir, decrementa o número de permissões disponíveis.
- `semaforo.release()` — devolve uma permissão ao semáforo, liberando espaço para outra thread que esteja esperando em `acquire()`.
- `Executors.newCachedThreadPool()` + `executor.execute(r1)` chamado 500 vezes — cria um cenário com muito mais tarefas do que permissões (500 tarefas competindo por apenas 3 "vagas"), evidenciando o efeito de gargalo controlado do semáforo.
- `executor.execute(runnable)` — similar a `submit()`, mas não retorna `Future` (usado aqui porque não há necessidade de acompanhar o resultado de cada tarefa).
- Tratamento de `InterruptedException` em `acquire()` com `Thread.currentThread().interrupt()` — mesmo padrão de boa prática das aulas anteriores, restaurando o status de interrupção da thread.
- Diferente de `synchronized` (que permite só **1** thread por vez), `Semaphore` permite configurar **N threads simultâneas** — útil para limitar acesso a recursos que suportam concorrência limitada (ex: pool de conexões, N chamadas simultâneas a uma API externa).

**`Semaphore_2.java`**

Evolução do exemplo, mostrando `tryAcquire` com timeout e uma forma de monitorar quantas threads estão esperando.

- `new Semaphore(100)` — mesmo conceito, agora com 100 permissões simultâneas.
- `semaforo.tryAcquire(1, TimeUnit.SECONDS)` — tenta adquirir uma permissão, mas **não bloqueia indefinidamente**: espera até 1 segundo e retorna `true`/`false` conforme conseguiu ou não a permissão dentro desse prazo. Diferente de `acquire()`, que bloquearia até conseguir.
- `while (!conseguiu) { conseguiu = tryAcquire(); }` — como o `tryAcquire` pode falhar (timeout de 1s sem conseguir permissão), o código fica tentando repetidamente até conseguir — um padrão de retry sobre uma tentativa não bloqueante.
- `AtomicInteger QTD` — contador atômico (ver aula-05) usado para rastrear quantas threads estão **na fila tentando conseguir uma permissão** no momento (`incrementAndGet()` antes de tentar, `decrementAndGet()` depois de conseguir).
- `Executors.newScheduledThreadPool(501)` — pool grande o suficiente para acomodar as 500 tarefas de trabalho (`r1`) mais 1 tarefa extra de monitoramento (`r2`) rodando em paralelo, sem uma competir pela thread da outra.
- `executor.scheduleWithFixedDelay(r2, 0, 100, TimeUnit.MILLISECONDS)` — tarefa `r2` roda a cada 100ms para atualizar uma janela (`Janelas.Mensagem`, classe auxiliar externa ao exemplo) mostrando em tempo real quantos usuários (`QTD.get()`) estão esperando para conseguir uma permissão.
- Reforça a diferença entre `acquire()` (aula anterior, bloqueio indefinido) e `tryAcquire(timeout)` (aqui, bloqueio limitado com possibilidade de decidir o que fazer em caso de falha — no caso, tentar de novo).

---

