import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Semaphore_1 {

  // Como o nome sugere, a classe Semaphore cria um semáforo
  // para a quantidade de threads que é possível passar pelo fluxo no qual ele é o semáforo
  private static final Semaphore SEMAFORO = new Semaphore(3);
  
  public static void main(String[] args) {
    ExecutorService executor = Executors.newCachedThreadPool();

    Runnable r1 = () -> {
      String name = Thread.currentThread().getName();
      int usuario = new Random().nextInt(10000);

      // aqui ele está a lidar com o limite de 3
      acquire();
      System.out.println("Usuário " + usuario
          + " se inscreveu no canal usando a thread " + name + "\n");
      sleep();
      // aqui está a liberar o fluxo quando cada thread que entrou no semáforo concluiu a sua tarefa
      SEMAFORO.release();
    };
    
    for (int i = 0; i < 500; i++) {
      executor.execute(r1);
    }
    
    executor.shutdown();
  }

  private static void sleep() {
    // espera de 1 a 6 segundos
    try {
      int tempoEspera = new Random().nextInt(6);
      tempoEspera++;
      Thread.sleep(1000 * tempoEspera);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }
  }

  private static void acquire() {
    try {
      SEMAFORO.acquire();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }
  }

}