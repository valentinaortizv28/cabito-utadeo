package modelo.items;

import java.util.List;

import modelo.Cabito;

/**
 * Tienda Tadeísta: vende los objetos del {@link Catalogo} a cambio de
 * monedas. Lo comprado va al inventario de Cabito.
 */
public class Tienda {

    /**
     * Devuelve todos los objetos que vende la tienda, incluidos los que aún
     * están bloqueados por nivel (la interfaz los muestra como bloqueados).
     *
     * @return lista de objetos
     */
    public List<Item> getItems() {
        return Catalogo.getItems();
    }

    /**
     * Indica si un objeto ya está desbloqueado para el nivel de Cabito.
     *
     * @param item  objeto a revisar
     * @param nivel nivel actual de Cabito
     * @return {@code true} si el nivel alcanza
     */
    public boolean estaDesbloqueado(Item item, int nivel) {
        return nivel >= item.getNivelRequerido();
    }

    /**
     * Intenta comprar un objeto para Cabito. Las validaciones se hacen en
     * este orden: que exista, que el nivel alcance, que no sea un permanente
     * repetido y que alcancen las monedas. Si algo falla, no se cobra nada.
     *
     * @param nombre nombre del objeto
     * @param cabito la mascota que compra (se usan su nivel y su inventario)
     * @return el resultado de la compra
     */
    public ResultadoCompra comprar(String nombre, Cabito cabito) {
        Item item = Catalogo.buscar(nombre);
        if (item == null) {
            return ResultadoCompra.NO_EXISTE;
        }
        if (!estaDesbloqueado(item, cabito.getNivel())) {
            return ResultadoCompra.NIVEL_INSUFICIENTE;
        }
        Inventario inventario = cabito.getInventario();
        if (!item.esConsumible() && inventario.tiene(nombre)) {
            return ResultadoCompra.YA_LO_TIENE;
        }
        if (!inventario.gastarMonedas(item.getPrecio())) {
            return ResultadoCompra.MONEDAS_INSUFICIENTES;
        }
        inventario.agregar(item);
        return ResultadoCompra.EXITO;
    }
}
