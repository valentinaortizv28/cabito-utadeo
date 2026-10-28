package persistencia;

/**
 * Error al guardar o cargar una partida. Su mensaje está escrito para
 * mostrárselo directamente al usuario (sin detalles técnicos).
 */
public class PersistenciaException extends Exception {

    /**
     * Crea el error.
     *
     * @param mensaje texto amigable para el usuario
     * @param causa   error técnico original (para depurar, no para mostrar)
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    /**
     * Crea el error sin causa técnica.
     *
     * @param mensaje texto amigable para el usuario
     */
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
