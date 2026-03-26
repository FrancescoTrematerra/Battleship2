package battleship;

public class Score {

    private String resultado;
    private int jogadas;
    private double tempo;

    public Score(String resultado, int jogadas, double tempo) {
        this.resultado = resultado;
        this.jogadas = jogadas;
        this.tempo = tempo;
    }

    public String getResultado() {
        return resultado;
    }

    public int getJogadas() {
        return jogadas;
    }

    public double getTempo() {
        return tempo;
    }

    @Override
    public String toString() {
        return "Resultado: " + resultado +
                " | Jogadas: " + jogadas +
                " | Tempo: " + tempo + "s";
    }
}