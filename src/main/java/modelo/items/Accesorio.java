package modelo.items;

import modelo.Cabito;

/**
 * Accesorio permanente que Cabito se pone y se quita. Puede dar un efecto
 * inmediato al ponérselo (que se deshace al quitárselo) y un {@link Bonus}
 * continuo mientras lo tiene puesto. Así el bonus solo cuenta mientras el
 * accesorio está puesto.
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
     * Deshace el efecto inmediato al quitarse el accesorio. Por ejemplo, el
     * Saco da +10 de felicidad al ponerlo y la resta al quitarlo, así no se
     * puede ganar felicidad poniéndolo y quitándolo muchas veces.
     *
     * @param cabito la mascota que se lo quita
     */
    public void quitar(Cabito cabito) {
        cabito.setFelicidad(cabito.getFelicidad() - felicidadAlEquipar);
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
