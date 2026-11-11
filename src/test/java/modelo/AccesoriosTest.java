package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.items.Bonus;
import modelo.items.Catalogo;
import modelo.items.Inventario;

/**
 * Pruebas de poner y quitar accesorios: el bonus solo cuenta mientras el
 * accesorio está puesto.
 */
class AccesoriosTest {

    private Partida partida;
    private Cabito cabito;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        partida = new Partida("Cabito", new RandomFijo());
        cabito = partida.getCabito();
        inventario = cabito.getInventario();
        cabito.setNivel(5);
        inventario.setMonedas(1000);
        for (String accesorio : new String[] {Catalogo.GORRA, Catalogo.SACO, Catalogo.TERMO, Catalogo.MOCHILA}) {
            partida.comprar(accesorio);
        }
        cabito.setFelicidad(50);
        cabito.setEnergia(80);
    }

    @Test
    @DisplayName("Poner y quitar la gorra cambia si está equipada y lo anota en el historial")
    void ponerYQuitar() {
        assertTrue(partida.usarItem(Catalogo.GORRA));
        assertTrue(inventario.estaEquipado(Catalogo.GORRA));
        assertEquals("Te pusiste: Gorra UTadeo.", partida.getHistorial().getUltimasAcciones(1).get(0));

        assertTrue(partida.quitarAccesorio(Catalogo.GORRA));
        assertFalse(inventario.estaEquipado(Catalogo.GORRA));
        assertTrue(inventario.tiene(Catalogo.GORRA), "quitarse un accesorio no lo bota");
        assertEquals("Te quitaste: Gorra UTadeo.", partida.getHistorial().getUltimasAcciones(1).get(0));
    }

    @Test
    @DisplayName("No se puede quitar un accesorio que no está puesto")
    void quitarSinPoner() {
        assertFalse(partida.quitarAccesorio(Catalogo.TERMO));
        assertFalse(partida.quitarAccesorio(Catalogo.EMPANADA));
    }

    @Test
    @DisplayName("Se puede volver a poner un accesorio después de quitárselo")
    void volverAPoner() {
        partida.usarItem(Catalogo.TERMO);
        partida.quitarAccesorio(Catalogo.TERMO);
        assertTrue(partida.usarItem(Catalogo.TERMO));
        assertTrue(inventario.estaEquipado(Catalogo.TERMO));
    }

    @Test
    @DisplayName("El bonus de la gorra y del termo solo cuenta mientras están puestos")
    void bonusContinuo() {
        partida.usarItem(Catalogo.GORRA);
        partida.usarItem(Catalogo.TERMO);
        assertTrue(inventario.tieneBonus(Bonus.FELICIDAD_LENTA));
        assertTrue(inventario.tieneBonus(Bonus.ENERGIA_LENTA));
        cabito.pasarTiempo();
        cabito.pasarTiempo();
        assertEquals(78, cabito.getEnergia()); // con termo: 1 por tic

        partida.quitarAccesorio(Catalogo.TERMO);
        partida.quitarAccesorio(Catalogo.GORRA);
        assertFalse(inventario.tieneBonus(Bonus.FELICIDAD_LENTA));
        assertFalse(inventario.tieneBonus(Bonus.ENERGIA_LENTA));
        cabito.pasarTiempo();
        assertEquals(76, cabito.getEnergia()); // sin termo: 2 por tic
    }

    @Test
    @DisplayName("La mochila solo da +20% de monedas mientras está puesta")
    void bonusMochila() {
        partida.usarItem(Catalogo.MOCHILA);
        assertEquals(30, inventario.ganarMonedasPorActividad(25));
        partida.quitarAccesorio(Catalogo.MOCHILA);
        assertEquals(25, inventario.ganarMonedasPorActividad(25));
    }

    @Test
    @DisplayName("El saco da +10 de felicidad al ponerlo y lo resta al quitarlo")
    void bonusSaco() {
        partida.usarItem(Catalogo.SACO);
        assertEquals(60, cabito.getFelicidad());
        partida.quitarAccesorio(Catalogo.SACO);
        assertEquals(50, cabito.getFelicidad());
        // Ponerlo y quitarlo varias veces no regala felicidad
        for (int i = 0; i < 5; i++) {
            partida.usarItem(Catalogo.SACO);
            partida.quitarAccesorio(Catalogo.SACO);
        }
        assertEquals(50, cabito.getFelicidad());
    }

    @Test
    @DisplayName("Dormido no se puede quitar accesorios; enfermo sí")
    void quitarSegunEstado() {
        partida.usarItem(Catalogo.GORRA);
        cabito.setEnergia(40);
        cabito.dormir();
        assertFalse(partida.quitarAccesorio(Catalogo.GORRA));
        cabito.despertar();

        cabito.setSaciedad(5);
        cabito.actualizarEstado();
        assertEquals(Estado.ENFERMO, cabito.getEstado());
        assertTrue(partida.quitarAccesorio(Catalogo.GORRA));
    }
}
