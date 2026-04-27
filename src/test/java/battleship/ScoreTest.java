package battleship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    @Test
    @DisplayName("Criar Score e verificar valores")
    void testCriacaoScore() {
        Score score = new Score("Vitória", 5, 10.5);

        assertEquals("Vitória", score.getResultado());
        assertEquals(5, score.getJogadas());
        assertEquals(10.5, score.getTempo());
    }

    @Test
    @DisplayName("Score com valores diferentes")
    void testValoresDiferentes() {
        Score score = new Score("Derrota", 10, 20.0);

        assertEquals("Derrota", score.getResultado());
        assertEquals(10, score.getJogadas());
        assertEquals(20.0, score.getTempo());
    }

    @Test
    @DisplayName("Score com valores limite")
    void testValoresLimite() {
        Score score = new Score("", 0, 0.0);

        assertEquals("", score.getResultado());
        assertEquals(0, score.getJogadas());
        assertEquals(0.0, score.getTempo());
    }

    @Test
    @DisplayName("toString deve conter informação correta")
    void testToString() {
        Score score = new Score("Vitória", 5, 10.5);

        String texto = score.toString();

        assertTrue(texto.contains("Resultado: Vitória"));
        assertTrue(texto.contains("Jogadas: 5"));
        assertTrue(texto.contains("Tempo: 10.5"));
    }
}