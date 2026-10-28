package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import minijuegos.MinijuegoMemoria;
import minijuegos.MinijuegoPreguntas;
import modelo.items.Inventario;

/**
 * Pruebas de cómo {@link Partida} habilita los minijuegos y entrega sus premios.
 */
class PartidaMinijuegosTest {

    private Partida partida;
    private Cabito cabito;

    @BeforeEach
    void setUp() {
        partida = new Partida("Cabito", new RandomFijo());
        cabito = partida.getCabito();
    }

    /** Con RandomFijo vacío el tablero queda en orden: se gana en 8 intentos. */
    private void ganarMemoria(MinijuegoMemoria juego) {
        for (int i = 0; i < 16; i++) {
            juego.seleccionar(i / 4, i % 4);
        }
    }

    @Test
    @DisplayName("La memoria requiere el Módulo 20 (nivel 4)")
    void memoriaRequiereNivel4() {
        MinijuegoMemoria juego = partida.crearMinijuegoMemoria();
        cabito.setNivel(3);
        assertFalse(partida.puedeJugar(juego));
        assertEquals("Memoria Tadeísta: Nivel 4 requerido.", partida.getMotivoNoPuedeJugar(juego));
        cabito.setNivel(4);
        assertTrue(partida.puedeJugar(juego));
        assertEquals("", partida.getMotivoNoPuedeJugar(juego));
    }

    @Test
    @DisplayName("Las preguntas requieren la Biblioteca (nivel 2)")
    void preguntasRequierenNivel2() {
        MinijuegoPreguntas juego = partida.crearMinijuegoPreguntas();
        assertFalse(partida.puedeJugar(juego));
        cabito.setNivel(2);
        assertTrue(partida.puedeJugar(juego));
    }

    @Test
    @DisplayName("Cansado o enfermo no puede jugar minijuegos")
    void estadoBloqueaMinijuegos() {
        cabito.setNivel(4);
        MinijuegoMemoria juego = partida.crearMinijuegoMemoria();

        cabito.setEnergia(20);
        cabito.actualizarEstado();
        assertEquals(Estado.CANSADO, cabito.getEstado());
        assertFalse(partida.puedeJugar(juego));
        assertTrue(partida.getMotivoNoPuedeJugar(juego).contains("cansado"));

        cabito.setEnergia(80);
        cabito.setSaciedad(10);
        cabito.actualizarEstado();
        assertFalse(partida.puedeJugar(juego));
    }

    @Test
    @DisplayName("Premio de la memoria: monedas, 40 XP e historial")
    void premioMemoria() {
        cabito.setNivel(4);
        MinijuegoMemoria juego = partida.crearMinijuegoMemoria();
        ganarMemoria(juego);
        assertTrue(partida.entregarPremio(juego));
        assertEquals(Inventario.MONEDAS_INICIALES + 50, cabito.getInventario().getMonedas());
        assertEquals(40, cabito.getExperiencia());
        assertTrue(partida.getHistorial().getUltimasAcciones(1).get(0).startsWith("Memoria Tadeísta"));
    }

    @Test
    @DisplayName("Premio de las preguntas: 5 monedas por acierto")
    void premioPreguntas() {
        cabito.setNivel(2);
        MinijuegoPreguntas juego = partida.crearMinijuegoPreguntas();
        while (!juego.estaTerminado()) {
            juego.responder(juego.getPreguntaActual().getIndiceCorrecta());
        }
        assertTrue(partida.entregarPremio(juego));
        assertEquals(Inventario.MONEDAS_INICIALES + 25, cabito.getInventario().getMonedas());
    }

    @Test
    @DisplayName("Un minijuego sin terminar no entrega premio")
    void sinTerminarNoHayPremio() {
        MinijuegoMemoria juego = partida.crearMinijuegoMemoria();
        juego.seleccionar(0, 0);
        assertFalse(partida.entregarPremio(juego));
        assertEquals(Inventario.MONEDAS_INICIALES, cabito.getInventario().getMonedas());
    }

    @Test
    @DisplayName("El premio puede subir de nivel y se registra el logro")
    void premioSubeNivel() {
        cabito.setNivel(4);
        cabito.setExperiencia(390); // nivel 4 necesita 400
        MinijuegoMemoria juego = partida.crearMinijuegoMemoria();
        ganarMemoria(juego);
        partida.entregarPremio(juego);
        assertEquals(5, cabito.getNivel());
        assertTrue(partida.getHistorial().getLogros().contains("Desbloqueaste el accesorio Mochila UTadeo"));
    }
}
