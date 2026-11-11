package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import controlador.JuegoController;
import modelo.Partida;
import modelo.items.Catalogo;
import persistencia.GestorPartidas;

/**
 * Pruebas del dibujo de accesorios sobre Cabito y de su marca en el inventario.
 */
class AccesoriosCabitoTest {

    private static final Set<String> TODOS = Set.of(Catalogo.GORRA, Catalogo.SACO, Catalogo.TERMO, Catalogo.MOCHILA);

    /** Dibuja a Cabito en una pose con ciertos accesorios y devuelve los píxeles. */
    private static int[] pintar(Pose pose, Set<String> accesorios) {
        DibujoCabito dibujo = new DibujoCabito();
        dibujo.fijarPose(pose);
        dibujo.setAccesorios(accesorios);
        dibujo.setSize(420, 380);
        BufferedImage imagen = new BufferedImage(420, 380, BufferedImage.TYPE_INT_ARGB);
        dibujo.paint(imagen.getGraphics());
        return imagen.getRGB(0, 0, 420, 380, null, 0, 420);
    }

    @Test
    @DisplayName("Cada pose de juego tiene anclas razonables; el retrato no lleva accesorios")
    void anclasPorPose() {
        for (Pose pose : Pose.values()) {
            AccesoriosCabito.Anclas anclas = AccesoriosCabito.anclas(pose);
            if (pose == Pose.RETRATO) {
                assertNull(anclas);
                continue;
            }
            assertNotNull(anclas, pose.name());
            for (AccesoriosCabito.Ancla ancla : new AccesoriosCabito.Ancla[] {
                anclas.gorra(), anclas.saco(), anclas.mochila()}) {
                assertTrue(ancla.x() >= 0 && ancla.x() <= 1, pose + ": x fuera de la imagen");
                assertTrue(ancla.y() >= 0 && ancla.y() <= 1, pose + ": y fuera de la imagen");
            }
            // La gorra va arriba (en la cabeza) y el termo en el piso
            assertTrue(anclas.gorra().y() < 0.2, pose + ": la gorra debe ir arriba");
            assertTrue(anclas.termo().y() > 0.9, pose + ": el termo debe ir en el piso");
        }
    }

    @Test
    @DisplayName("Ponerse accesorios cambia el dibujo de Cabito en todas las poses")
    void accesoriosSeDibujan() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            for (Pose pose : Pose.values()) {
                if (pose == Pose.RETRATO) {
                    continue;
                }
                int[] sin = pintar(pose, Set.of());
                for (String accesorio : TODOS) {
                    int[] con = pintar(pose, Set.of(accesorio));
                    assertFalse(Arrays.equals(sin, con), pose + " con " + accesorio + " se ve igual que sin él");
                }
            }
        });
    }

    @Test
    @DisplayName("La escena le pasa al dibujo los accesorios puestos")
    void escenaPasaAccesorios() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Partida partida = new Partida("Cabito");
            partida.getCabito().getInventario().setMonedas(500);
            partida.comprar(Catalogo.GORRA);
            partida.comprar(Catalogo.TERMO);
            partida.usarItem(Catalogo.GORRA);
            PanelEscena escena = new PanelEscena(new JuegoController(null, new GestorPartidas()));
            escena.actualizar(partida);
            assertEquals(Set.of(Catalogo.GORRA), escena.getDibujo().getAccesorios());

            partida.quitarAccesorio(Catalogo.GORRA);
            escena.actualizar(partida);
            assertTrue(escena.getDibujo().getAccesorios().isEmpty());
        });
    }

    @Test
    @DisplayName("La casilla de un accesorio puesto queda marcada y su botón dice Quitar")
    void casillaMarcada() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            CasillaItem puesta = new CasillaItem(Catalogo.buscar(Catalogo.GORRA), 1, true, "Quitar", true, () -> { });
            assertTrue(puesta.isEquipado());
            assertEquals("Quitar", puesta.getBoton().getText());
            assertTrue(puesta.getBoton().isEnabled());

            CasillaItem guardada = new CasillaItem(Catalogo.buscar(Catalogo.TERMO), 1, false, "Poner", true, () -> { });
            assertFalse(guardada.isEquipado());
        });
    }
}
