package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.items.Catalogo;
import modelo.items.Item;

/**
 * Pruebas de los íconos de los objetos ({@link IconosItems}) y de la casilla
 * del inventario ({@link CasillaItem}).
 */
class IconosItemsTest {

    /** Cuenta los píxeles que no son transparentes. */
    private static int pixelesVisibles(BufferedImage imagen) {
        int visibles = 0;
        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                if ((imagen.getRGB(x, y) >>> 24) > 0) {
                    visibles++;
                }
            }
        }
        return visibles;
    }

    @Test
    @DisplayName("Cada objeto de la tienda tiene su propia clave de ícono")
    void clavesPropias() {
        Set<String> claves = new HashSet<>();
        for (Item item : Catalogo.getItems()) {
            String clave = IconosItems.clave(item.getNombre());
            assertNotEquals(IconosItems.CLAVE_DESCONOCIDA, clave, item.getNombre());
            claves.add(clave);
        }
        assertEquals(12, claves.size());
        assertEquals(IconosItems.CLAVE_DESCONOCIDA, IconosItems.clave("Pizza"));
    }

    @Test
    @DisplayName("Los íconos tienen el tamaño pedido, fondo transparente y un dibujo en el centro")
    void iconosDibujados() {
        for (Item item : Catalogo.getItems()) {
            BufferedImage imagen = IconosItems.imagen(item.getNombre(), 64);
            assertEquals(64, imagen.getWidth(), item.getNombre());
            assertEquals(64, imagen.getHeight(), item.getNombre());
            assertEquals(0, imagen.getRGB(0, 0) >>> 24, item.getNombre() + ": la esquina debe ser transparente");
            assertTrue(pixelesVisibles(imagen) > 64 * 64 / 6, item.getNombre() + ": el dibujo está casi vacío");
        }
    }

    @Test
    @DisplayName("Los 12 íconos son distintos entre sí")
    void iconosDistintos() {
        Set<Integer> huellas = new HashSet<>();
        for (Item item : Catalogo.getItems()) {
            BufferedImage imagen = IconosItems.imagen(item.getNombre(), 48);
            int[] pixeles = imagen.getRGB(0, 0, 48, 48, null, 0, 48);
            huellas.add(Arrays.hashCode(pixeles));
        }
        assertEquals(12, huellas.size());
    }

    @Test
    @DisplayName("Cada ícono se dibuja una sola vez por tamaño (caché)")
    void cache() {
        assertSame(IconosItems.imagen(Catalogo.TINTO, 40), IconosItems.imagen(Catalogo.TINTO, 40));
    }

    @Test
    @DisplayName("La casilla muestra la cantidad y el botón según el objeto")
    void casilla() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            CasillaItem empanadas = new CasillaItem(Catalogo.buscar(Catalogo.EMPANADA), 3, false,
                    "Comer", true, () -> { });
            assertEquals("x3", empanadas.getTextoCantidad());
            assertEquals("Comer", empanadas.getBoton().getText());
            assertTrue(empanadas.getBoton().isEnabled());

            CasillaItem gorra = new CasillaItem(Catalogo.buscar(Catalogo.GORRA), 1, true,
                    "Equipado", false, () -> { });
            assertEquals("x1", gorra.getTextoCantidad());
            assertFalse(gorra.getBoton().isEnabled());
        });
    }

    @Test
    @DisplayName("El botón de la casilla ejecuta su acción")
    void casillaAccion() throws Exception {
        boolean[] usado = {false};
        SwingUtilities.invokeAndWait(() -> {
            CasillaItem casilla = new CasillaItem(Catalogo.buscar(Catalogo.KIT_ASEO), 2, false,
                    "Usar", true, () -> usado[0] = true);
            casilla.getBoton().doClick();
        });
        assertTrue(usado[0]);
    }
}
