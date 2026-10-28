package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Cabito#pasarTiempo()}: cada llamada equivale a 10
 * segundos de juego, así que no hace falta esperar tiempo real.
 */
class CabitoTiempoTest {

    private Cabito cabito;

    @BeforeEach
    void setUp() {
        cabito = new Cabito("Cabito");
        cabito.setSaciedad(80);
        cabito.setEnergia(80);
        cabito.setFelicidad(70);
        cabito.setHigiene(80);
        cabito.setInteligencia(60);
    }

    @Test
    @DisplayName("Un tic normal: saciedad −3, energía −2, higiene −2, felicidad −1")
    void ticNormal() {
        cabito.pasarTiempo();
        assertEquals(77, cabito.getSaciedad());
        assertEquals(78, cabito.getEnergia());
        assertEquals(78, cabito.getHigiene());
        assertEquals(69, cabito.getFelicidad());
        assertEquals(60, cabito.getInteligencia());
    }

    @Test
    @DisplayName("Con saciedad < 20 la felicidad baja 2 adicionales")
    void castigoPorHambre() {
        cabito.setSaciedad(22); // queda en 19 después del tic
        cabito.pasarTiempo();
        assertEquals(19, cabito.getSaciedad());
        assertEquals(67, cabito.getFelicidad());
    }

    @Test
    @DisplayName("Con higiene < 20 la felicidad baja 2 adicionales")
    void castigoPorSuciedad() {
        cabito.setHigiene(21); // queda en 19 después del tic
        cabito.pasarTiempo();
        assertEquals(67, cabito.getFelicidad());
    }

    @Test
    @DisplayName("Si queda justo en 20 no hay castigo extra")
    void sinCastigoEn20() {
        cabito.setSaciedad(23);
        cabito.setHigiene(22);
        cabito.pasarTiempo();
        assertEquals(20, cabito.getSaciedad());
        assertEquals(20, cabito.getHigiene());
        assertEquals(69, cabito.getFelicidad());
    }

    @Test
    @DisplayName("Dormido: energía +8 y saciedad solo −1")
    void ticDormido() {
        cabito.dormir();
        cabito.pasarTiempo();
        assertEquals(79, cabito.getSaciedad());
        assertEquals(88, cabito.getEnergia());
        assertEquals(78, cabito.getHigiene());
        assertEquals(69, cabito.getFelicidad());
        assertEquals(Estado.DORMIDO, cabito.getEstado());
    }

    @Test
    @DisplayName("Se despierta solo al llegar a energía 100")
    void despiertaSolo() {
        cabito.setEnergia(90);
        cabito.dormir();
        cabito.pasarTiempo(); // 98
        assertTrue(cabito.isDormido());
        cabito.pasarTiempo(); // 100 (limitado)
        assertEquals(100, cabito.getEnergia());
        assertFalse(cabito.isDormido());
        assertEquals(Estado.NORMAL, cabito.getEstado());
    }

    @Test
    @DisplayName("Los atributos nunca bajan de 0 aunque pase mucho tiempo")
    void nuncaBajaDeCero() {
        for (int i = 0; i < 200; i++) {
            cabito.pasarTiempo();
        }
        assertEquals(0, cabito.getSaciedad());
        assertEquals(0, cabito.getEnergia());
        assertEquals(0, cabito.getHigiene());
        assertEquals(0, cabito.getFelicidad());
        assertEquals(Estado.ENFERMO, cabito.getEstado());
    }

    @Test
    @DisplayName("El tiempo cambia el estado: sin cuidados Cabito termina hambriento")
    void tiempoCambiaEstado() {
        // saciedad 80 → baja 3 por tic; tras 17 tics queda en 29 (< 30)
        for (int i = 0; i < 17; i++) {
            cabito.pasarTiempo();
        }
        assertEquals(29, cabito.getSaciedad());
        assertEquals(Estado.HAMBRIENTO, cabito.getEstado());
    }
}
