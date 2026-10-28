package minijuegos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.RandomFijo;

/**
 * Pruebas de {@link MinijuegoMemoria}.
 * <p>
 * Con {@code new RandomFijo()} el barajado no mueve ninguna carta, así que
 * el tablero queda en orden: casillas 0 y 1 = ícono 0, casillas 2 y 3 =
 * ícono 1, etc. Eso permite jugar "a la perfección" en las pruebas.
 */
class MinijuegoMemoriaTest {

    private MinijuegoMemoria ordenado() {
        return new MinijuegoMemoria(new RandomFijo());
    }

    /** Selecciona la casilla número {@code i} (0–15), recorriendo fila por fila. */
    private ResultadoJugada elegir(MinijuegoMemoria juego, int i) {
        return juego.seleccionar(i / 4, i % 4);
    }

    /** Hace {@code n} intentos fallidos y luego encuentra las 8 parejas. */
    private void jugarConFallos(MinijuegoMemoria juego, int n) {
        for (int k = 0; k < n; k++) {
            assertEquals(ResultadoJugada.PRIMERA_CARTA, elegir(juego, 0));
            assertEquals(ResultadoJugada.NO_PAREJA, elegir(juego, 2));
            juego.ocultarNoEmparejadas();
        }
        for (int pareja = 0; pareja < 8; pareja++) {
            elegir(juego, 2 * pareja);
            assertEquals(ResultadoJugada.PAREJA, elegir(juego, 2 * pareja + 1));
        }
    }

    // ---------------- Tablero y barajado ----------------

    @Test
    @DisplayName("El tablero es 4x4 y cada ícono aparece exactamente dos veces")
    void dosDeCadaIcono() {
        MinijuegoMemoria juego = new MinijuegoMemoria(new Random(2026));
        Map<String, Integer> cuenta = new HashMap<>();
        for (int f = 0; f < MinijuegoMemoria.FILAS; f++) {
            for (int c = 0; c < MinijuegoMemoria.COLUMNAS; c++) {
                cuenta.merge(juego.getCarta(f, c).getIcono(), 1, Integer::sum);
            }
        }
        assertEquals(8, cuenta.size());
        for (int veces : cuenta.values()) {
            assertEquals(2, veces);
        }
    }

    @Test
    @DisplayName("Fisher–Yates: con j = 0 en el primer paso, la última casilla se cambia con la primera")
    void fisherYatesIntercambio() {
        // i = 15 → j = 0; en los demás pasos RandomFijo devuelve j = i (no mueve nada)
        MinijuegoMemoria juego = new MinijuegoMemoria(new RandomFijo(0));
        assertEquals("Lápiz", juego.getCarta(0, 0).getIcono());
        assertEquals("Cabito", juego.getCarta(3, 3).getIcono());
        assertEquals("Cabito", juego.getCarta(0, 1).getIcono());
    }

    @Test
    @DisplayName("Con un Random real el tablero queda desordenado")
    void quedaDesordenado() {
        MinijuegoMemoria barajado = new MinijuegoMemoria(new Random(7));
        MinijuegoMemoria orden = ordenado();
        int iguales = 0;
        for (int i = 0; i < 16; i++) {
            if (barajado.getCarta(i / 4, i % 4).getIcono().equals(orden.getCarta(i / 4, i % 4).getIcono())) {
                iguales++;
            }
        }
        assertTrue(iguales < 16);
    }

    @Test
    @DisplayName("Todas las cartas empiezan boca abajo")
    void empiezanBocaAbajo() {
        MinijuegoMemoria juego = new MinijuegoMemoria(new Random(1));
        for (int i = 0; i < 16; i++) {
            assertFalse(juego.getCarta(i / 4, i % 4).isVisible());
        }
    }

    // ---------------- Jugadas ----------------

    @Test
    @DisplayName("Dos cartas iguales quedan emparejadas y cuentan un intento")
    void pareja() {
        MinijuegoMemoria juego = ordenado();
        assertEquals(ResultadoJugada.PRIMERA_CARTA, elegir(juego, 0));
        assertEquals(ResultadoJugada.PAREJA, elegir(juego, 1));
        assertTrue(juego.getCarta(0, 0).isEmparejada());
        assertTrue(juego.getCarta(0, 1).isEmparejada());
        assertEquals(1, juego.getIntentos());
        assertEquals(1, juego.getParejasEncontradas());
    }

