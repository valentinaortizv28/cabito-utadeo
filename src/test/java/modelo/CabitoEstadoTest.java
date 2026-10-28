package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Cabito#actualizarEstado()}: un caso por cada estado,
 * el orden de prioridad y los valores límite.
 */
class CabitoEstadoTest {

    private Cabito cabito;

    @BeforeEach
    void setUp() {
        cabito = new Cabito("Cabito");
    }

    /** Pone los cinco atributos de una vez para que cada prueba sea corta. */
    private void configurar(int saciedad, int energia, int felicidad, int higiene, int inteligencia) {
        cabito.setSaciedad(saciedad);
        cabito.setEnergia(energia);
        cabito.setFelicidad(felicidad);
        cabito.setHigiene(higiene);
        cabito.setInteligencia(inteligencia);
    }

    // ---------------- Un caso por estado ----------------

    @Test
    @DisplayName("Una partida nueva empieza en NORMAL")
    void estadoInicialEsNormal() {
        assertEquals(Estado.NORMAL, cabito.getEstado());
    }

    @Test
    @DisplayName("DORMIDO cuando se manda a dormir con energía < 100")
    void dormido() {
        configurar(80, 50, 70, 80, 60);
        cabito.dormir();
        assertEquals(Estado.DORMIDO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("ENFERMO cuando la saciedad es < 15")
    void enfermoPorSaciedad() {
        configurar(14, 80, 70, 80, 60);
        assertEquals(Estado.ENFERMO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("ENFERMO cuando la higiene es < 15")
    void enfermoPorHigiene() {
        configurar(80, 80, 70, 14, 60);
        assertEquals(Estado.ENFERMO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("HAMBRIENTO cuando la saciedad es < 30")
    void hambriento() {
        configurar(29, 80, 70, 80, 60);
        assertEquals(Estado.HAMBRIENTO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("CANSADO cuando la energía es < 25")
    void cansado() {
        configurar(80, 24, 70, 80, 60);
        assertEquals(Estado.CANSADO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("SUCIO cuando la higiene es < 25")
    void sucio() {
        configurar(80, 80, 70, 24, 60);
        assertEquals(Estado.SUCIO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("TRISTE cuando la felicidad es < 30")
    void triste() {
        configurar(80, 80, 29, 80, 60);
        assertEquals(Estado.TRISTE, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("FELIZ con felicidad > 75 y los demás > 50")
    void feliz() {
        configurar(51, 51, 76, 51, 51);
        assertEquals(Estado.FELIZ, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("NORMAL cuando ninguna otra condición aplica")
    void normal() {
        configurar(60, 60, 60, 60, 60);
        assertEquals(Estado.NORMAL, cabito.actualizarEstado());
    }

    // ---------------- Valores límite ----------------

    @Test
    @DisplayName("Saciedad 15 exacta ya no es ENFERMO sino HAMBRIENTO")
    void limiteEnfermo() {
        configurar(15, 80, 70, 80, 60);
        assertEquals(Estado.HAMBRIENTO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("Saciedad 30, energía 25, higiene 25 y felicidad 30 exactas dan NORMAL")
    void limitesInferioresDanNormal() {
        configurar(30, 25, 30, 25, 60);
        assertEquals(Estado.NORMAL, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("Felicidad 75 exacta no es FELIZ")
    void felicidad75NoEsFeliz() {
        configurar(80, 80, 75, 80, 80);
        assertEquals(Estado.NORMAL, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("Con un atributo en 50 exacto no es FELIZ")
    void atributoEn50NoEsFeliz() {
        configurar(80, 80, 90, 80, 50);
        assertEquals(Estado.NORMAL, cabito.actualizarEstado());
    }

    // ---------------- Prioridad ----------------

    @Test
    @DisplayName("DORMIDO tiene prioridad sobre ENFERMO")
    void dormidoGanaAEnfermo() {
        configurar(5, 50, 10, 5, 60);
        cabito.dormir();
        assertEquals(Estado.DORMIDO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("ENFERMO tiene prioridad sobre HAMBRIENTO, CANSADO y TRISTE")
    void enfermoGanaAHambriento() {
        configurar(10, 10, 10, 80, 60);
        assertEquals(Estado.ENFERMO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("HAMBRIENTO tiene prioridad sobre CANSADO")
    void hambrientoGanaACansado() {
        configurar(20, 10, 70, 80, 60);
        assertEquals(Estado.HAMBRIENTO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("CANSADO tiene prioridad sobre SUCIO")
    void cansadoGanaASucio() {
        configurar(80, 10, 70, 20, 60);
        assertEquals(Estado.CANSADO, cabito.actualizarEstado());
    }

    @Test
    @DisplayName("SUCIO tiene prioridad sobre TRISTE")
    void sucioGanaATriste() {
        configurar(80, 80, 10, 20, 60);
        assertEquals(Estado.SUCIO, cabito.actualizarEstado());
    }

    // ---------------- Dormir y despertar ----------------

    @Test
    @DisplayName("Si se manda a dormir con energía 100 se despierta de inmediato")
    void dormirConEnergiaLlena() {
        configurar(80, 100, 70, 80, 60);
        cabito.dormir();
        assertFalse(cabito.isDormido());
        assertEquals(Estado.NORMAL, cabito.getEstado());
    }

    @Test
    @DisplayName("Despertar quita el estado DORMIDO y recalcula el estado")
    void despertar() {
        configurar(80, 20, 70, 80, 60);
        cabito.dormir();
        cabito.despertar();
        assertFalse(cabito.isDormido());
        assertEquals(Estado.CANSADO, cabito.getEstado());
    }

    // ---------------- Acciones permitidas por estado ----------------

    @Test
    @DisplayName("DORMIDO solo permite despertar")
    void accionesDormido() {
        assertTrue(Estado.DORMIDO.soloPermiteDespertar());
        assertFalse(Estado.DORMIDO.permiteJugar());
        assertFalse(Estado.DORMIDO.permiteMinijuegos());
    }

    @Test
    @DisplayName("ENFERMO no permite jugar ni minijuegos")
    void accionesEnfermo() {
        assertFalse(Estado.ENFERMO.soloPermiteDespertar());
        assertFalse(Estado.ENFERMO.permiteJugar());
        assertFalse(Estado.ENFERMO.permiteMinijuegos());
    }

    @Test
    @DisplayName("CANSADO permite jugar pero no minijuegos")
    void accionesCansado() {
        assertTrue(Estado.CANSADO.permiteJugar());
        assertFalse(Estado.CANSADO.permiteMinijuegos());
    }

    @Test
    @DisplayName("Los demás estados permiten todo")
    void accionesOtrosEstados() {
        for (Estado e : new Estado[] {Estado.HAMBRIENTO, Estado.SUCIO, Estado.TRISTE,
                Estado.FELIZ, Estado.NORMAL}) {
            assertFalse(e.soloPermiteDespertar(), e.name());
            assertTrue(e.permiteJugar(), e.name());
            assertTrue(e.permiteMinijuegos(), e.name());
        }
    }

    @Test
    @DisplayName("Jugar no hace nada si Cabito está enfermo")
    void noJuegaEnfermo() {
        configurar(10, 80, 50, 80, 60);
        cabito.actualizarEstado();
        assertFalse(cabito.jugar());
        assertEquals(50, cabito.getFelicidad());
    }

    @Test
    @DisplayName("Bañar no hace nada si Cabito está dormido")
    void noSeBanaDormido() {
        configurar(80, 50, 70, 40, 60);
        cabito.dormir();
        assertFalse(cabito.banar());
        assertEquals(40, cabito.getHigiene());
    }
}
