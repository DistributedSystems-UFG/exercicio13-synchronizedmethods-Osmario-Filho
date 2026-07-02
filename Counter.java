// Versao SEM sincronizacao.
// increment()/decrement() fazem c++ / c-- , que NAO sao operacoes atomicas:
// cada uma se decompoe em ler -> modificar -> escrever. Com varias threads,
// essas etapas se intercalam e atualizacoes se perdem (lost updates).
class Counter {

    private int c = 0;

    public void increment() {
        c++;
    }

    public void decrement() {
        c--;
    }

    public int value() {
        return c;
    }
}
