import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class CyclicBarrier_2 {

  private static BlockingQueue<Double> resultados = 
      new LinkedBlockingQueue<>();
  
  // (432*3) + (3^14) + (45*127/12) = ?
  public static void main(String[] args) {
    Runnable finalizacao = () -> {
      System.out.println("Somando tudo.");
      double resultadoFinal = 0;
      resultadoFinal += resultados.poll();
      resultadoFinal += resultados.poll();
      resultadoFinal += resultados.poll();
      System.out.println("Processamento finalizado. "
          + "Resultado final: " + resultadoFinal);
    };

// Com o CyclicBarrier é possível declarar a quantidade de partes que ele vai aguardar
// antes de disparar a ação final (finalizacao). É por isso que cada runnable dá um await():
// isso funciona como um aviso para o CyclicBarrier de que aquela parte terminou.
// Quando o número de avisos bater com o total declarado (3), a barreira libera e roda a ação final.
    CyclicBarrier cyclicBarrier = new CyclicBarrier(3, finalizacao);
    
    ExecutorService executor = Executors.newFixedThreadPool(3);
    
    Runnable r1 = () -> {
//      System.out.println(Thread.currentThread().getName());
      resultados.add(432d*3d);
      await(cyclicBarrier);
//      System.out.println(Thread.currentThread().getName());
    };
    Runnable r2 = () -> {
//      System.out.println(Thread.currentThread().getName());
      resultados.add(Math.pow(3d, 14d));
      await(cyclicBarrier);
//      System.out.println(Thread.currentThread().getName());
    };
    Runnable r3 = () -> {
//      System.out.println(Thread.currentThread().getName());
      resultados.add(45d*127d/12d);
      await(cyclicBarrier);
//      System.out.println(Thread.currentThread().getName());
    };
    
    executor.submit(r1);
    executor.submit(r2);
    executor.submit(r3);
    
    executor.shutdown();
  }

  private static void await(CyclicBarrier cyclicBarrier) {
    try {
      cyclicBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }
  }

}
