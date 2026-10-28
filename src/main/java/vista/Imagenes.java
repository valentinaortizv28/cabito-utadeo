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
 * Si una imagen no existe, devuelve {@code null} y la vista dibuja una
 * figura con Graphics2D en su lugar. Así, para cambiar el aspecto de Cabito
 * o de un mundo basta con poner un PNG con el nombre correcto (por ejemplo
 * {@code cabito_feliz.png} o {@code fondo_biblioteca.png}) en esa carpeta.
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
