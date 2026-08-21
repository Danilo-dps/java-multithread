
public class Thread_1 {

    public static void main(String[] args) {
        // Thread atual
        Thread t = Thread.currentThread();

        MeuRunnable meuRunnable = new MeuRunnable();

        // Nova thread
        Thread t0 = new Thread(meuRunnable);
        // t0.run(); apenas executando na mesma thread
        // t0.start(); executando em uma nova thread

        // Runnable como lambda
        Thread t1 = new Thread(() -> System.out.println(Thread.currentThread().getName()));
        // t1.start(); // NÃO FAÇA! VAI LANÇAR EXCEÇÃO!

        // Várias threads
        Thread t2 = new Thread(meuRunnable);

        t0.start(); // executando em uma nova thread
        t1.start();
        t2.start();
    }

}