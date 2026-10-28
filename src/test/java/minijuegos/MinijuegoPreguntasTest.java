package minijuegos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.RandomFijo;

/**
 * Pruebas de {@link MinijuegoPreguntas}, {@link Pregunta} y {@link BancoPreguntas}.
 */
class MinijuegoPreguntasTest {

    /** Con j = i en cada paso, Fisher–Yates no mueve nada: salen las 5 primeras del banco. */
    private MinijuegoPreguntas enOrden() {
        return new MinijuegoPreguntas(new RandomFijo(0, 0, 0, 0, 0));
    }

    private int incorrecta(Pregunta p) {
        return (p.getIndiceCorrecta() + 1) % p.getOpciones().size();
    }

    @Test
    @DisplayName("El banco tiene 15 preguntas válidas con 4 opciones")
    void banco() {
        List<Pregunta> banco = BancoPreguntas.getPreguntas();
        assertEquals(15, banco.size());
        for (Pregunta p : banco) {
            assertEquals(4, p.getOpciones().size(), p.getEnunciado());
        }
    }

    @Test
    @DisplayName("Cada partida tiene 5 preguntas distintas del banco")
    void cincoDistintas() {
        for (int semilla = 0; semilla < 20; semilla++) {
            MinijuegoPreguntas juego = new MinijuegoPreguntas(new Random(semilla));
            Set<Pregunta> distintas = new HashSet<>(juego.getPreguntas());
            assertEquals(5, distintas.size());
            assertTrue(BancoPreguntas.getPreguntas().containsAll(distintas));
        }
    }

    @Test
    @DisplayName("Fisher–Yates parcial: sin intercambios salen las 5 primeras del banco")
    void fisherYatesSinIntercambios() {
        assertEquals(BancoPreguntas.getPreguntas().subList(0, 5), enOrden().getPreguntas());
    }

    @Test
    @DisplayName("Fisher–Yates parcial: el primer intercambio trae una pregunta del final")
    void fisherYatesConIntercambio() {
        // i = 0 → j = 0 + 14 = 14; los demás pasos no mueven nada
        MinijuegoPreguntas juego = new MinijuegoPreguntas(new RandomFijo(14, 0, 0, 0, 0));
        assertEquals(BancoPreguntas.getPreguntas().get(14), juego.getPreguntas().get(0));
    }

    @Test
    @DisplayName("Responder bien suma un acierto y pasa a la siguiente pregunta")
    void responderBien() {
        MinijuegoPreguntas juego = enOrden();
        Pregunta primera = juego.getPreguntaActual();
        assertEquals(1, juego.getNumeroPregunta());
        assertTrue(juego.responder(primera.getIndiceCorrecta()));
        assertEquals(1, juego.getCorrectas());
        assertEquals(2, juego.getNumeroPregunta());
    }

    @Test
    @DisplayName("Responder mal no suma, pero también avanza")
    void responderMal() {
        MinijuegoPreguntas juego = enOrden();
        assertFalse(juego.responder(incorrecta(juego.getPreguntaActual())));
        assertEquals(0, juego.getCorrectas());
        assertEquals(2, juego.getNumeroPregunta());
    }

    @Test
    @DisplayName("Todas bien: 25 monedas")
    void todasBien() {
        MinijuegoPreguntas juego = enOrden();
        while (!juego.estaTerminado()) {
            juego.responder(juego.getPreguntaActual().getIndiceCorrecta());
        }
        assertEquals(5, juego.getCorrectas());
        assertEquals(25, juego.calcularMonedas());
        assertNull(juego.getPreguntaActual());
    }

    @Test
    @DisplayName("3 de 5 bien: 15 monedas")
    void tresDeCinco() {
        MinijuegoPreguntas juego = enOrden();
        for (int i = 0; i < 5; i++) {
            Pregunta p = juego.getPreguntaActual();
            juego.responder(i < 3 ? p.getIndiceCorrecta() : incorrecta(p));
        }
        assertEquals(15, juego.calcularMonedas());
        assertTrue(juego.getResumen().contains("3 de 5"));
    }

    @Test
    @DisplayName("Antes de terminar no da monedas, y después ya no acepta respuestas")
    void antesYDespues() {
        MinijuegoPreguntas juego = enOrden();
        juego.responder(juego.getPreguntaActual().getIndiceCorrecta());
        assertEquals(0, juego.calcularMonedas());
        for (int i = 0; i < 4; i++) {
            juego.responder(0);
        }
        assertTrue(juego.estaTerminado());
        assertFalse(juego.responder(0));
    }

    @Test
    @DisplayName("Una pregunta con índice correcto fuera de rango es un error")
    void preguntaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pregunta("¿?", List.of("a", "b"), 2));
    }

    @Test
    @DisplayName("Un banco con menos de 5 preguntas es un error")
    void bancoPequeno() {
        List<Pregunta> pocas = BancoPreguntas.getPreguntas().subList(0, 3);
        assertThrows(IllegalArgumentException.class, () -> new MinijuegoPreguntas(pocas, new Random()));
    }
}
