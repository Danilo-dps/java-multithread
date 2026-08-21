import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Executors_Scheduled {

  public static void main(String[] args) {
    ScheduledExecutorService executor = Executors
        .newScheduledThreadPool(3);

// executa uma única vez, após o delay de 2 segundos
    executor.schedule(new Tarefa(), 2, TimeUnit.SECONDS);

// em teoria, executa logo no início e depois a cada 1 segundo (contado a partir do INÍCIO de cada execução)
// só que como a Tarefa leva 2s (Thread.sleep(2000)) e o período é de 1s, a execução nunca "dá tempo"
// de esperar o período completo — a próxima já começa assim que a anterior termina
    executor.scheduleAtFixedRate(new Tarefa(), 0, 1, TimeUnit.SECONDS);

// executa logo no início, e depois sempre 1 segundo após o TÉRMINO da execução anterior
// (o delay é contado a partir do fim da tarefa, não do início)
    executor.scheduleWithFixedDelay(new Tarefa(), 0, 1, TimeUnit.SECONDS);
    
    //executor.shutdown();
  }

  public static class Tarefa implements Runnable {
    @Override
    public void run() {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(LocalTime.now());
      String name = Thread.currentThread().getName();
      int nextInt = new Random().nextInt(1000);
      System.out.println(name + ": Inscreva-se no canal! " + nextInt);
    }
  }

}
