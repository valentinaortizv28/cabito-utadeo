package modelo;

/**
 * Estados automáticos en los que puede estar Cabito.
 * <p>
 * El estado no lo elige el usuario: lo calcula {@link Cabito#actualizarEstado()}
 * a partir de los atributos. Cada estado define qué imagen se muestra y qué
 * acciones están permitidas, de modo que la vista y el controlador solo tienen
 * que preguntarle al estado.
 */
public enum Estado {

    /** Cabito está durmiendo; solo se le puede despertar. */
    DORMIDO("Zzz... déjame dormir un rato más.", "cabito_dormido.png"),
    /** Saciedad o higiene muy bajas; necesita cuidados urgentes. */
    ENFERMO("No me siento bien... necesito que me cuides.", "cabito_enfermo.png"),
    /** Saciedad baja. */
    HAMBRIENTO("¡Tengo hambre! ¿Vamos a la cafetería?", "cabito_hambriento.png"),
    /** Energía baja. */
    CANSADO("Estoy muy cansado...", "cabito_cansado.png"),
    /** Higiene baja. */
    SUCIO("Creo que necesito un baño.", "cabito_sucio.png"),
    /** Felicidad baja. */
    TRISTE("Me siento un poco triste.", "cabito_triste.png"),
    /** Felicidad alta y todo lo demás en buen nivel. */
    FELIZ("¡Qué buen día en la Tadeo!", "cabito_feliz.png"),
    /** Ningún otro estado aplica. */
    NORMAL("¡Hola! ¿Qué hacemos hoy?", "cabito_normal.png");

    private final String descripcion;
    private final String nombreImagen;

    /**
     * Crea un estado con su mensaje y su imagen.
     *
     * @param descripcion  frase que dice Cabito en este estado
     * @param nombreImagen nombre del archivo en {@code src/main/resources/img/}
     */
    Estado(String descripcion, String nombreImagen) {
        this.descripcion = descripcion;
        this.nombreImagen = nombreImagen;
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
     * Devuelve el nombre del archivo de imagen asociado a este estado.
     *
     * @return nombre del archivo PNG, por ejemplo {@code "cabito_feliz.png"}
     */
    public String getNombreImagen() {
        return nombreImagen;
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
     * Indica si Cabito puede entrar a un minijuego en este estado.
     *
     * @return {@code false} si está dormido, enfermo o cansado
     */
    public boolean permiteMinijuegos() {
        return this != DORMIDO && this != ENFERMO && this != CANSADO;
    }
}
