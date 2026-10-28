package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import minijuegos.MinijuegoMemoria;
import minijuegos.MinijuegoPreguntas;
import minijuegos.Pregunta;
import modelo.RandomFijo;

/**
 * Pruebas de los paneles de minijuegos sin abrir ventanas: se crean los
 * paneles y se simulan clics con {@code doClick()}. Todo corre en el hilo
 * de Swing (EDT), como en la aplicación real.
 */
class PanelesMinijuegosTest {

    @BeforeAll
    static void sinPantalla() {
        System.setProperty("java.awt.headless", "true");
    }

    /** Ejecuta el código en el hilo de Swing y espera a que termine. */
    private static void enSwing(Runnable codigo) throws Exception {
        SwingUtilities.invokeAndWait(codigo);
    }

    @Test
    @DisplayName("Memoria: las cartas empiezan con '?' y al tocar una pareja muestran el ícono")
    void memoriaMuestraPareja() throws Exception {
        enSwing(() -> {
            MinijuegoMemoria juego = new MinijuegoMemoria(new RandomFijo());
            PanelMemoria panel = new PanelMemoria(juego, j -> { });
            assertEquals("?", panel.getBoton(0, 0).getText());
            panel.getBoton(0, 0).doClick();
            panel.getBoton(0, 1).doClick();
            assertEquals("Cabito", panel.getBoton(0, 0).getText());
            assertEquals("Cabito", panel.getBoton(0, 1).getText());
            assertTrue(panel.getTextoEstado().contains("Parejas: 1/8"));
        });
    }

    @Test
    @DisplayName("Memoria: mientras se muestran dos cartas distintas, los clics se ignoran")
    void memoriaBloqueaDuranteFallo() throws Exception {
        enSwing(() -> {
            MinijuegoMemoria juego = new MinijuegoMemoria(new RandomFijo());
            PanelMemoria panel = new PanelMemoria(juego, j -> { });
            panel.getBoton(0, 0).doClick();
            panel.getBoton(0, 2).doClick(); // no es pareja: arranca el temporizador
            panel.getBoton(1, 0).doClick(); // se ignora
            assertEquals("?", panel.getBoton(1, 0).getText());
            assertEquals(1, juego.getIntentos());
        });
    }

    @Test
    @DisplayName("Memoria: al encontrar las 8 parejas avisa una sola vez y muestra el resumen")
    void memoriaAvisaAlTerminar() throws Exception {
        AtomicInteger avisos = new AtomicInteger();
        enSwing(() -> {
            MinijuegoMemoria juego = new MinijuegoMemoria(new RandomFijo());
            PanelMemoria panel = new PanelMemoria(juego, j -> avisos.incrementAndGet());
            for (int i = 0; i < 16; i++) {
                panel.getBoton(i / 4, i % 4).doClick();
            }
            panel.getBoton(0, 0).doClick();
            assertTrue(juego.estaTerminado());
            assertTrue(panel.getTextoEstado().contains("8 intentos"));
        });
        assertEquals(1, avisos.get());
    }

    @Test
    @DisplayName("Preguntas: después de responder se ignoran las opciones y se habilita 'Siguiente'")
    void preguntasResponder() throws Exception {
        enSwing(() -> {
            MinijuegoPreguntas juego = new MinijuegoPreguntas(new RandomFijo(0, 0, 0, 0, 0));
            PanelPreguntas panel = new PanelPreguntas(juego, j -> { });
            assertFalse(panel.getBotonSiguiente().isEnabled());
            int correcta = juego.getPreguntaActual().getIndiceCorrecta();
            panel.getBotonOpcion(correcta).doClick();
            assertTrue(panel.getBotonSiguiente().isEnabled());
            assertEquals(1, juego.getCorrectas());
            panel.getBotonOpcion(correcta).doClick(); // se ignora: aún no se pulsó "Siguiente"
            assertEquals(1, juego.getCorrectas());
            assertEquals(2, juego.getNumeroPregunta());
        });
    }

    @Test
    @DisplayName("Preguntas: tras la quinta respuesta muestra el resumen y avisa una vez")
    void preguntasCompletas() throws Exception {
        AtomicInteger avisos = new AtomicInteger();
        enSwing(() -> {
            MinijuegoPreguntas juego = new MinijuegoPreguntas(new RandomFijo(0, 0, 0, 0, 0));
            PanelPreguntas panel = new PanelPreguntas(juego, j -> avisos.incrementAndGet());
            for (int i = 0; i < MinijuegoPreguntas.PREGUNTAS_POR_PARTIDA; i++) {
                Pregunta p = juego.getPreguntaActual();
                panel.getBotonOpcion(p.getIndiceCorrecta()).doClick();
                panel.getBotonSiguiente().doClick();
            }
            assertTrue(juego.estaTerminado());
            assertTrue(panel.getTextoEnunciado().contains("5 de 5"));
            panel.getBotonSiguiente().doClick();
        });
        assertEquals(1, avisos.get());
    }
}
