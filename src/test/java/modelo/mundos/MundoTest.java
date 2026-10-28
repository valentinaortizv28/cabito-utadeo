package modelo.mundos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.Cabito;
import modelo.RandomFijo;
import modelo.items.Catalogo;
import modelo.items.Inventario;

/**
 * Pruebas de los tres mundos y de las reglas comunes de {@link Mundo}.
 */
class MundoTest {

    private Cabito cabito;
    private Inventario inventario;

    @BeforeEach
    void setUp() {
        cabito = new Cabito("Cabito");
        inventario = cabito.getInventario();
        cabito.setSaciedad(80);
        cabito.setEnergia(80);
        cabito.setFelicidad(50);
        cabito.setHigiene(80);
        cabito.setInteligencia(50);
        cabito.actualizarEstado();
    }

    // ---------------- La Brújula ----------------

    @Test
    @DisplayName("Recorrer el campus: monedas, felicidad +10, energía −10 y 15 XP")
    void brujula() {
        // nextInt(21) = 7 → 12 monedas; nextInt(100) = 99 → sin evento
        ResultadoActividad r = new Brujula().realizarActividad(cabito, new RandomFijo(7, 99));
        assertTrue(r.isRealizada());
        assertEquals(12, r.getMonedas());
        assertEquals(Inventario.MONEDAS_INICIALES + 12, inventario.getMonedas());
        assertEquals(60, cabito.getFelicidad());
        assertEquals(70, cabito.getEnergia());
        assertEquals(15, cabito.getExperiencia());
    }

    @Test
    @DisplayName("Las monedas de la Brújula van de 5 a 25")
    void brujulaRangoMonedas() {
        assertEquals(5, new Brujula().realizarActividad(cabito, new RandomFijo(0, 99)).getMonedas());
        assertEquals(25, new Brujula().realizarActividad(cabito, new RandomFijo(20, 99)).getMonedas());
    }

    @Test
    @DisplayName("Evento '¡Encontraste una moneda!' suma 5 monedas más")
    void brujulaEventoMoneda() {
        // 10 monedas, evento (nextInt(100) = 0 < 30), tipo 0 = moneda
        ResultadoActividad r = new Brujula().realizarActividad(cabito, new RandomFijo(5, 0, 0));
        assertEquals(15, r.getMonedas());
        assertTrue(r.getMensaje().contains("¡Encontraste una moneda!"));
        assertEquals(Inventario.MONEDAS_INICIALES + 15, inventario.getMonedas());
    }

    @Test
    @DisplayName("Evento de amigos da felicidad extra y el del tinto energía extra")
    void brujulaOtrosEventos() {
        new Brujula().realizarActividad(cabito, new RandomFijo(0, 29, 1));
        assertEquals(65, cabito.getFelicidad()); // 50 + 10 + 5

        new Brujula().realizarActividad(cabito, new RandomFijo(0, 10, 2));
        assertEquals(65, cabito.getEnergia()); // 80 − 10 − 10 + 5
    }

    @Test
    @DisplayName("Con la Mochila equipada la Brújula da +20% de monedas")
    void brujulaConMochila() {
        inventario.agregar(Catalogo.buscar(Catalogo.MOCHILA));
        inventario.equipar(Catalogo.MOCHILA, cabito);
        ResultadoActividad r = new Brujula().realizarActividad(cabito, new RandomFijo(20, 99));
        assertEquals(30, r.getMonedas()); // 25 + 20%
    }

    // ---------------- Biblioteca ----------------

    @Test
    @DisplayName("Estudiar: inteligencia +10, energía −15 y 25 XP")
    void biblioteca() {
        cabito.setNivel(2);
        ResultadoActividad r = new Biblioteca().realizarActividad(cabito, new RandomFijo());
        assertTrue(r.isRealizada());
        assertEquals(60, cabito.getInteligencia());
        assertEquals(65, cabito.getEnergia());
        assertEquals(25, cabito.getExperiencia());
        assertEquals(0, r.getMonedas());
    }

    @Test
    @DisplayName("Con el Cuaderno, estudiar da +5 de inteligencia extra")
    void bibliotecaConCuaderno() {
        cabito.setNivel(2);
        inventario.agregar(Catalogo.buscar(Catalogo.CUADERNO));
        new Biblioteca().realizarActividad(cabito, new RandomFijo());
        assertEquals(65, cabito.getInteligencia());
    }

    @Test
    @DisplayName("La Biblioteca está bloqueada en nivel 1")
    void bibliotecaBloqueada() {
        ResultadoActividad r = new Biblioteca().realizarActividad(cabito, new RandomFijo());
        assertFalse(r.isRealizada());
        assertEquals("Nivel 2 requerido", r.getMensaje());
        assertEquals(50, cabito.getInteligencia());
        assertEquals(0, cabito.getExperiencia());
    }

    // ---------------- Módulo 20 ----------------

    @Test
    @DisplayName("Taller: inteligencia +5, felicidad +5 y 30 XP")
    void modulo20() {
        cabito.setNivel(4);
        ResultadoActividad r = new Modulo20().realizarActividad(cabito, new RandomFijo());
        assertTrue(r.isRealizada());
        assertEquals(55, cabito.getInteligencia());
        assertEquals(55, cabito.getFelicidad());
        assertEquals(30, cabito.getExperiencia());
    }

    @Test
    @DisplayName("El Módulo 20 requiere nivel 4")
    void modulo20Bloqueado() {
        Modulo20 modulo = new Modulo20();
        assertFalse(modulo.estaDesbloqueado(3));
        assertTrue(modulo.estaDesbloqueado(4));
        cabito.setNivel(3);
        assertFalse(modulo.realizarActividad(cabito, new RandomFijo()).isRealizada());
    }

    // ---------------- Reglas comunes ----------------

    @Test
    @DisplayName("La Brújula está desbloqueada desde el nivel 1")
    void brujulaDesbloqueada() {
        assertTrue(new Brujula().estaDesbloqueado(1));
    }

    @Test
    @DisplayName("Enfermo no puede hacer actividades")
    void enfermoNoHaceActividades() {
        cabito.setSaciedad(10);
        cabito.actualizarEstado();
        ResultadoActividad r = new Brujula().realizarActividad(cabito, new RandomFijo(7, 99));
        assertFalse(r.isRealizada());
        assertEquals(Inventario.MONEDAS_INICIALES, inventario.getMonedas());
    }

    @Test
    @DisplayName("Dormido no puede hacer actividades")
    void dormidoNoHaceActividades() {
        cabito.dormir();
        assertFalse(new Brujula().realizarActividad(cabito, new RandomFijo()).isRealizada());
    }

    @Test
    @DisplayName("Cansado sí puede hacer actividades")
    void cansadoSiHaceActividades() {
        cabito.setEnergia(20);
        cabito.actualizarEstado();
        assertTrue(new Brujula().realizarActividad(cabito, new RandomFijo()).isRealizada());
        assertEquals(10, cabito.getEnergia());
    }

    @Test
    @DisplayName("La actividad avisa cuando Cabito sube de nivel")
    void avisaSubidaDeNivel() {
        cabito.setExperiencia(90);
        ResultadoActividad r = new Brujula().realizarActividad(cabito, new RandomFijo());
        assertTrue(r.isSubioNivel());
        assertEquals(2, cabito.getNivel());
        assertEquals(5, cabito.getExperiencia());
    }
}
