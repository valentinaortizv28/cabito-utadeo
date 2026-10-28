package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import controlador.JuegoController;
import controlador.OpcionSalir;
import controlador.VistaJuego;
import minijuegos.Minijuego;
import modelo.Cabito;
import modelo.Estado;
import modelo.Partida;
import persistencia.GestorPartidas;

/**
 * Pruebas sin pantalla de los paneles de la ventana principal: que reflejen
 * el estado del modelo (dibujo de Cabito, botones habilitados, barras y
 * monedas). Las ventanas ({@code JFrame}/{@code JDialog}) no se pueden
 * crear sin pantalla, así que se prueban los paneles por separado.
 */
class PanelesPrincipalesTest {

    /** Vista que no hace nada: los paneles solo necesitan un controlador. */
    private static class VistaVacia implements VistaJuego {
        @Override public void mostrarInicio() { }
        @Override public void mostrarJuego(Partida partida) { }
        @Override public void actualizar(Partida partida) { }
        @Override public void mostrarDialogo(String texto) { }
        @Override public void mostrarAviso(String titulo, String texto) { }
        @Override public void mostrarError(String texto) { }
        @Override public String pedirNombre() { return "Cabito"; }
        @Override public String elegir(String titulo, String mensaje, List<String> opciones) { return null; }
        @Override public boolean confirmar(String titulo, String mensaje) { return true; }
        @Override public OpcionSalir preguntarGuardarAntesDeSalir() { return OpcionSalir.NO_GUARDAR; }
        @Override public void abrirMinijuego(Minijuego minijuego) { }
        @Override public void cerrar() { }
    }

    private static void enSwing(Runnable codigo) throws Exception {
        SwingUtilities.invokeAndWait(codigo);
    }

    private static JuegoController controlador() {
        return new JuegoController(new VistaVacia(), new GestorPartidas());
    }

    @Test
    @DisplayName("La escena dibuja a Cabito según su estado y deshabilita botones si duerme")
    void escenaSegunEstado() throws Exception {
        enSwing(() -> {
            Partida partida = new Partida("Cabito");
            Cabito cabito = partida.getCabito();
            PanelEscena escena = new PanelEscena(controlador());

            escena.actualizar(partida);
            assertEquals(Estado.NORMAL, escena.getDibujo().getEstado());
            assertTrue(escena.getBotonJugar().isEnabled());
            assertEquals("Dormir", escena.getBotonDormir().getText());

            cabito.setEnergia(40);
            cabito.dormir();
            escena.actualizar(partida);
            assertEquals(Estado.DORMIDO, escena.getDibujo().getEstado());
            assertFalse(escena.getBotonJugar().isEnabled());
            assertEquals("Despertar", escena.getBotonDormir().getText());
            assertTrue(escena.getBotonDormir().isEnabled());
        });
    }

    @Test
    @DisplayName("El globo de diálogo muestra el texto escapado")
    void globo() throws Exception {
        enSwing(() -> {
            PanelEscena escena = new PanelEscena(controlador());
            escena.setMensaje("Hola <b>Cabito</b> & amigos");
            assertTrue(escena.getMensaje().contains("Hola &lt;b&gt;Cabito&lt;/b&gt; &amp; amigos"));
        });
    }

    @Test
    @DisplayName("La barra de hambre muestra 100 - saciedad y se pone roja con mucha hambre")
    void barraHambre() throws Exception {
        enSwing(() -> {
            Cabito cabito = new Cabito("Cabito");
            cabito.setSaciedad(20);
            PanelAtributos atributos = new PanelAtributos();
            atributos.actualizar(cabito);
            assertEquals(80, atributos.getBarraHambre().getValue());
            assertEquals(Estilo.INCORRECTO, atributos.getBarraHambre().getForeground());
        });
    }

    @Test
    @DisplayName("La barra superior muestra las monedas")
    void barraSuperior() throws Exception {
        enSwing(() -> {
            Partida partida = new Partida("Cabito");
            partida.getCabito().getInventario().setMonedas(77);
            BarraSuperior barra = new BarraSuperior(controlador());
            barra.actualizar(partida);
            assertEquals("Monedas: 77", barra.getTextoMonedas());
        });
    }

    @Test
    @DisplayName("Colores de los atributos: verde, ámbar y rojo")
    void colores() {
        assertEquals(Estilo.CORRECTO, Estilo.colorAtributo(60));
        assertEquals(Estilo.ADVERTENCIA, Estilo.colorAtributo(59));
        assertEquals(Estilo.ADVERTENCIA, Estilo.colorAtributo(30));
        assertEquals(Estilo.INCORRECTO, Estilo.colorAtributo(29));
        assertEquals("Hambriento", Estilo.nombreBonito("HAMBRIENTO"));
    }
}
