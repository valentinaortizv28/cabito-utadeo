package modelo.items;

import modelo.Cabito;

/**
 * Juguete permanente. No se usa directamente: mientras Cabito lo tenga,
 * da un efecto extra cada vez que realiza cierta actividad (por ejemplo,
 * el Balón da más felicidad al jugar).
 */
public class Juguete extends Item {

    private final Actividad actividad;
    private final int felicidadExtra;
    private final int inteligenciaExtra;

    /**
     * Crea un juguete.
     *
     * @param nombre            nombre único
     * @param descripcion       texto para la tienda
     * @param precio            costo en monedas
     * @param actividad         actividad que mejora este juguete
     * @param felicidadExtra    felicidad adicional en esa actividad
     * @param inteligenciaExtra inteligencia adicional en esa actividad
     */
    public Juguete(String nombre, String descripcion, int precio,
                   Actividad actividad, int felicidadExtra, int inteligenciaExtra) {
        super(nombre, descripcion, precio, 1);
        this.actividad = actividad;
        this.felicidadExtra = felicidadExtra;
        this.inteligenciaExtra = inteligenciaExtra;
    }

    /**
     * Aplica el efecto extra del juguete. Lo llama la actividad que el
     * juguete mejora (ver {@link Inventario#aplicarJuguetes}).
     *
     * @param cabito la mascota que usa el juguete
     */
    @Override
    public void aplicar(Cabito cabito) {
        cabito.setFelicidad(cabito.getFelicidad() + felicidadExtra);
        cabito.setInteligencia(cabito.getInteligencia() + inteligenciaExtra);
    }

    /**
     * @return siempre {@code false}: los juguetes son permanentes
     */
    @Override
    public boolean esConsumible() {
        return false;
    }

    /**
     * @return {@code "Juguete"}
     */
    @Override
    public String getTipo() {
        return "Juguete";
    }

    /**
     * @return actividad que mejora este juguete
     */
    public Actividad getActividad() {
        return actividad;
    }
}
