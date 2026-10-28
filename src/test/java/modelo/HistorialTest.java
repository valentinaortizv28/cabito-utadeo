package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Historial}.
 */
class HistorialTest {

    private Historial historial;

    @BeforeEach
    void setUp() {
        historial = new Historial();
    }

    @Test
    @DisplayName("Las acciones se guardan en orden de llegada")
    void orden() {
        historial.registrar("A");
        historial.registrar("B");
        historial.registrar("C");
        assertEquals(List.of("A", "B", "C"), historial.getAcciones());
    }

    @Test
    @DisplayName("Al pasar el máximo se borra la acción más antigua")
    void tope() {
        for (int i = 1; i <= Historial.MAXIMO_ACCIONES + 5; i++) {
            historial.registrar("Acción " + i);
        }
        assertEquals(Historial.MAXIMO_ACCIONES, historial.getAcciones().size());
        assertEquals("Acción 6", historial.getAcciones().get(0));
    }

    @Test
    @DisplayName("Las últimas acciones salen de la más nueva a la más vieja")
    void ultimas() {
        historial.registrar("A");
        historial.registrar("B");
        historial.registrar("C");
        assertEquals(List.of("C", "B"), historial.getUltimasAcciones(2));
        assertEquals(List.of("C", "B", "A"), historial.getUltimasAcciones(10));
    }

    @Test
    @DisplayName("Un logro queda en logros y también en acciones")
    void logro() {
        historial.registrarLogro("Nivel 2");
        assertEquals(List.of("Nivel 2"), historial.getLogros());
        assertTrue(historial.getAcciones().get(0).contains("Nivel 2"));
    }

    @Test
    @DisplayName("Los logros no se pierden aunque se borren acciones viejas")
    void logrosNoSeBorran() {
        historial.registrarLogro("Primer logro");
        for (int i = 0; i < Historial.MAXIMO_ACCIONES; i++) {
            historial.registrar("Acción");
        }
        assertEquals(List.of("Primer logro"), historial.getLogros());
    }

    @Test
    @DisplayName("Las listas que se devuelven no se pueden modificar desde afuera")
    void soloLectura() {
        assertThrows(UnsupportedOperationException.class, () -> historial.getAcciones().add("X"));
        assertThrows(UnsupportedOperationException.class, () -> historial.getLogros().add("X"));
    }
}
