package modelo.items;

import modelo.Cabito;

/**
 * Artículo de cuidado (aseo o bienestar). Se consume al usarlo y sube
 * saciedad, energía y/o higiene. Se puede usar aunque Cabito esté enfermo.
 */
public class ItemCuidado extends Item {

    private final int saciedad;
    private final int energia;
    private final int higiene;

    /**
     * Crea un artículo de cuidado.
     *
     * @param nombre      nombre único
     * @param descripcion texto para la tienda
     * @param precio      costo en monedas
     * @param saciedad    cuánto sube la saciedad
     * @param energia     cuánto sube la energía
     * @param higiene     cuánto sube la higiene
     */
    public ItemCuidado(String nombre, String descripcion, int precio,
                       int saciedad, int energia, int higiene) {
        super(nombre, descripcion, precio, 1);
        this.saciedad = saciedad;
        this.energia = energia;
        this.higiene = higiene;
    }

    /**
     * Suma a Cabito la saciedad, energía e higiene de este artículo.
     *
     * @param cabito la mascota que recibe el cuidado
     */
    @Override
    public void aplicar(Cabito cabito) {
        cabito.setSaciedad(cabito.getSaciedad() + saciedad);
        cabito.setEnergia(cabito.getEnergia() + energia);
        cabito.setHigiene(cabito.getHigiene() + higiene);
    }

    /**
     * @return siempre {@code true}: el artículo se gasta
     */
    @Override
    public boolean esConsumible() {
        return true;
    }

    /**
     * @return {@code "Cuidado"}
     */
    @Override
    public String getTipo() {
        return "Cuidado";
    }
}
