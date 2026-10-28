package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas generales de {@link Cabito}: valores iniciales, límites de los
 * atributos, acciones básicas y experiencia.
 */
class CabitoTest {

    private Cabito cabito;

    @BeforeEach
    void setUp() {
        cabito = new Cabito("Cabito");
    }

    @Test
    @DisplayName("Valores iniciales de una partida nueva")
    void valoresIniciales() {
        assertEquals("Cabito", cabito.getNombre());
        assertEquals(80, cabito.getSaciedad());
        assertEquals(80, cabito.getEnergia());
        assertEquals(70, cabito.getFelicidad());
        assertEquals(80, cabito.getHigiene());
        assertEquals(60, cabito.getInteligencia());
        assertEquals(1, cabito.getNivel());
        assertEquals(0, cabito.getExperiencia());
        assertFalse(cabito.isDormido());
    }

    @Test
    @DisplayName("Los setters limitan los atributos al rango 0–100")
    void settersLimitan() {
        cabito.setSaciedad(150);
        cabito.setEnergia(-20);
        cabito.setFelicidad(101);
        cabito.setHigiene(-1);
        cabito.setInteligencia(500);
        assertEquals(100, cabito.getSaciedad());
        assertEquals(0, cabito.getEnergia());
        assertEquals(100, cabito.getFelicidad());
        assertEquals(0, cabito.getHigiene());
        assertEquals(100, cabito.getInteligencia());
    }

    @Test
    @DisplayName("El hambre que ve el usuario es 100 - saciedad")
    void hambre() {
        cabito.setSaciedad(30);
        assertEquals(70, cabito.getHambre());
    }

    @Test
    @DisplayName("Jugar: felicidad +15, energía −10, higiene −5, +10 XP; luego queda FELIZ")
    void jugar() {
        assertTrue(cabito.jugar());
        assertEquals(85, cabito.getFelicidad());
        assertEquals(70, cabito.getEnergia());
        assertEquals(75, cabito.getHigiene());
        assertEquals(10, cabito.getExperiencia());
        assertEquals(Estado.FELIZ, cabito.getEstado());
    }

    @Test
    @DisplayName("Bañar: higiene queda en 100 y felicidad −5")
    void banar() {
        cabito.setHigiene(20);
        assertTrue(cabito.banar());
        assertEquals(100, cabito.getHigiene());
        assertEquals(65, cabito.getFelicidad());
    }

    @Test
    @DisplayName("Se puede bañar a Cabito enfermo")
    void banarEnfermo() {
        cabito.setHigiene(5);
        cabito.actualizarEstado();
        assertEquals(Estado.ENFERMO, cabito.getEstado());
        assertTrue(cabito.banar());
        assertEquals(Estado.NORMAL, cabito.getEstado());
    }

    @Test
    @DisplayName("Para subir del nivel 1 se necesitan 100 XP")
    void subirNivel() {
        assertFalse(cabito.ganarExperiencia(99));
        assertEquals(1, cabito.getNivel());
        assertTrue(cabito.ganarExperiencia(1));
        assertEquals(2, cabito.getNivel());
        assertEquals(0, cabito.getExperiencia());
        assertEquals(200, cabito.getExperienciaParaSubir());
    }

    @Test
    @DisplayName("La experiencia sobrante se conserva y se pueden subir varios niveles")
    void variosNiveles() {
        // nivel 1→2 cuesta 100, 2→3 cuesta 200: con 350 queda en nivel 3 con 50 XP
        assertTrue(cabito.ganarExperiencia(350));
        assertEquals(3, cabito.getNivel());
        assertEquals(50, cabito.getExperiencia());
    }

    @Test
    @DisplayName("Experiencia negativa o cero no hace nada")
    void experienciaNoPositiva() {
        assertFalse(cabito.ganarExperiencia(0));
        assertFalse(cabito.ganarExperiencia(-30));
        assertEquals(0, cabito.getExperiencia());
    }
}
