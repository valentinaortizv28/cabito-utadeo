package vista;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Carga imágenes PNG desde {@code src/main/resources/img/}.
 * <p>
 * Las imágenes se buscan con {@link Class#getResource(String)}, es decir, en
 * el <i>classpath</i>: así funcionan igual al ejecutar desde IntelliJ (carpeta
 * {@code target/classes}) que desde un archivo {@code .jar}.
 * <p>
 * Si una imagen no existe, devuelve {@code null} y la vista dibuja otra cosa
 * en su lugar: un fondo sencillo para los mundos, o un aviso para Cabito.
 * Las poses de Cabito están en {@code img/cabito/} (ver {@link Pose}) y los
 * fondos opcionales de los mundos se llaman, por ejemplo,
 * {@code fondo_biblioteca.png}.
 */
public final class Imagenes {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    /** No se crean objetos de esta clase: todo es estático. */
    private Imagenes() {
    }

    /**
     * Busca una imagen por su nombre de archivo. Cada imagen se lee una sola
     * vez y luego se guarda en memoria.
     *
     * @param nombreArchivo nombre del archivo dentro de {@code img/}
     * @return la imagen, o {@code null} si no existe o no se pudo leer
     */
    public static BufferedImage cargar(String nombreArchivo) {
        if (!CACHE.containsKey(nombreArchivo)) {
            CACHE.put(nombreArchivo, leer(nombreArchivo));
        }
        return CACHE.get(nombreArchivo);
    }

    /**
     * Lee la imagen del classpath.
     *
     * @param nombreArchivo nombre del archivo dentro de {@code img/}
     * @return la imagen o {@code null}
     */
    private static BufferedImage leer(String nombreArchivo) {
        URL url = Imagenes.class.getResource("/img/" + nombreArchivo);
        if (url == null) {
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            return null;
        }
    }
}
