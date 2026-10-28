package modelo.items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import modelo.Cabito;

/**
 * Monedas y objetos de Cabito.
 * <p>
 * Los objetos se guardan por nombre en un mapa {@code nombre → cantidad};
 * los accesorios equipados se guardan en un conjunto de nombres. Como solo
 * hay textos y números, la partida se puede guardar en JSON fácilmente.
 */
public class Inventario {

    /** Monedas con las que empieza una partida nueva. */
    public static final int MONEDAS_INICIALES = 50;

    /** Porcentaje extra de monedas que da la Mochila UTadeo. */
    private static final int PORCENTAJE_MOCHILA = 20;

    private int monedas;
    private final Map<String, Integer> cantidades;
    private final Set<String> equipados;

    /**
     * Crea un inventario vacío con las monedas iniciales.
     */
    public Inventario() {
        monedas = MONEDAS_INICIALES;
        cantidades = new LinkedHashMap<>();
        equipados = new LinkedHashSet<>();
    }

    // ------------------------------------------------------------------
    // Monedas
    // ------------------------------------------------------------------

    /**
     * @return monedas disponibles
     */
    public int getMonedas() {
        return monedas;
    }

    /**
     * Cambia las monedas directamente (por ejemplo, al cargar una partida).
     *
     * @param monedas nuevo valor; si es negativo queda en 0
     */
    public void setMonedas(int monedas) {
        this.monedas = Math.max(0, monedas);
    }

    /**
     * Suma monedas sin ningún bonus (premios de minijuegos, eventos, etc.).
     *
     * @param cantidad monedas a sumar; si no es positiva no hace nada
     */
    public void agregarMonedas(int cantidad) {
        if (cantidad > 0) {
            monedas += cantidad;
        }
    }

    /**
     * Suma las monedas ganadas en una actividad de un mundo. Si la Mochila
     * UTadeo está equipada, se gana un 20% más (redondeando hacia abajo).
     *
     * @param base monedas que da la actividad
     * @return monedas realmente ganadas
     */
    public int ganarMonedasPorActividad(int base) {
        int ganadas = base;
        if (tieneBonus(Bonus.MONEDAS_EXTRA)) {
            ganadas += base * PORCENTAJE_MOCHILA / 100;
        }
        agregarMonedas(ganadas);
        return ganadas;
    }

    /**
     * Resta monedas solo si alcanzan.
     *
     * @param cantidad monedas a gastar
     * @return {@code true} si se pudieron gastar; {@code false} si no alcanzan
     */
    public boolean gastarMonedas(int cantidad) {
        if (cantidad < 0 || cantidad > monedas) {
            return false;
        }
        monedas -= cantidad;
        return true;
    }

    // ------------------------------------------------------------------
    // Objetos
    // ------------------------------------------------------------------

    /**
     * Agrega una unidad de un objeto.
     *
     * @param item objeto a agregar
     */
    public void agregar(Item item) {
        cantidades.merge(item.getNombre(), 1, Integer::sum);
    }

    /**
     * Quita una unidad de un objeto; si era la última, el objeto desaparece
     * del inventario.
     *
     * @param nombre nombre del objeto
     * @return {@code true} si había al menos una unidad
     */
    public boolean quitar(String nombre) {
        int cantidad = getCantidad(nombre);
        if (cantidad == 0) {
            return false;
        }
        if (cantidad == 1) {
            cantidades.remove(nombre);
            equipados.remove(nombre);
        } else {
            cantidades.put(nombre, cantidad - 1);
        }
        return true;
    }

    /**
     * @param nombre nombre del objeto
     * @return cuántas unidades hay (0 si no hay ninguna)
     */
    public int getCantidad(String nombre) {
        return cantidades.getOrDefault(nombre, 0);
    }

    /**
     * @param nombre nombre del objeto
     * @return {@code true} si hay al menos una unidad
     */
    public boolean tiene(String nombre) {
        return getCantidad(nombre) > 0;
    }

    /**
     * Devuelve los objetos que hay en el inventario (una entrada por tipo,
     * sin repetir), en el orden en que se obtuvieron.
     *
     * @return lista nueva de objetos
     */
    public List<Item> getItems() {
        List<Item> lista = new ArrayList<>();
        for (String nombre : cantidades.keySet()) {
            Item item = Catalogo.buscar(nombre);
            if (item != null) {
                lista.add(item);
            }
        }
        return lista;
    }

    /**
     * Devuelve solo las comidas del inventario (para la acción Alimentar).
     *
     * @return lista nueva de comidas
     */
    public List<Comida> getComidas() {
        List<Comida> comidas = new ArrayList<>();
        for (Item item : getItems()) {
            if (item instanceof Comida) {
                comidas.add((Comida) item);
            }
        }
        return comidas;
    }

    /**
     * Usa un objeto del inventario sobre Cabito:
     * <ul>
     *   <li>Consumible (comida o cuidado): aplica el efecto y se gasta una unidad.</li>
     *   <li>Accesorio: se equipa y aplica su efecto inmediato.</li>
     *   <li>Juguete: no se usa directamente; su efecto es automático.</li>
     * </ul>
     * No revisa el estado de Cabito: eso lo hace {@link Cabito#usarItem(String)}.
     *
     * @param nombre nombre del objeto
     * @param cabito la mascota que lo recibe
     * @return {@code true} si el objeto se usó
     */
    public boolean usar(String nombre, Cabito cabito) {
        Item item = Catalogo.buscar(nombre);
        if (item == null || !tiene(nombre)) {
            return false;
        }
        if (item.esConsumible()) {
            item.aplicar(cabito);
            quitar(nombre);
            return true;
        }
        if (item instanceof Accesorio) {
            return equipar(nombre, cabito);
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Accesorios y juguetes
    // ------------------------------------------------------------------

    /**
     * Equipa un accesorio que esté en el inventario y aplica su efecto
     * inmediato. Se pueden tener varios accesorios distintos equipados a la vez.
     *
     * @param nombre nombre del accesorio
     * @param cabito la mascota que se lo pone
     * @return {@code false} si no lo tiene, no es accesorio o ya estaba equipado
     */
    public boolean equipar(String nombre, Cabito cabito) {
        Item item = Catalogo.buscar(nombre);
        if (!(item instanceof Accesorio) || !tiene(nombre) || estaEquipado(nombre)) {
            return false;
        }
        equipados.add(nombre);
        item.aplicar(cabito);
        return true;
    }

    /**
     * @param nombre nombre del accesorio
     * @return {@code true} si está equipado
     */
    public boolean estaEquipado(String nombre) {
        return equipados.contains(nombre);
    }

    /**
     * Indica si algún accesorio equipado da el bonus indicado.
     *
     * @param bonus bonus a buscar
     * @return {@code true} si está activo
     */
    public boolean tieneBonus(Bonus bonus) {
        for (String nombre : equipados) {
            Item item = Catalogo.buscar(nombre);
            if (item instanceof Accesorio && ((Accesorio) item).getBonus() == bonus) {
                return true;
            }
        }
        return false;
    }

    /**
     * Aplica el efecto extra de todos los juguetes que mejoran una actividad.
     * Por ejemplo, al jugar se aplica el Balón Tadeísta si Cabito lo tiene.
     *
     * @param actividad actividad que se está realizando
     * @param cabito    la mascota que la realiza
     */
    public void aplicarJuguetes(Actividad actividad, Cabito cabito) {
        for (Item item : getItems()) {
            if (item instanceof Juguete && ((Juguete) item).getActividad() == actividad) {
                item.aplicar(cabito);
            }
        }
    }
}
