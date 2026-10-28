package modelo.items;

import modelo.Cabito;

/**
 * Accesorio permanente que Cabito se equipa. Puede dar un efecto inmediato
 * al equiparlo y un {@link Bonus} continuo mientras está equipado.
 */
public class Accesorio extends Item {

    private final Bonus bonus;
    private final int felicidadAlEquipar;

    /**
     * Crea un accesorio.
     *
     * @param nombre             nombre único
     * @param descripcion        texto para la tienda
     * @param precio             costo en monedas
     * @param nivelRequerido     nivel mínimo para comprarlo
     * @param bonus              efecto continuo mientras está equipado
     * @param felicidadAlEquipar felicidad que gana Cabito al equiparlo
     */
    public Accesorio(String nombre, String descripcion, int precio, int nivelRequerido,
                     Bonus bonus, int felicidadAlEquipar) {
        super(nombre, descripcion, precio, nivelRequerido);
        this.bonus = bonus;
        this.felicidadAlEquipar = felicidadAlEquipar;
    }

    /**
     * Aplica el efecto inmediato de equiparse el accesorio.
     *
     * @param cabito la mascota que se lo equipa
     */
    @Override
    public void aplicar(Cabito cabito) {
        cabito.setFelicidad(cabito.getFelicidad() + felicidadAlEquipar);
    }

    /**
     * @return siempre {@code false}: los accesorios son permanentes
     */
    @Override
    public boolean esConsumible() {
        return false;
    }

    /**
     * @return {@code "Accesorio"}
     */
    @Override
    public String getTipo() {
        return "Accesorio";
    }

    /**
     * @return efecto continuo que da mientras está equipado
     */
    public Bonus getBonus() {
        return bonus;
    }
}
