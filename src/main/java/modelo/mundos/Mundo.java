package modelo.mundos;

import java.util.Random;

import modelo.Cabito;

/**
 * Lugar de la universidad que Cabito puede visitar. Cada mundo tiene una
 * actividad propia y un nivel mínimo para entrar.
 * <p>
 * Usa el patrón <i>Template Method</i>: {@link #realizarActividad} hace los
 * pasos comunes a todos los mundos (revisar nivel y estado, dar experiencia)
 * y cada subclase solo escribe lo propio en {@link #hacerActividad}.
 */
public abstract class Mundo {

    private final String nombre;
    private final String descripcion;
    private final String nombreActividad;
    private final int nivelRequerido;
    private final String nombreFondo;

    /**
     * Crea un mundo.
     *
     * @param nombre          nombre del mundo
     * @param descripcion     texto corto para la tarjeta del mundo
     * @param nombreActividad nombre del botón de la actividad
     * @param nivelRequerido  nivel mínimo para entrar
     * @param nombreFondo     archivo de imagen de fondo en {@code img/}
     */
    protected Mundo(String nombre, String descripcion, String nombreActividad,
                    int nivelRequerido, String nombreFondo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nombreActividad = nombreActividad;
        this.nivelRequerido = nivelRequerido;
        this.nombreFondo = nombreFondo;
    }

    /**
     * Intenta hacer la actividad del mundo con Cabito:
     * <ol>
     *   <li>Revisa que el nivel alcance para este mundo.</li>
     *   <li>Revisa que el estado lo permita (no dormido ni enfermo).</li>
     *   <li>Hace la actividad propia del mundo.</li>
     *   <li>Da la experiencia y recalcula el estado.</li>
     * </ol>
     *
     * @param cabito la mascota que hace la actividad
     * @param random generador de números aleatorios (se recibe para poder
     *               controlarlo en las pruebas)
     * @return el resultado, con el mensaje para el usuario
     */
    public final ResultadoActividad realizarActividad(Cabito cabito, Random random) {
        if (!estaDesbloqueado(cabito.getNivel())) {
            return ResultadoActividad.fallo(getMensajeBloqueado());
        }
        if (!cabito.getEstado().permiteActividades()) {
            return ResultadoActividad.fallo(cabito.getNombre() + " no puede hacer actividades ahora: "
                    + cabito.getEstado().getDescripcion());
        }
        ResultadoActividad resultado = hacerActividad(cabito, random);
        boolean subio = cabito.ganarExperiencia(resultado.getExperiencia());
        resultado.setSubioNivel(subio);
        cabito.actualizarEstado();
        return resultado;
    }

    /**
     * Aplica los efectos propios de la actividad de este mundo. No debe dar
     * experiencia: la da {@link #realizarActividad} con el valor que se
     * devuelva en el resultado.
     *
     * @param cabito la mascota que hace la actividad
     * @param random generador de números aleatorios
     * @return resultado creado con {@link ResultadoActividad#exito}
     */
    protected abstract ResultadoActividad hacerActividad(Cabito cabito, Random random);

    /**
     * Indica si un nivel alcanza para entrar a este mundo.
     *
     * @param nivel nivel de Cabito
     * @return {@code true} si el nivel es suficiente
     */
    public boolean estaDesbloqueado(int nivel) {
        return nivel >= nivelRequerido;
    }

    /**
     * @return texto para la tarjeta de un mundo bloqueado, por ejemplo "Nivel 2 requerido"
     */
    public String getMensajeBloqueado() {
        return "Nivel " + nivelRequerido + " requerido";
    }

    /**
     * @return nombre del mundo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return descripción corta del mundo
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @return nombre de la actividad, por ejemplo "Estudiar"
     */
    public String getNombreActividad() {
        return nombreActividad;
    }

    /**
     * @return nivel mínimo para entrar
     */
    public int getNivelRequerido() {
        return nivelRequerido;
    }

    /**
     * @return archivo de imagen de fondo
     */
    public String getNombreFondo() {
        return nombreFondo;
    }

    /**
     * Devuelve el nombre, para que el mundo se vea bien en listas de Swing.
     *
     * @return nombre del mundo
     */
    @Override
    public String toString() {
        return nombre;
    }
}
