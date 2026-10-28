package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import modelo.Cabito;
import modelo.Partida;
import modelo.items.Inventario;

/**
 * Guarda y carga partidas como archivos JSON dentro de una carpeta
 * (por defecto {@code partidas/}). Cada partida se guarda en un archivo con
 * el nombre de Cabito, por ejemplo {@code partidas/Cabito.json}.
 * <p>
 * Todos los errores se convierten en {@link PersistenciaException} con un
 * mensaje para el usuario, para que la interfaz nunca muestre stack traces.
 */
public class GestorPartidas {

    /** Carpeta donde se guardan las partidas si no se indica otra. */
    public static final String CARPETA_POR_DEFECTO = "partidas";
    /** Extensión de los archivos de partida. */
    public static final String EXTENSION = ".json";

    private final Path carpeta;
    private final Gson gson;

    /**
     * Crea un gestor que usa la carpeta {@code partidas/} del directorio
     * desde donde se ejecuta el juego.
     */
    public GestorPartidas() {
        this(Paths.get(CARPETA_POR_DEFECTO));
    }

    /**
     * Crea un gestor que usa otra carpeta (útil en las pruebas).
     *
     * @param carpeta carpeta donde se guardan las partidas
     */
    public GestorPartidas(Path carpeta) {
        this.carpeta = carpeta;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // ------------------------------------------------------------------
    // Guardar
    // ------------------------------------------------------------------

    /**
     * Guarda la partida en un archivo JSON. Si ya existía una partida con
     * el mismo nombre, se reemplaza. La carpeta se crea si no existe.
     *
     * @param partida partida a guardar
     * @return ruta del archivo escrito
     * @throws PersistenciaException si no se pudo escribir el archivo
     */
    public Path guardar(Partida partida) throws PersistenciaException {
        String nombre = partida.getCabito().getNombre();
        Path archivo = carpeta.resolve(nombreArchivo(nombre));
        try {
            Files.createDirectories(carpeta);
            String json = gson.toJson(aDatos(partida));
            Files.writeString(archivo, json, StandardCharsets.UTF_8);
            return archivo;
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar la partida \"" + nombre
                    + "\". Revisa que la carpeta '" + carpeta + "' se pueda escribir.", e);
        }
    }

    // ------------------------------------------------------------------
    // Cargar
    // ------------------------------------------------------------------

    /**
     * Carga una partida guardada.
     *
     * @param nombre nombre de la partida (como lo devuelve {@link #listarPartidas()})
     * @return la partida reconstruida
     * @throws PersistenciaException si no existe, no se puede leer o el
     *                               archivo está dañado
     */
    public Partida cargar(String nombre) throws PersistenciaException {
        Path archivo = carpeta.resolve(nombreArchivo(nombre));
        if (!Files.isRegularFile(archivo)) {
            throw new PersistenciaException("No existe una partida guardada con el nombre \"" + nombre + "\".");
        }
        String json;
        try {
            json = Files.readString(archivo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer la partida \"" + nombre + "\".", e);
        }
        DatosPartida datos;
        try {
            datos = gson.fromJson(json, DatosPartida.class);
        } catch (JsonParseException e) {
            throw new PersistenciaException("El archivo de la partida \"" + nombre
                    + "\" está dañado y no se puede cargar.", e);
        }
        if (datos == null || datos.nombre == null || datos.nombre.isBlank()) {
            throw new PersistenciaException("El archivo de la partida \"" + nombre
                    + "\" está incompleto y no se puede cargar.");
        }
        if (datos.version > DatosPartida.VERSION_ACTUAL) {
            throw new PersistenciaException("La partida \"" + nombre
                    + "\" fue guardada con una versión más nueva del juego.");
        }
        return aPartida(datos);
    }

    /**
     * Lista los nombres de las partidas guardadas, en orden alfabético.
     *
     * @return nombres sin la extensión; lista vacía si no hay ninguna
     * @throws PersistenciaException si la carpeta existe pero no se puede leer
     */
    public List<String> listarPartidas() throws PersistenciaException {
        List<String> nombres = new ArrayList<>();
        if (!Files.isDirectory(carpeta)) {
            return nombres;
        }
        try (DirectoryStream<Path> archivos = Files.newDirectoryStream(carpeta, "*" + EXTENSION)) {
            for (Path archivo : archivos) {
                String nombreArchivo = archivo.getFileName().toString();
                nombres.add(nombreArchivo.substring(0, nombreArchivo.length() - EXTENSION.length()));
            }
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer la carpeta de partidas '" + carpeta + "'.", e);
        }
        Collections.sort(nombres);
        return nombres;
    }

