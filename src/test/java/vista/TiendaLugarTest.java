package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import controlador.Animacion;
import controlador.JuegoController;
import controlador.OpcionSalir;
import controlador.VistaJuego;
import minijuegos.Minijuego;
import modelo.Partida;
import modelo.items.Catalogo;
import persistencia.GestorPartidas;

/**
 * Pruebas sin pantalla de la Tienda Tadeísta como lugar: el modo tienda de
 * la escena, el mostrador de productos y la tarjeta en la barra de lugares.
 */
class TiendaLugarTest {

    /** Vista que no hace nada; el controlador trabaja sobre la partida dada. */
    private static class VistaVacia implements VistaJuego {
        @Override public void mostrarInicio() { }
        @Override public void mostrarJuego(Partida partida) { }
        @Override public void actualizar(Partida partida) { }
        @Override public void mostrarDialogo(String texto) { }
        @Override public void animar(Animacion animacion) { }
        @Override public void mostrarAviso(String titulo, String texto) { }
        @Override public void mostrarError(String texto) { }
        @Override public String pedirNombre() { return "Cabito"; }
        @Override public String elegir(String titulo, String mensaje, List<String> opciones) { return null; }
        @Override public boolean confirmar(String titulo, String mensaje) { return true; }
        @Override public OpcionSalir preguntarGuardarAntesDeSalir() { return OpcionSalir.NO_GUARDAR; }
        @Override public void abrirMinijuego(Minijuego minijuego) { }
        @Override public void cerrar() { }
    }

    /** Crea un controlador con una partida nueva llamada "Cabito". */
    private static JuegoController controladorConPartida() {
        JuegoController controlador = new JuegoController(new VistaVacia(), new GestorPartidas());
        controlador.nuevaPartida();
        return controlador;
    }

    @Test
    @DisplayName("En la tienda se ocultan las acciones, aparece 'Volver a ...' y el mostrador")
    void modoTienda() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JuegoController controlador = controladorConPartida();
            Partida partida = controlador.getPartida();
            PanelEscena escena = new PanelEscena(controlador);
            escena.actualizar(partida);
            assertFalse(escena.getPanelTienda().isVisible());
            assertFalse(escena.getBotonVolver().isVisible());
            assertTrue(escena.getBotonJugar().isVisible());

            partida.irATienda();
            escena.actualizar(partida);
            assertTrue(escena.getPanelTienda().isVisible());
            assertTrue(escena.getBotonVolver().isVisible());
            assertEquals("Volver a La Brújula", escena.getBotonVolver().getText());
            assertFalse(escena.getBotonJugar().isVisible());
            assertEquals(12, escena.getPanelTienda().getCantidadProductos());

            partida.salirDeTienda();
            escena.actualizar(partida);
            assertFalse(escena.getPanelTienda().isVisible());
            assertTrue(escena.getBotonJugar().isVisible());
        });
    }

    @Test
    @DisplayName("Al entrar, los productos aparecen cuando Cabito termina de caminar")
    void productosDespuesDeCaminar() throws Exception {
        PanelEscena[] escena = new PanelEscena[1];
        SwingUtilities.invokeAndWait(() -> {
            JuegoController controlador = controladorConPartida();
            Partida partida = controlador.getPartida();
            escena[0] = new PanelEscena(controlador);
            partida.irATienda();
            escena[0].actualizar(partida);
            escena[0].animar(Animacion.ENTRAR_TIENDA);
            assertEquals(Pose.CAMINAR, escena[0].getDibujo().getPose());
            assertFalse(escena[0].getPanelTienda().isContenidoVisible());
        });
        Thread.sleep(DibujoCabito.DURACION_CAMINATA + 500);
        SwingUtilities.invokeAndWait(() -> assertTrue(escena[0].getPanelTienda().isContenidoVisible()));
    }

    @Test
    @DisplayName("Si la caminata se interrumpe, los productos aparecen igual")
    void caminataInterrumpida() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JuegoController controlador = controladorConPartida();
            Partida partida = controlador.getPartida();
            PanelEscena escena = new PanelEscena(controlador);
            partida.irATienda();
            escena.actualizar(partida);
            escena.animar(Animacion.ENTRAR_TIENDA);
            escena.animar(Animacion.JUGAR);
            assertTrue(escena.getPanelTienda().isContenidoVisible());
        });
    }

    @Test
    @DisplayName("Comprar desde el mostrador: compra si alcanza y avisa con amabilidad si no")
    void comprarEnMostrador() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JuegoController controlador = controladorConPartida();
            Partida partida = controlador.getPartida();
            PanelTienda tienda = new PanelTienda(controlador);
            tienda.actualizar(partida);

            tienda.comprarProducto(1); // Empanada, 15 monedas
            assertTrue(tienda.getMensaje().contains("Compraste Empanada"));
            assertEquals(1, partida.getCabito().getInventario().getCantidad(Catalogo.EMPANADA));

            partida.getCabito().getInventario().setMonedas(5);
            tienda.comprarProducto(2); // Almuerzo, 35 monedas
            assertTrue(tienda.getMensaje().contains("Me faltan 30 monedas"), tienda.getMensaje());
            assertFalse(partida.getCabito().getInventario().tiene(Catalogo.ALMUERZO));
        });
    }

    @Test
    @DisplayName("La barra de lugares tiene los tres mundos y la tienda")
    void tarjetaDeTienda() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JuegoController controlador = controladorConPartida();
            PanelInferior inferior = new PanelInferior(controlador, new VistaSwing());
            inferior.actualizar(controlador.getPartida());
            assertEquals(4, inferior.getCantidadTarjetas());
        });
    }
}
