package modelo;

/**
 * Estados automáticos en los que puede estar Cabito.
 * <p>
 * El estado no lo elige el usuario: lo calcula {@link Cabito#actualizarEstado()}
 * a partir de los atributos. Cada estado define la frase de Cabito y qué
 * acciones están permitidas, de modo que la vista y el controlador solo tienen
 * que preguntarle al estado. La imagen de cada estado la decide la vista
 * ({@code vista.Pose}), porque el modelo no sabe nada de imágenes.
 */
public enum Estado {

    /** Cabito está durmiendo; solo se le puede despertar. */
    DORMIDO("Zzz... déjame dormir un rato más."),
    /** Saciedad o higiene muy bajas; necesita cuidados urgentes. */
    ENFERMO("No me siento bien... necesito que me cuides."),
    /** Saciedad baja. */
    HAMBRIENTO("¡Tengo hambre! ¿Vamos a la cafetería?"),
    /** Energía baja. */
    CANSADO("Estoy muy cansado..."),
    /** Higiene baja. */
    SUCIO("Creo que necesito un baño."),
    /** Felicidad baja. */
    TRISTE("Me siento un poco triste."),
    /** Felicidad alta y todo lo demás en buen nivel. */
    FELIZ("¡Qué buen día en la Tadeo!"),
    /** Ningún otro estado aplica. */
    NORMAL("¡Hola! ¿Qué hacemos hoy?");

    private final String descripcion;

    /**
     * Crea un estado con su mensaje.
     *
     * @param descripcion frase que dice Cabito en este estado
     */
    Estado(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve la frase que dice Cabito en este estado (globo de diálogo).
     *
     * @return descripción del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Indica si en este estado solo se permite la acción "Despertar".
     *
     * @return {@code true} únicamente para {@link #DORMIDO}
     */
    public boolean soloPermiteDespertar() {
        return this == DORMIDO;
    }

    /**
     * Indica si Cabito puede jugar en este estado.
     *
     * @return {@code false} si está dormido o enfermo
     */
    public boolean permiteJugar() {
        return this != DORMIDO && this != ENFERMO;
    }

    /**
     * Indica si Cabito puede hacer las actividades de los mundos en este
     * estado. Sigue la misma regla que jugar.
     *
     * @return {@code false} si está dormido o enfermo
     */
    public boolean permiteActividades() {
        return permiteJugar();
    }

    /**
     * Indica si Cabito puede entrar a un minijuego en este estado.
     *
     * @return {@code false} si está dormido, enfermo o cansado
     */
    public boolean permiteMinijuegos() {
        return this != DORMIDO && this != ENFERMO && this != CANSADO;
    }
}
