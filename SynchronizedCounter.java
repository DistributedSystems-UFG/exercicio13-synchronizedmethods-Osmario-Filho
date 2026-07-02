// Versao COM sincronizacao.
// A palavra-chave 'synchronized' garante que apenas UMA thread por vez execute
// cada metodo sobre a mesma instancia (lock intrinseco do objeto). Isso torna o
// par ler-modificar-escrever atomico e ainda estabelece uma relacao
// happens-before, garantindo visibilidade das alteracoes entre as threads.
class SynchronizedCounter {

    private int c = 0;

    public synchronized void increment() {
        c++;
    }

    public synchronized void decrement() {
        c--;
    }

    public synchronized int value() {
        return c;
    }
}
