package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.items.Catalogo;
import modelo.items.Inventario;

/**
 * Pruebas de cómo los objetos afectan a {@link Cabito}: alimentar, usar
 * objetos según el estado y los bonus de juguetes y accesorios.
 */
class CabitoItemsTest {

    private Cabito cabito;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        cabito = new Cabito("Cabito");
        inventario = cabito.getInventario();
        cabito.setSaciedad(80);
        cabito.setEnergia(80);
        cabito.setFelicidad(70);
        cabito.setHigiene(80);
        cabito.setInteligencia(60);
        cabito.actualizarEstado();
    }

    private void agregar(String nombre) {
        inventario.agregar(Catalogo.buscar(nombre));
    }

    // ---------------- Alimentar y usar ----------------

    @Test
    @DisplayName("Alimentar sin comida en el inventario no hace nada")
    void alimentarSinComida() {
        cabito.setSaciedad(40);
        assertFalse(cabito.alimentar(Catalogo.EMPANADA));
        assertEquals(40, cabito.getSaciedad());
    }

    @Test
    @DisplayName("Alimentar con una empanada sube la saciedad y la gasta")
    void alimentarConEmpanada() {
        cabito.setSaciedad(25);
        cabito.actualizarEstado();
        assertEquals(Estado.HAMBRIENTO, cabito.getEstado());
        agregar(Catalogo.EMPANADA);
        assertTrue(cabito.alimentar(Catalogo.EMPANADA));
        assertEquals(50, cabito.getSaciedad());
        assertFalse(inventario.tiene(Catalogo.EMPANADA));
        assertEquals(Estado.NORMAL, cabito.getEstado());
    }

    @Test
    @DisplayName("Alimentar no acepta objetos que no son comida")
    void alimentarConNoComida() {
        agregar(Catalogo.KIT_ASEO);
        assertFalse(cabito.alimentar(Catalogo.KIT_ASEO));
        assertTrue(inventario.tiene(Catalogo.KIT_ASEO));
    }

    @Test
    @DisplayName("Dormido no se puede usar ningún objeto")
    void dormidoNoUsaItems() {
        cabito.setEnergia(40);
        agregar(Catalogo.EMPANADA);
        cabito.dormir();
        assertFalse(cabito.alimentar(Catalogo.EMPANADA));
        assertTrue(inventario.tiene(Catalogo.EMPANADA));
    }

    @Test
    @DisplayName("El botiquín saca a Cabito del estado ENFERMO")
    void botiquinCura() {
        cabito.setSaciedad(10);
        cabito.setHigiene(10);
        cabito.actualizarEstado();
        assertEquals(Estado.ENFERMO, cabito.getEstado());
        agregar(Catalogo.BOTIQUIN);
        assertTrue(cabito.usarItem(Catalogo.BOTIQUIN));
        assertEquals(30, cabito.getSaciedad());
        assertEquals(30, cabito.getHigiene());
        assertTrue(cabito.getEstado() != Estado.ENFERMO);
    }

    @Test
    @DisplayName("Enfermo sí puede comer, pero no equiparse accesorios")
    void enfermoComePeroNoSeEquipa() {
        cabito.setSaciedad(10);
        cabito.actualizarEstado();
        agregar(Catalogo.GORRA);
        agregar(Catalogo.TINTO);
        assertFalse(cabito.usarItem(Catalogo.GORRA));
        assertFalse(inventario.estaEquipado(Catalogo.GORRA));
        assertTrue(cabito.alimentar(Catalogo.TINTO));
    }

    @Test
    @DisplayName("Equipar el Saco desde Cabito da +10 de felicidad")
    void equiparSacoDesdeCabito() {
        agregar(Catalogo.SACO);
        assertTrue(cabito.usarItem(Catalogo.SACO));
        assertEquals(80, cabito.getFelicidad());
        assertTrue(inventario.estaEquipado(Catalogo.SACO));
    }

    // ---------------- Bonus ----------------

    @Test
    @DisplayName("Con el Balón, jugar da +10 de felicidad extra")
    void jugarConBalon() {
        cabito.setFelicidad(50);
        agregar(Catalogo.BALON);
        cabito.jugar();
        assertEquals(75, cabito.getFelicidad()); // 50 + 15 + 10
    }

    @Test
    @DisplayName("Con la Gorra, la felicidad baja la mitad de rápido")
    void gorraFelicidadLenta() {
        agregar(Catalogo.GORRA);
        cabito.usarItem(Catalogo.GORRA);
        for (int i = 0; i < 10; i++) {
            cabito.pasarTiempo();
        }
        assertEquals(65, cabito.getFelicidad()); // sin gorra serían 60
    }

    @Test
    @DisplayName("Con la Gorra y Cabito descuidado, también baja la mitad del castigo")
    void gorraConCastigo() {
        cabito.setSaciedad(15);
        agregar(Catalogo.GORRA);
        inventario.equipar(Catalogo.GORRA, cabito);
        cabito.pasarTiempo(); // bajada 3: tic impar → 2
        cabito.pasarTiempo(); // bajada 3: tic par → 1
        assertEquals(67, cabito.getFelicidad()); // sin gorra serían 64
    }

    @Test
    @DisplayName("Con el Termo, la energía baja la mitad de rápido")
    void termoEnergiaLenta() {
        agregar(Catalogo.TERMO);
        cabito.usarItem(Catalogo.TERMO);
        for (int i = 0; i < 10; i++) {
            cabito.pasarTiempo();
        }
        assertEquals(70, cabito.getEnergia()); // sin termo serían 60
    }

    @Test
    @DisplayName("Sin accesorios el paso del tiempo es el normal")
    void sinAccesorios() {
        for (int i = 0; i < 10; i++) {
            cabito.pasarTiempo();
        }
        assertEquals(60, cabito.getFelicidad());
        assertEquals(60, cabito.getEnergia());
    }
}
