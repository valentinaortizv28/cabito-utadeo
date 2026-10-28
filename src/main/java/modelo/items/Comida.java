package modelo.items;

import modelo.Cabito;

/**
 * Comida de la cafetería. Se consume al usarla y sube saciedad, energía
 * y/o felicidad.
 */
public class Comida extends Item {

    private final int saciedad;
    private final int energia;
    private final int felicidad;

    /**
     * Crea una comida.
     *
     * @param nombre      nombre único
     * @param descripcion texto para la tienda
     * @param precio      costo en monedas
     * @param saciedad    cuánto sube la saciedad
     * @param energia     cuánto sube la energía
     * @param felicidad   cuánto sube la felicidad
     */
    public Comida(String nombre, String descripcion, int precio,
                  int saciedad, int energia, int felicidad) {
        super(nombre, descripcion, precio, 1);
        this.saciedad = saciedad;
        this.energia = energia;
        this.felicidad = felicidad;
    }

    /**
     * Suma a Cabito la saciedad, energía y felicidad de esta comida.
     *
     * @param cabito la mascota que come
     */
    @Override
    public void aplicar(Cabito cabito) {
        cabito.setSaciedad(cabito.getSaciedad() + saciedad);
        cabito.setEnergia(cabito.getEnergia() + energia);
        cabito.setFelicidad(cabito.getFelicidad() + felicidad);
    }

    /**
     * @return siempre {@code true}: la comida se gasta
     */
    @Override
    public boolean esConsumible() {
        return true;
    }

    /**
     * @return {@code "Comida"}
     */
    @Override
    public String getTipo() {
        return "Comida";
    }
}