    /**
     * Indica si ya hay una partida guardada con ese nombre (para avisar
     * antes de sobrescribirla).
     *
     * @param nombre nombre de la partida
     * @return {@code true} si el archivo existe
     */
    public boolean existe(String nombre) {
        return Files.isRegularFile(carpeta.resolve(nombreArchivo(nombre)));
    }

    /**
     * Convierte el nombre de una partida en un nombre de archivo seguro en
     * cualquier sistema operativo: quita las tildes (la ñ queda como n) y
     * cambia por "_" todo lo que no sea letra, número, espacio, guion o
     * guion bajo. Se usa solo ASCII porque algunos sistemas no aceptan
     * tildes en los nombres de archivo. El nombre real de Cabito, con
     * tildes, se guarda dentro del JSON.
     *
     * @param nombre nombre de la partida
     * @return nombre de archivo con extensión, por ejemplo {@code "Cabito.json"}
     */
    public static String nombreArchivo(String nombre) {
        String sinTildes = nombre == null ? ""
                : Normalizer.normalize(nombre.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String limpio = sinTildes.replaceAll("[^A-Za-z0-9 _-]", "_");
        if (limpio.isEmpty()) {
            limpio = "partida";
        }
        return limpio + EXTENSION;
    }

    // ------------------------------------------------------------------
    // Traducción Partida ↔ DatosPartida
    // ------------------------------------------------------------------

    /**
     * Copia el estado de una partida a un objeto de datos plano.
     *
     * @param partida partida de origen
     * @return datos listos para convertir a JSON
     */
    DatosPartida aDatos(Partida partida) {
        Cabito cabito = partida.getCabito();
        Inventario inventario = cabito.getInventario();
        DatosPartida datos = new DatosPartida();
        datos.fechaGuardado = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        datos.nombre = cabito.getNombre();
        datos.saciedad = cabito.getSaciedad();
        datos.energia = cabito.getEnergia();
        datos.felicidad = cabito.getFelicidad();
        datos.higiene = cabito.getHigiene();
        datos.inteligencia = cabito.getInteligencia();
        datos.nivel = cabito.getNivel();
        datos.experiencia = cabito.getExperiencia();
        datos.dormido = cabito.isDormido();

        datos.monedas = inventario.getMonedas();
        datos.inventario.putAll(inventario.getCantidades());
        datos.accesoriosEquipados.addAll(inventario.getEquipados());

        datos.mundoActual = partida.getMundoActual().getNombre();
        datos.actividadesPorMundo.putAll(partida.getActividadesPorMundo());

        datos.historial.addAll(partida.getHistorial().getAcciones());
        datos.logros.addAll(partida.getHistorial().getLogros());
        return datos;
    }

    /**
     * Reconstruye una partida a partir de los datos leídos del archivo.
     * Los valores fuera de rango se corrigen con los setters del modelo y
     * los datos que falten (por ejemplo, si alguien editó el archivo a mano)
     * se reemplazan por valores vacíos.
     *
     * @param datos datos leídos del JSON
     * @return la partida reconstruida
     */
    Partida aPartida(DatosPartida datos) {
        Partida partida = new Partida(datos.nombre);
        Cabito cabito = partida.getCabito();

        cabito.setSaciedad(datos.saciedad);
        cabito.setEnergia(datos.energia);
        cabito.setFelicidad(datos.felicidad);
        cabito.setHigiene(datos.higiene);
        cabito.setInteligencia(datos.inteligencia);
        cabito.setNivel(datos.nivel);
        cabito.setExperiencia(datos.experiencia);
        cabito.setDormido(datos.dormido);

        Inventario inventario = cabito.getInventario();
        inventario.setMonedas(datos.monedas);
        inventario.restaurar(noNulo(datos.inventario), noNulo(datos.accesoriosEquipados));

        partida.restaurarProgreso(datos.mundoActual, noNulo(datos.actividadesPorMundo));
        partida.getHistorial().restaurar(noNulo(datos.historial), noNulo(datos.logros));

        cabito.actualizarEstado();
        return partida;
    }

    /** Devuelve la lista, o una vacía si en el archivo no venía. */
    private static List<String> noNulo(List<String> lista) {
        return lista == null ? List.of() : lista;
    }

    /** Devuelve el mapa, o uno vacío si en el archivo no venía. */
    private static Map<String, Integer> noNulo(Map<String, Integer> mapa) {
        return mapa == null ? Map.of() : mapa;
    }
}
