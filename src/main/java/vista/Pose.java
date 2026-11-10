package vista;

import modelo.Estado;

/**
 * Poses de Cabito, cada una con su imagen PNG en
 * {@code src/main/resources/img/cabito/}. Las imágenes se recortaron de la
 * hoja de personaje de Cabito.
 * <p>
 * {@link #para(Estado)} decide qué pose se muestra en cada estado. Para
 * cambiar una imagen basta con reemplazar el PNG con el mismo nombre.
 */
public enum Pose {

    /** De pie, quieto (estado normal). */
    QUIETO("quieto.png"),
    /** Caminando (al moverse entre mundos). */
    CAMINAR("caminar.png"),
    /** Corriendo (al jugar y durante los minijuegos). */
    CORRER("correr.png"),
    /** Saltando de alegría (estado feliz). */
    SALTAR("saltar.png"),
    /** Sentado (triste, con hambre o sucio). */
    SENTADO("sentado.png"),
    /** Acostado (cansado o dormido). */
    ACOSTADO("acostado.png"),
    /** Sentado con una curita (enfermo). */
    HERIDO("herido.png"),
    /** Retrato grande sentado, para la pantalla inicial. */
    RETRATO("retrato.png");

    /** Carpeta de las poses dentro de {@code img/}. */
    private static final String CARPETA = "cabito/";

    private final String archivo;

    /**
     * @param archivo nombre del PNG dentro de {@code img/cabito/}
     */
    Pose(String archivo) {
        this.archivo = archivo;
    }

    /**
     * @return ruta de la imagen dentro de {@code img/}, por ejemplo {@code "cabito/saltar.png"}
     */
    public String getRuta() {
        return CARPETA + archivo;
    }

    /**
     * Devuelve la pose que corresponde a un estado de Cabito.
     *
     * @param estado estado actual
     * @return la pose para ese estado
     */
    public static Pose para(Estado estado) {
        switch (estado) {
            case FELIZ:
                return SALTAR;
            case TRISTE:
            case HAMBRIENTO:
            case SUCIO:
                return SENTADO;
            case CANSADO:
            case DORMIDO:
                return ACOSTADO;
            case ENFERMO:
                return HERIDO;
            case NORMAL:
            default:
                return QUIETO;
        }
    }
}
