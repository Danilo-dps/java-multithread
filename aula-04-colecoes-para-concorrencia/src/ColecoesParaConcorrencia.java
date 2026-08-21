import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ColecoesParaConcorrencia {

// Essa classe é pesada porque cada ação faz com que ele copie o array,
// sendo recomendada para cenário de muita leitura e pouca escrita
//  private static List<String> lista = new CopyOnWriteArrayList<>();
//  private static Map<Integer, String> mapa = new ConcurrentHashMap<>();
  private static BlockingQueue<String> fila = new LinkedBlockingQueue<>();
  
  public static void main(String[] args) throws InterruptedException {
    // Coleções que são Thread-safe
    
    MeuRunnable runnable = new MeuRunnable();
    Thread t0 = new Thread(runnable);
    Thread t1 = new Thread(runnable);
    Thread t2 = new Thread(runnable);
    t0.start();
    t1.start();
    t2.start();
    Thread.sleep(500);
//    System.out.println(lista);
//    System.out.println(mapa);
    System.out.println(fila);
  }
  
  public static class MeuRunnable implements Runnable {
    @Override
    public void run() {
//      lista.add("Inscreva-se no canal!");
//      mapa.put(new Random().nextInt(), "Curta esse vídeo!");
      fila.add("Se tiver condições, torne-se um apoiador do canal!");
      String name = Thread.currentThread().getName();
      System.out.println(name + " inseriu na lista!");
    }
  }
}
