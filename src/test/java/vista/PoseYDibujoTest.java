package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.Estado;

/**
 * Pruebas de {@link Pose} (qué imagen corresponde a cada estado y que las
 * imágenes existan) y de las poses temporales y fijas de {@link DibujoCabito}.
 */
class PoseYDibujoTest {

    @Test
    @DisplayName("Cada estado tiene la pose pedida")
    void poseSegunEstado() {
        assertEquals(Pose.QUIETO, Pose.para(Estado.NORMAL));
        assertEquals(Pose.SALTAR, Pose.para(Estado.FELIZ));
        assertEquals(Pose.SENTADO, Pose.para(Estado.TRISTE));
        assertEquals(Pose.SENTADO, Pose.para(Estado.HAMBRIENTO));
        assertEquals(Pose.SENTADO, Pose.para(Estado.SUCIO));
        assertEquals(Pose.ACOSTADO, Pose.para(Estado.CANSADO));
        assertEquals(Pose.ACOSTADO, Pose.para(Estado.DORMIDO));
        assertEquals(Pose.HERIDO, Pose.para(Estado.ENFERMO));
    }

    @Test
    @DisplayName("Todas las imágenes de poses están en el classpath y tienen fondo transparente")
    void imagenesExisten() throws Exception {
        for (Pose pose : Pose.values()) {
            URL url = getClass().getResource("/img/" + pose.getRuta());
            assertNotNull(url, "Falta " + pose.getRuta());
            BufferedImage imagen = ImageIO.read(url);
            assertTrue(imagen.getColorModel().hasAlpha(), pose.getRuta() + " no tiene canal alfa");
            int alfaEsquina = imagen.getRGB(0, 0) >>> 24;
            assertEquals(0, alfaEsquina, pose.getRuta() + " no tiene la esquina transparente");
            assertNotNull(Imagenes.cargar(pose.getRuta()));
        }
    }

    @Test
    @DisplayName("Prioridad de poses: fija, luego temporal, luego la del estado")
    void prioridadDePoses() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            DibujoCabito dibujo = new DibujoCabito();
            dibujo.setEstado(Estado.FELIZ);
            assertEquals(Pose.SALTAR, dibujo.getPose());
            assertFalse(dibujo.isAnimando());

            dibujo.mostrarPoseTemporal(Pose.CORRER);
            assertEquals(Pose.CORRER, dibujo.getPose());
            assertTrue(dibujo.isAnimando());

            dibujo.fijarPose(Pose.HERIDO);
            assertEquals(Pose.HERIDO, dibujo.getPose());
            dibujo.soltarPose();
            assertEquals(Pose.CORRER, dibujo.getPose());
        });
    }

    @Test
    @DisplayName("Al caminar se ve la pose Caminar y luego vuelve a la del estado")
    void caminataTermina() throws Exception {
        DibujoCabito[] dibujo = new DibujoCabito[1];
        SwingUtilities.invokeAndWait(() -> {
            dibujo[0] = new DibujoCabito();
            dibujo[0].setEstado(Estado.NORMAL);
            dibujo[0].animarCaminata();
            assertEquals(Pose.CAMINAR, dibujo[0].getPose());
        });
        Thread.sleep(DibujoCabito.DURACION_CAMINATA + 500);
        SwingUtilities.invokeAndWait(() -> {
            assertFalse(dibujo[0].isAnimando());
            assertEquals(Pose.QUIETO, dibujo[0].getPose());
        });
    }
}
