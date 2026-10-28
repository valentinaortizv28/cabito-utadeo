package modelo.items;

import modelo.Cabito;

/**
 * Objeto que se puede comprar en la Tienda Tadeísta.
 * <p>
 * Cada tipo de objeto decide qué le hace a Cabito sobrescribiendo
 * {@link #aplicar(Cabito)} (polimorfismo). Los objetos son solo
 * definiciones: el inventario guarda cuántos tiene el jugador de cada uno
 * usando su nombre.
 */
public abstract class Item {

    private final String nombre;
    private final String descripcion;
    private final int precio;
    private final int nivelRequerido;

    /**
     * Crea un objeto.
     *
     * @param nombre         nombre único del objeto
     * @param descripcion    texto que se muestra en la tienda
     * @param precio         costo en monedas
     * @param nivelRequerido nivel mínimo de Cabito para poder comprarlo
     */
    protected Item(String nombre, String descripcion, int precio, int nivelRequerido) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.nivelRequerido = nivelRequerido;
    }

    /**
     * Aplica el efecto del objeto sobre Cabito. No recalcula el estado:
     * eso lo hace quien usa el objeto.
     *
     * @param cabito la mascota que recibe el efecto
     */
    public abstract void aplicar(Cabito cabito);

    /**
     * Indica si el objeto se gasta al usarlo.
     *
     * @return {@code true} para comida y artículos de cuidado;
     *         {@code false} para juguetes y accesorios, que son permanentes
     */
    public abstract boolean esConsumible();

    /**
     * Devuelve el tipo de objeto para mostrarlo en la interfaz.
     *
     * @return por ejemplo {@code "Comida"} o {@code "Accesorio"}
     */
    public abstract String getTipo();

    /**
     * @return nombre único del objeto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return descripción del efecto para la tienda
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @return precio en monedas
     */
    public int getPrecio() {
        return precio;
    }

    /**
     * @return nivel mínimo para comprarlo (1 si está disponible desde el inicio)
     */
    public int getNivelRequerido() {
        return nivelRequerido;
    }

    /**
     * Devuelve el nombre, para que el objeto se vea bien en listas de Swing.
     *
     * @return nombre del objeto
     */
    @Override
    public String toString() {
        return nombre;
    }
}
