package persistencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Copia "plana" de una partida, tal como se escribe en el archivo JSON.
 * <p>
 * Solo tiene textos, números, listas y mapas, que Gson sabe convertir sin
 * configuración. Así el archivo es fácil de leer y el modelo no tiene que
 * adaptarse a Gson. {@link GestorPartidas} hace la traducción en ambos
 * sentidos.
 */
class DatosPartida {

    /** Versión del formato del archivo, por si cambia en el futuro. */
    static final int VERSION_ACTUAL = 1;

    int version = VERSION_ACTUAL;
    String fechaGuardado;

    // Cabito
    String nombre;
    int saciedad;
    int energia;
    int felicidad;
    int higiene;
    int inteligencia;
    int nivel;
    int experiencia;
    boolean dormido;

    // Inventario
    int monedas;
    Map<String, Integer> inventario = new LinkedHashMap<>();
    List<String> accesoriosEquipados = new ArrayList<>();

    // Progreso en los mundos
    String mundoActual;
    Map<String, Integer> actividadesPorMundo = new LinkedHashMap<>();

    // Historial
    List<String> historial = new ArrayList<>();
    List<String> logros = new ArrayList<>();
}
