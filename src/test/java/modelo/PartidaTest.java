package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.items.Catalogo;
import modelo.items.ResultadoCompra;
import modelo.mundos.Biblioteca;
import modelo.mundos.Brujula;
import modelo.mundos.Modulo20;

/**
 * Pruebas de {@link Partida}: mundos, niveles, desbloqueos e historial.
 */
class PartidaTest {

    private Partida partida;
    private Cabito cabito;

    @BeforeEach
    void setUp() {
        partida = new Partida("Cabito", new RandomFijo());
        cabito = partida.getCabito();
    }

    private String ultimaAccion() {
        return partida.getHistorial().getUltimasAcciones(1).get(0);
    }

    // ---------------- Mundos ----------------

    @Test
    @DisplayName("La partida empieza en La Brújula con tres mundos")
    void inicio() {
        assertEquals(Brujula.NOMBRE, partida.getMundoActual().getNombre());
        assertEquals(3, partida.getMundos().size());
        assertEquals(1, partida.getHistorial().getAcciones().size());
    }

    @Test
    @DisplayName("No se puede viajar a un mundo bloqueado")
    void viajarBloqueado() {
        assertFalse(partida.viajarA(Biblioteca.NOMBRE));
        assertEquals(Brujula.NOMBRE, partida.getMundoActual().getNombre());
    }

    @Test
    @DisplayName("No se puede viajar a un mundo que no existe")
    void viajarInexistente() {
        assertFalse(partida.viajarA("Cafetería"));
    }

    @Test
    @DisplayName("Con nivel suficiente se puede viajar y queda en el historial")
    void viajarDesbloqueado() {
        cabito.setNivel(4);
        assertTrue(partida.viajarA(Modulo20.NOMBRE));
        assertEquals(Modulo20.NOMBRE, partida.getMundoActual().getNombre());
        assertEquals("Viajaste a Módulo 20.", ultimaAccion());
    }

    @Test
    @DisplayName("Hacer una actividad cuenta para el progreso del mundo")
    void progresoPorMundo() {
        partida.realizarActividad();
        partida.realizarActividad();
        assertEquals(2, partida.getActividadesRealizadas(Brujula.NOMBRE));
        assertEquals(0, partida.getActividadesRealizadas(Biblioteca.NOMBRE));
    }

    @Test
    @DisplayName("Una actividad que no se pudo hacer no cuenta ni se anota")
    void actividadFallidaNoCuenta() {
        cabito.setSaciedad(5);
        cabito.actualizarEstado();
        int antes = partida.getHistorial().getAcciones().size();
        assertFalse(partida.realizarActividad().isRealizada());
        assertEquals(0, partida.getActividadesRealizadas(Brujula.NOMBRE));
        assertEquals(antes, partida.getHistorial().getAcciones().size());
    }

    // ---------------- Niveles y desbloqueos ----------------

    @Test
    @DisplayName("Subir a nivel 2 desbloquea la Biblioteca y se registra como logro")
    void subirANivel2() {
        cabito.setExperiencia(90);
        partida.realizarActividad(); // +15 XP → nivel 2
        assertEquals(2, cabito.getNivel());
        assertTrue(partida.estaDesbloqueado(partida.buscarMundo(Biblioteca.NOMBRE)));
        assertEquals(List.of("¡Cabito subió al nivel 2!", "Desbloqueaste el mundo Biblioteca"),
                partida.getHistorial().getLogros());
        assertTrue(partida.viajarA(Biblioteca.NOMBRE));
    }

    @Test
    @DisplayName("Jugar también puede subir de nivel y registrar el logro")
    void jugarSubeNivel() {
        cabito.setExperiencia(95);
        assertTrue(partida.jugar());
        assertEquals(2, cabito.getNivel());
        assertEquals(2, partida.getHistorial().getLogros().size());
    }

