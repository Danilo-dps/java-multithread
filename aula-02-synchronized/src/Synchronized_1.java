/**
 * usar o synchronized impede o paralelismo
 * porque vai estar criando uma fila para acessar o recurso
 * ele torna o dado thread-safe cobrando o preço de não ter o paralelismo
 */
public class Synchronized_1 {

  static int i = -1;

  public static void main(String[] args) {
    MeuRunnable runnable = new MeuRunnable();


// Esse for chama run() diretamente (não start()), então não há criação de threads:
// tudo executa na própria thread main, de forma sequencial por definição.
// O resultado seria igual ao das 5 threads, mas isso não tem relação com o synchronized —
// aqui não existe concorrência para o lock resolver.
//    for (int i = 0; i < 5; i++) {
//      runnable.run();
//    }
    
    Thread t0 = new Thread(runnable);
    Thread t1 = new Thread(runnable);
    Thread t2 = new Thread(runnable);
    Thread t3 = new Thread(runnable);
    Thread t4 = new Thread(runnable);

    t0.start();
    t1.start();
    t2.start();
    t3.start();
    t4.start();
  }

// Aqui o synchronized está a nível de classe (lock no objeto Class)
// A ordem de impressão não é definida pela ordem de chamada de .start(),
// e sim pela ordem em que cada thread consegue adquirir o lock — decisão do
// escalonador da JVM/SO, não determinística. O valor de i apenas reflete
// essa ordem depois que ela já aconteceu.
//  public static void imprime() {
//    synchronized (Synchronized_1.class) {
//      i++;
//      String name = Thread.currentThread().getName();
//      System.out.println(name + ":" + i);
//    }
//  }

  public static class MeuRunnable implements Runnable {

// Esses dois locks servem para exemplificar múltiplos monitores independentes.
// Isso tem aplicação real (ex: lock por recurso, para reduzir contenção),
// mas NÃO da forma como está aqui: lock1 e lock2 protegem acessos à MESMA
// variável compartilhada (i), então os blocos não são mutuamente exclusivos
// entre si -- uma thread em lock1 e outra em lock2 podem alterar i ao mesmo
// tempo, gerando race condition. Locks diferentes só isolam fluxos que de fato
// operam sobre dados diferentes.
//    static Object lock1 = new Object();
//    static Object lock2 = new Object();
    @Override
    public synchronized void run() {
//    public void run() {
//      imprime();
//      synchronized (this) {
        i++;
        String name = Thread.currentThread().getName();
        System.out.println(name + ":" + i);
//      }
//      synchronized(lock1) {
//        i++;
//        String name = Thread.currentThread().getName();
//        System.out.println(name + ":" + i);
//      }
//      synchronized(lock2) {
//        i++;
//        String name = Thread.currentThread().getName();
//        System.out.println(name + ":" + i);
//      }
    }
  }

}