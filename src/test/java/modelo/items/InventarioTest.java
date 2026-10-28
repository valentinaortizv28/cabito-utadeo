package modelo.items;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.Cabito;

/**
 * Pruebas de {@link Inventario}: monedas, consumibles, accesorios y juguetes.
 */
class InventarioTest {

    private Cabito cabito;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        cabito = new Cabito("Cabito");
        inventario = cabito.getInventario();
        cabito.setSaciedad(50);
        cabito.setEnergia(50);
        cabito.setFelicidad(50);
        cabito.setHigiene(50);
        cabito.setInteligencia(50);
    }

    private void agregar(String nombre) {
        inventario.agregar(Catalogo.buscar(nombre));
    }

    // ---------------- Monedas ----------------

    @Test
    @DisplayName("Empieza con 50 monedas y sin objetos")
    void inicial() {
        assertEquals(50, inventario.getMonedas());
        assertTrue(inventario.getItems().isEmpty());
    }

    @Test
    @DisplayName("No se pueden gastar más monedas de las que hay")
    void gastarDeMas() {
        assertFalse(inventario.gastarMonedas(51));
        assertEquals(50, inventario.getMonedas());
        assertTrue(inventario.gastarMonedas(50));
        assertEquals(0, inventario.getMonedas());
    }

    @Test
    @DisplayName("Sin Mochila una actividad da las monedas base")
    void monedasSinMochila() {
        assertEquals(25, inventario.ganarMonedasPorActividad(25));
        assertEquals(75, inventario.getMonedas());
    }

    @Test
    @DisplayName("Con la Mochila equipada una actividad da +20%")
    void monedasConMochila() {
        agregar(Catalogo.MOCHILA);
        inventario.equipar(Catalogo.MOCHILA, cabito);
        assertEquals(30, inventario.ganarMonedasPorActividad(25));
        assertEquals(80, inventario.getMonedas());
    }

    @Test
    @DisplayName("La Mochila sin equipar no da bonus")
    void mochilaSinEquipar() {
        agregar(Catalogo.MOCHILA);
        assertEquals(25, inventario.ganarMonedasPorActividad(25));
    }

    // ---------------- Consumibles ----------------

    @Test
    @DisplayName("Usar un consumible aplica su efecto y gasta una unidad")
    void usarConsumible() {
        agregar(Catalogo.EMPANADA);
        agregar(Catalogo.EMPANADA);
        assertTrue(inventario.usar(Catalogo.EMPANADA, cabito));
        assertEquals(75, cabito.getSaciedad());
        assertEquals(1, inventario.getCantidad(Catalogo.EMPANADA));
    }

    @Test
    @DisplayName("Al usar la última unidad el objeto desaparece del inventario")
    void ultimaUnidad() {
        agregar(Catalogo.KIT_ASEO);
        assertTrue(inventario.usar(Catalogo.KIT_ASEO, cabito));
        assertEquals(90, cabito.getHigiene());
        assertFalse(inventario.tiene(Catalogo.KIT_ASEO));
        assertTrue(inventario.getItems().isEmpty());
    }

    @Test
    @DisplayName("No se puede usar un objeto que no se tiene")
    void usarSinTener() {
        assertFalse(inventario.usar(Catalogo.EMPANADA, cabito));
        assertEquals(50, cabito.getSaciedad());
    }

    @Test
    @DisplayName("Efecto de cada comida y artículo de cuidado")
    void efectosConsumibles() {
        revisarEfecto(Catalogo.TINTO, 55, 70, 50, 50);
        revisarEfecto(Catalogo.EMPANADA, 75, 50, 50, 50);
        revisarEfecto(Catalogo.ALMUERZO, 100, 50, 55, 50);
        revisarEfecto(Catalogo.AGUA, 50, 60, 50, 50);
        revisarEfecto(Catalogo.KIT_ASEO, 50, 50, 50, 90);
        revisarEfecto(Catalogo.BOTIQUIN, 70, 70, 50, 70);
    }

    /** Aplica un objeto partiendo de todo en 50 y revisa los cuatro atributos. */
    private void revisarEfecto(String nombre, int saciedad, int energia, int felicidad, int higiene) {
        Cabito c = new Cabito("Prueba");
        c.setSaciedad(50);
        c.setEnergia(50);
        c.setFelicidad(50);
        c.setHigiene(50);
        Catalogo.buscar(nombre).aplicar(c);
        assertEquals(saciedad, c.getSaciedad(), nombre + " saciedad");
        assertEquals(energia, c.getEnergia(), nombre + " energía");
        assertEquals(felicidad, c.getFelicidad(), nombre + " felicidad");
        assertEquals(higiene, c.getHigiene(), nombre + " higiene");
    }

    @Test
    @DisplayName("Los efectos no pasan de 100")
    void efectoLimitado() {
        cabito.setSaciedad(90);
        agregar(Catalogo.ALMUERZO);
        inventario.usar(Catalogo.ALMUERZO, cabito);
        assertEquals(100, cabito.getSaciedad());
    }

    @Test
    @DisplayName("getComidas solo devuelve comidas")
    void soloComidas() {
        agregar(Catalogo.EMPANADA);
        agregar(Catalogo.KIT_ASEO);
        agregar(Catalogo.GORRA);
        agregar(Catalogo.TINTO);
        assertEquals(2, inventario.getComidas().size());
        assertEquals(4, inventario.getItems().size());
    }

    // ---------------- Accesorios ----------------

    @Test
    @DisplayName("Equipar un accesorio lo marca como equipado y no lo gasta")
    void equiparAccesorio() {
        agregar(Catalogo.GORRA);
        assertTrue(inventario.equipar(Catalogo.GORRA, cabito));
        assertTrue(inventario.estaEquipado(Catalogo.GORRA));
        assertTrue(inventario.tiene(Catalogo.GORRA));
        assertTrue(inventario.tieneBonus(Bonus.FELICIDAD_LENTA));
        assertFalse(inventario.tieneBonus(Bonus.ENERGIA_LENTA));
    }

    @Test
    @DisplayName("Usar un accesorio es lo mismo que equiparlo")
    void usarAccesorioLoEquipa() {
        agregar(Catalogo.TERMO);
        assertTrue(inventario.usar(Catalogo.TERMO, cabito));
        assertTrue(inventario.estaEquipado(Catalogo.TERMO));
        assertTrue(inventario.tiene(Catalogo.TERMO));
    }

    @Test
    @DisplayName("Equipar el Saco da +10 de felicidad, solo la primera vez")
    void equiparSaco() {
        agregar(Catalogo.SACO);
        assertTrue(inventario.equipar(Catalogo.SACO, cabito));
        assertEquals(60, cabito.getFelicidad());
        assertFalse(inventario.equipar(Catalogo.SACO, cabito));
        assertEquals(60, cabito.getFelicidad());
    }

    @Test
    @DisplayName("No se puede equipar un accesorio que no se tiene")
    void equiparSinTener() {
        assertFalse(inventario.equipar(Catalogo.GORRA, cabito));
        assertFalse(inventario.tieneBonus(Bonus.FELICIDAD_LENTA));
    }

    @Test
    @DisplayName("Solo los accesorios se pueden equipar")
    void equiparNoAccesorio() {
        agregar(Catalogo.EMPANADA);
        assertFalse(inventario.equipar(Catalogo.EMPANADA, cabito));
    }

    @Test
    @DisplayName("Se pueden equipar varios accesorios distintos a la vez")
    void variosAccesorios() {
        agregar(Catalogo.GORRA);
        agregar(Catalogo.TERMO);
        inventario.equipar(Catalogo.GORRA, cabito);
        inventario.equipar(Catalogo.TERMO, cabito);
        assertTrue(inventario.tieneBonus(Bonus.FELICIDAD_LENTA));
        assertTrue(inventario.tieneBonus(Bonus.ENERGIA_LENTA));
    }

    // ---------------- Juguetes ----------------

    @Test
    @DisplayName("Un juguete no se puede usar directamente ni se gasta")
    void usarJuguete() {
        agregar(Catalogo.BALON);
        assertFalse(inventario.usar(Catalogo.BALON, cabito));
        assertTrue(inventario.tiene(Catalogo.BALON));
    }

    @Test
    @DisplayName("aplicarJuguetes solo aplica los de la actividad indicada")
    void aplicarJuguetesPorActividad() {
        agregar(Catalogo.BALON);
        agregar(Catalogo.CUADERNO);
        inventario.aplicarJuguetes(Actividad.ESTUDIAR, cabito);
        assertEquals(55, cabito.getInteligencia());
        assertEquals(50, cabito.getFelicidad());
        inventario.aplicarJuguetes(Actividad.JUGAR, cabito);
        assertEquals(60, cabito.getFelicidad());
    }
}
