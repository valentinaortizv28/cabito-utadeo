package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.items.Catalogo;
import modelo.items.Inventario;
import modelo.mundos.Biblioteca;
import modelo.mundos.Brujula;

/**
 * Pruebas de la Tienda Tadeísta como lugar al que Cabito puede ir.
 */
class PartidaTiendaTest {

    private Partida partida;

    @BeforeEach
    void setUp() {
        partida = new Partida("Cabito", new RandomFijo());
    }

    @Test
    @DisplayName("Se puede ir a la tienda desde el nivel 1 y se recuerda el mundo de origen")
    void irATienda() {
        assertTrue(partida.irATienda());
        assertTrue(partida.isEnTienda());
        assertEquals(Brujula.NOMBRE, partida.getMundoActual().getNombre());
        assertEquals("Entraste a la Tienda Tadeísta.", partida.getHistorial().getUltimasAcciones(1).get(0));
    }

    @Test
    @DisplayName("No se puede entrar dos veces ni salir sin haber entrado")
    void entrarYSalirSoloUnaVez() {
        assertFalse(partida.salirDeTienda());
        partida.irATienda();
        assertFalse(partida.irATienda());
    }

    @Test
    @DisplayName("Al salir, Cabito vuelve al mundo de donde vino")
    void volverAlMundoAnterior() {
        partida.getCabito().setNivel(2);
        partida.viajarA(Biblioteca.NOMBRE);
        partida.irATienda();
        assertTrue(partida.salirDeTienda());
        assertFalse(partida.isEnTienda());
        assertEquals(Biblioteca.NOMBRE, partida.getMundoActual().getNombre());
        assertEquals("Volviste a Biblioteca.", partida.getHistorial().getUltimasAcciones(1).get(0));
    }

    @Test
    @DisplayName("Viajar a un mundo desde la tienda también sale de la tienda")
    void viajarDesdeLaTienda() {
        partida.getCabito().setNivel(2);
        partida.irATienda();
        assertTrue(partida.viajarA(Biblioteca.NOMBRE));
        assertFalse(partida.isEnTienda());
    }

    @Test
    @DisplayName("Dentro de la tienda no se hacen actividades, pero sí se compra")
    void actividadesEnTienda() {
        partida.irATienda();
        assertFalse(partida.realizarActividad().isRealizada());
        assertEquals(0, partida.getActividadesRealizadas(Brujula.NOMBRE));
        partida.comprar(Catalogo.EMPANADA);
        assertEquals(Inventario.MONEDAS_INICIALES - 15, partida.getCabito().getInventario().getMonedas());
    }
}
