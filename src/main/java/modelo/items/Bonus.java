package modelo.items;

/**
 * Efectos continuos que dan los accesorios mientras están equipados.
 */
public enum Bonus {

    /** No da ningún efecto continuo. */
    NINGUNO,
    /** La felicidad baja la mitad de rápido con el tiempo (Gorra UTadeo). */
    FELICIDAD_LENTA,
    /** La energía baja la mitad de rápido con el tiempo (Termo Tadeísta). */
    ENERGIA_LENTA,
    /** +20% de monedas en las actividades de los mundos (Mochila UTadeo). */
    MONEDAS_EXTRA
}