    @Test
    @DisplayName("Desbloqueos de cada nivel: mundos y accesorios")
    void desbloqueosPorNivel() {
        assertEquals(List.of("el mundo Biblioteca"), partida.desbloqueosEntre(1, 2));
        assertEquals(List.of("el accesorio Saco \"UTadeo\""), partida.desbloqueosEntre(2, 3));
        assertEquals(List.of("el mundo Módulo 20"), partida.desbloqueosEntre(3, 4));
        assertEquals(List.of("el accesorio Mochila UTadeo"), partida.desbloqueosEntre(4, 5));
        assertTrue(partida.desbloqueosEntre(5, 10).isEmpty());
    }

    @Test
    @DisplayName("Si se suben varios niveles de una vez se registran todos los desbloqueos")
    void variosNivelesDeUnaVez() {
        assertEquals(List.of("el mundo Biblioteca", "el mundo Módulo 20", "el accesorio Saco \"UTadeo\""),
                partida.desbloqueosEntre(1, 4));
    }

    @Test
    @DisplayName("El Saco se puede comprar al llegar al nivel 3")
    void sacoDesbloqueadoEnNivel3() {
        cabito.getInventario().setMonedas(200);
        assertEquals(ResultadoCompra.NIVEL_INSUFICIENTE, partida.comprar(Catalogo.SACO));
        cabito.setNivel(3);
        assertEquals(ResultadoCompra.EXITO, partida.comprar(Catalogo.SACO));
    }

    @Test
    @DisplayName("Progresión real: con actividades de la Brújula se llega al nivel 2")
    void progresionReal() {
        // 100 XP / 15 por recorrido = 7 recorridos. Se recarga la energía para que no se canse.
        for (int i = 0; i < 7; i++) {
            cabito.setEnergia(100);
            assertTrue(partida.realizarActividad().isRealizada());
        }
        assertEquals(2, cabito.getNivel());
        assertEquals(5, cabito.getExperiencia());
    }

    // ---------------- Historial de acciones ----------------

    @Test
    @DisplayName("Las acciones exitosas quedan en el historial")
    void accionesEnHistorial() {
        partida.jugar();
        assertEquals("Jugaste con Cabito.", ultimaAccion());
        partida.banar();
        assertEquals("Bañaste a Cabito.", ultimaAccion());
        partida.comprar(Catalogo.EMPANADA);
        assertEquals("Compraste Empanada por 15 monedas.", ultimaAccion());
        partida.alimentar(Catalogo.EMPANADA);
        assertEquals("Cabito comió: Empanada.", ultimaAccion());
    }

    @Test
    @DisplayName("Dormir y despertar quedan en el historial")
    void dormirYDespertar() {
        cabito.setEnergia(50);
        assertTrue(partida.dormir());
        assertEquals("Cabito se fue a dormir.", ultimaAccion());
        assertFalse(partida.dormir());
        assertTrue(partida.despertar());
        assertEquals("Despertaste a Cabito.", ultimaAccion());
        assertFalse(partida.despertar());
    }

    @Test
    @DisplayName("Las acciones fallidas no se anotan")
    void fallidasNoSeAnotan() {
        int antes = partida.getHistorial().getAcciones().size();
        partida.alimentar(Catalogo.EMPANADA); // no tiene empanada
        cabito.getInventario().setMonedas(0);
        partida.comprar(Catalogo.EMPANADA);   // sin monedas
        assertEquals(antes, partida.getHistorial().getAcciones().size());
    }

    @Test
    @DisplayName("Cuando el tiempo cambia el estado, se anota en el historial")
    void cambioDeEstadoPorTiempo() {
        cabito.setSaciedad(31);
        cabito.actualizarEstado();
        partida.pasarTiempo(); // 28 → HAMBRIENTO
        assertEquals(Estado.HAMBRIENTO, cabito.getEstado());
        assertTrue(ultimaAccion().startsWith("Cabito ahora está hambriento"));
    }
}
