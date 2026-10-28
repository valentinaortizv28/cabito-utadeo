package minijuegos;

/**
 * Comportamiento común de todos los minijuegos.
 * <p>
 * La partida no necesita saber qué minijuego se jugó: al terminar le pide
 * las monedas y la experiencia a través de esta interfaz.
 */
public interface Minijuego {

    /**
     * @return nombre del minijuego para mostrar en pantalla
     */
    String getNombre();

    /**
     * @return explicación corta de cómo se juega
     */
    String getInstrucciones();

    /**
     * @return nombre del mundo donde está el minijuego (define el nivel para jugarlo)
     */
    String getNombreMundo();

    /**
     * @return {@code true} cuando la partida del minijuego terminó
     */
    boolean estaTerminado();

    /**
     * Calcula las monedas ganadas según el desempeño.
     *
     * @return monedas ganadas; 0 si aún no ha terminado
     */
    int calcularMonedas();

    /**
     * Calcula la experiencia ganada.
     *
     * @return experiencia ganada; 0 si aún no ha terminado
     */
    int calcularExperiencia();

    /**
     * @return texto con el resultado final para mostrar al usuario
     */
    String getResumen();
}
