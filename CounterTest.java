import java.util.ArrayList;
import java.util.List;

/**
 * Testa as duas versoes da classe Counter sob concorrencia.
 *
 * Estrategia do teste:
 *   - N_THREADS threads chamam increment() ITERATIONS vezes cada uma.
 *   - Outras N_THREADS threads chamam decrement() ITERATIONS vezes cada uma.
 *   - Como o numero de incrementos == numero de decrementos, o valor final
 *     CORRETO deve ser sempre 0, independentemente da ordem de execucao.
 *
 * Qualquer valor final diferente de 0 e prova de corrida (race condition):
 * atualizacoes foram perdidas porque c++/c-- nao sao atomicos.
 */
public class CounterTest {

    static final int N_THREADS = 4;        // threads incrementando (e outras 4 decrementando)
    static final int ITERATIONS = 1_000_000;
    static final int RUNS = 5;             // repeticoes para evidenciar a nao-determinismo

    // Executa uma bateria de threads sobre um contador e devolve o valor final.
    // O contador e passado como Object e acessado via os dois helpers abaixo,
    // para que o mesmo harness sirva as duas implementacoes.
    static int runTest(boolean synchronizedVersion) throws InterruptedException {
        final Counter plain = synchronizedVersion ? null : new Counter();
        final SynchronizedCounter sync = synchronizedVersion ? new SynchronizedCounter() : null;

        List<Thread> threads = new ArrayList<>();

        // Metade das threads incrementa, a outra metade decrementa.
        for (int t = 0; t < N_THREADS; t++) {
            threads.add(new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    if (synchronizedVersion) sync.increment(); else plain.increment();
                }
            }));
            threads.add(new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    if (synchronizedVersion) sync.decrement(); else plain.decrement();
                }
            }));
        }

        for (Thread th : threads) th.start();
        for (Thread th : threads) th.join();   // espera todas terminarem

        return synchronizedVersion ? sync.value() : plain.value();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Configuracao: " + N_THREADS + " threads incrementando + "
                + N_THREADS + " decrementando, " + ITERATIONS + " iteracoes cada.");
        System.out.println("Valor final CORRETO esperado em todas as execucoes: 0");
        System.out.println();

        System.out.println("=== Counter (SEM synchronized) ===");
        for (int r = 1; r <= RUNS; r++) {
            int result = runTest(false);
            System.out.printf("  Execucao %d: valor final = %,d %s%n",
                    r, result, (result == 0 ? "(ok por acaso)" : "<-- INCONSISTENTE (updates perdidos)"));
        }

        System.out.println();
        System.out.println("=== SynchronizedCounter (COM synchronized) ===");
        for (int r = 1; r <= RUNS; r++) {
            int result = runTest(true);
            System.out.printf("  Execucao %d: valor final = %,d %s%n",
                    r, result, (result == 0 ? "(correto)" : "<-- INESPERADO"));
        }
    }
}