    @Test
    @DisplayName("Dos cartas distintas se ocultan después de ocultarNoEmparejadas")
    void noPareja() {
        MinijuegoMemoria juego = ordenado();
        elegir(juego, 0);
        assertEquals(ResultadoJugada.NO_PAREJA, elegir(juego, 2));
        assertTrue(juego.hayCartasPorOcultar());
        assertTrue(juego.getCarta(0, 0).isVisible());
        juego.ocultarNoEmparejadas();
        assertFalse(juego.getCarta(0, 0).isVisible());
        assertFalse(juego.getCarta(0, 2).isVisible());
        assertEquals(1, juego.getIntentos());
    }

    @Test
    @DisplayName("Si no se ocultaron, la siguiente selección las oculta sola")
    void ocultaAlSeleccionarOtra() {
        MinijuegoMemoria juego = ordenado();
        elegir(juego, 0);
        elegir(juego, 2);
        assertEquals(ResultadoJugada.PRIMERA_CARTA, elegir(juego, 4));
        assertFalse(juego.getCarta(0, 0).isVisible());
        assertFalse(juego.getCarta(0, 2).isVisible());
    }

    @Test
    @DisplayName("No se puede seleccionar una carta boca arriba ni una emparejada")
    void seleccionesInvalidas() {
        MinijuegoMemoria juego = ordenado();
        elegir(juego, 0);
        assertEquals(ResultadoJugada.INVALIDA, elegir(juego, 0));
        elegir(juego, 1);
        assertEquals(ResultadoJugada.INVALIDA, elegir(juego, 1));
        assertEquals(1, juego.getIntentos());
    }

    @Test
    @DisplayName("Fuera del tablero la selección es inválida")
    void fueraDelTablero() {
        MinijuegoMemoria juego = ordenado();
        assertEquals(ResultadoJugada.INVALIDA, juego.seleccionar(4, 0));
        assertEquals(ResultadoJugada.INVALIDA, juego.seleccionar(0, -1));
    }

    // ---------------- Puntaje ----------------

    @Test
    @DisplayName("Antes de terminar no da monedas ni XP")
    void sinTerminar() {
        MinijuegoMemoria juego = ordenado();
        elegir(juego, 0);
        elegir(juego, 1);
        assertFalse(juego.estaTerminado());
        assertEquals(0, juego.calcularMonedas());
        assertEquals(0, juego.calcularExperiencia());
    }

    @Test
    @DisplayName("Juego perfecto (8 intentos): 50 monedas y 40 XP")
    void perfecto() {
        MinijuegoMemoria juego = ordenado();
        jugarConFallos(juego, 0);
        assertTrue(juego.estaTerminado());
        assertEquals(8, juego.getIntentos());
        assertEquals(50, juego.calcularMonedas());
        assertEquals(40, juego.calcularExperiencia());
    }

    @Test
    @DisplayName("12 intentos todavía dan 50 monedas; 13 dan 30")
    void limite12() {
        MinijuegoMemoria juego12 = ordenado();
        jugarConFallos(juego12, 4);
        assertEquals(12, juego12.getIntentos());
        assertEquals(50, juego12.calcularMonedas());

        MinijuegoMemoria juego13 = ordenado();
        jugarConFallos(juego13, 5);
        assertEquals(30, juego13.calcularMonedas());
    }

    @Test
    @DisplayName("18 intentos dan 30 monedas; 19 dan 15; siempre 40 XP")
    void limite18() {
        MinijuegoMemoria juego18 = ordenado();
        jugarConFallos(juego18, 10);
        assertEquals(18, juego18.getIntentos());
        assertEquals(30, juego18.calcularMonedas());

        MinijuegoMemoria juego19 = ordenado();
        jugarConFallos(juego19, 11);
        assertEquals(15, juego19.calcularMonedas());
        assertEquals(40, juego19.calcularExperiencia());
    }

    @Test
    @DisplayName("Después de terminar ya no se puede seleccionar")
    void terminadoNoSelecciona() {
        MinijuegoMemoria juego = ordenado();
        jugarConFallos(juego, 0);
        assertEquals(ResultadoJugada.INVALIDA, elegir(juego, 0));
        assertTrue(juego.getResumen().contains("8 intentos"));
    }
}
