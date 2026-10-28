package modelo.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.Cabito;

/**
 * Pruebas de {@link Tienda} y del {@link Catalogo}.
 */
class TiendaTest {

    private Tienda tienda;
    private Cabito cabito;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        tienda = new Tienda();
        cabito = new Cabito("Cabito");
        inventario = cabito.getInventario();
    }

    // ---------------- Catálogo ----------------

    @Test
    @DisplayName("El catálogo tiene los 12 objetos de la tabla con su precio, tipo y nivel")
    void catalogoCompleto() {
        assertEquals(12, Catalogo.getItems().size());
        revisar(Catalogo.TINTO, "Comida", 10, 1);
        revisar(Catalogo.EMPANADA, "Comida", 15, 1);
        revisar(Catalogo.ALMUERZO, "Comida", 35, 1);
        revisar(Catalogo.AGUA, "Comida", 8, 1);
        revisar(Catalogo.BALON, "Juguete", 40, 1);
        revisar(Catalogo.CUADERNO, "Juguete", 30, 1);
        revisar(Catalogo.KIT_ASEO, "Cuidado", 20, 1);
        revisar(Catalogo.BOTIQUIN, "Cuidado", 45, 1);
        revisar(Catalogo.GORRA, "Accesorio", 60, 1);
        revisar(Catalogo.SACO, "Accesorio", 120, 3);
        revisar(Catalogo.TERMO, "Accesorio", 80, 1);
        revisar(Catalogo.MOCHILA, "Accesorio", 100, 5);
    }

    private void revisar(String nombre, String tipo, int precio, int nivel) {
        Item item = Catalogo.buscar(nombre);
        assertNotNull(item, nombre);
        assertEquals(tipo, item.getTipo(), nombre);
        assertEquals(precio, item.getPrecio(), nombre);
        assertEquals(nivel, item.getNivelRequerido(), nombre);
    }

    @Test
    @DisplayName("Buscar un nombre que no existe devuelve null")
    void buscarInexistente() {
        assertNull(Catalogo.buscar("Pizza"));
    }

    // ---------------- Compras ----------------

    @Test
    @DisplayName("Comprar descuenta las monedas y agrega el objeto al inventario")
    void compraExitosa() {
        assertEquals(ResultadoCompra.EXITO, tienda.comprar(Catalogo.EMPANADA, cabito));
        assertEquals(Inventario.MONEDAS_INICIALES - 15, inventario.getMonedas());
        assertEquals(1, inventario.getCantidad(Catalogo.EMPANADA));
    }

    @Test
    @DisplayName("Se pueden comprar varias unidades de un consumible")
    void variosConsumibles() {
        tienda.comprar(Catalogo.TINTO, cabito);
        tienda.comprar(Catalogo.TINTO, cabito);
        assertEquals(2, inventario.getCantidad(Catalogo.TINTO));
        assertEquals(Inventario.MONEDAS_INICIALES - 20, inventario.getMonedas());
    }

    @Test
    @DisplayName("Sin monedas suficientes no se compra ni se cobra nada")
    void sinMonedasSuficientes() {
        inventario.setMonedas(30);
        assertEquals(ResultadoCompra.MONEDAS_INSUFICIENTES, tienda.comprar(Catalogo.ALMUERZO, cabito));
        assertEquals(30, inventario.getMonedas());
        assertFalse(inventario.tiene(Catalogo.ALMUERZO));
    }

    @Test
    @DisplayName("Con las monedas exactas sí se puede comprar y queda en 0")
    void monedasExactas() {
        inventario.setMonedas(35);
        assertEquals(ResultadoCompra.EXITO, tienda.comprar(Catalogo.ALMUERZO, cabito));
        assertEquals(0, inventario.getMonedas());
    }

    @Test
    @DisplayName("Con 0 monedas no se puede comprar ni lo más barato")
    void ceroMonedas() {
        inventario.setMonedas(0);
        assertEquals(ResultadoCompra.MONEDAS_INSUFICIENTES, tienda.comprar(Catalogo.AGUA, cabito));
        assertTrue(inventario.getItems().isEmpty());
    }

    @Test
    @DisplayName("El Saco requiere nivel 3 aunque haya monedas")
    void sacoBloqueadoPorNivel() {
        inventario.setMonedas(500);
        assertEquals(ResultadoCompra.NIVEL_INSUFICIENTE, tienda.comprar(Catalogo.SACO, cabito));
        assertEquals(500, inventario.getMonedas());

        cabito.setNivel(3);
        assertEquals(ResultadoCompra.EXITO, tienda.comprar(Catalogo.SACO, cabito));
    }

    @Test
    @DisplayName("La Mochila requiere nivel 5")
    void mochilaBloqueadaPorNivel() {
        inventario.setMonedas(500);
        cabito.setNivel(4);
        assertEquals(ResultadoCompra.NIVEL_INSUFICIENTE, tienda.comprar(Catalogo.MOCHILA, cabito));
        cabito.setNivel(5);
        assertEquals(ResultadoCompra.EXITO, tienda.comprar(Catalogo.MOCHILA, cabito));
    }

    @Test
    @DisplayName("estaDesbloqueado compara el nivel con el requerido")
    void desbloqueo() {
        Item saco = Catalogo.buscar(Catalogo.SACO);
        assertFalse(tienda.estaDesbloqueado(saco, 2));
        assertTrue(tienda.estaDesbloqueado(saco, 3));
    }

    @Test
    @DisplayName("Un permanente no se puede comprar dos veces")
    void permanenteRepetido() {
        inventario.setMonedas(200);
        assertEquals(ResultadoCompra.EXITO, tienda.comprar(Catalogo.BALON, cabito));
        assertEquals(ResultadoCompra.YA_LO_TIENE, tienda.comprar(Catalogo.BALON, cabito));
        assertEquals(160, inventario.getMonedas());
        assertEquals(1, inventario.getCantidad(Catalogo.BALON));
    }

    @Test
    @DisplayName("Comprar un objeto que no existe no cobra nada")
    void compraInexistente() {
        assertEquals(ResultadoCompra.NO_EXISTE, tienda.comprar("Pizza", cabito));
        assertEquals(Inventario.MONEDAS_INICIALES, inventario.getMonedas());
    }
}
