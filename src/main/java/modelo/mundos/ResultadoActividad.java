package modelo.mundos;

/**
 * Resultado de intentar una actividad en un mundo: si se pudo hacer, el
 * mensaje para el usuario y lo que se ganó.
 */
public class ResultadoActividad {

    private final boolean realizada;
    private final String mensaje;
    private final int monedas;
    private final int experiencia;
    private boolean subioNivel;

    /**
     * Crea un resultado. Se usan los métodos {@link #exito} y {@link #fallo}.
     */
    private ResultadoActividad(boolean realizada, String mensaje, int monedas, int experiencia) {
        this.realizada = realizada;
        this.mensaje = mensaje;
        this.monedas = monedas;
        this.experiencia = experiencia;
    }

    /**
     * Crea el resultado de una actividad que sí se hizo.
     *
     * @param mensaje     texto para el usuario
     * @param monedas     monedas ganadas (ya sumadas al inventario)
     * @param experiencia experiencia que da la actividad
     * @return el resultado
     */
    public static ResultadoActividad exito(String mensaje, int monedas, int experiencia) {
        return new ResultadoActividad(true, mensaje, monedas, experiencia);
    }

    /**
     * Crea el resultado de una actividad que no se pudo hacer.
     *
     * @param mensaje motivo para el usuario
     * @return el resultado, sin ganancias
     */
    public static ResultadoActividad fallo(String mensaje) {
        return new ResultadoActividad(false, mensaje, 0, 0);
    }

    /**
     * @return {@code true} si la actividad se hizo
     */
    public boolean isRealizada() {
        return realizada;
    }

    /**
     * @return texto para mostrar al usuario
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @return monedas ganadas
     */
    public int getMonedas() {
        return monedas;
    }

    /**
     * @return experiencia ganada
     */
    public int getExperiencia() {
        return experiencia;
    }

    /**
     * @return {@code true} si con esta actividad Cabito subió de nivel
     */
    public boolean isSubioNivel() {
        return subioNivel;
    }

    /**
     * Lo marca {@link Mundo} después de dar la experiencia.
     *
     * @param subioNivel si Cabito subió de nivel
     */
    void setSubioNivel(boolean subioNivel) {
        this.subioNivel = subioNivel;
    }
}
