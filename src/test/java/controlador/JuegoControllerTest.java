package controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import minijuegos.MinijuegoMemoria;
import modelo.Cabito;
import modelo.Estado;
import modelo.Partida;
import modelo.items.Catalogo;
import modelo.items.Inventario;
import modelo.items.ResultadoCompra;
import modelo.mundos.Biblioteca;
import modelo.mundos.Modulo20;
import persistencia.GestorPartidas;

/**
 * Pruebas de {@link JuegoController} con una {@link VistaFalsa}: se
 * comprueba qué le pide el controlador a la vista sin abrir ventanas.
 */
class JuegoControllerTest {

    @TempDir
    Path carpeta;

    private VistaFalsa vista;
    private GestorPartidas gestor;
    private JuegoController controlador;

    @BeforeEach
    void setUp() {
        vista = new VistaFalsa();
        gestor = new GestorPartidas(carpeta);
        controlador = new JuegoController(vista, gestor);
    }

    @AfterEach
    void detenerReloj() {
        vista.respuestaSalir = OpcionSalir.NO_GUARDAR;
        controlador.salir();
    }

    /** Empieza una partida nueva llamada "Cabito". */
    private Cabito empezar() {
        vista.nombres.add("Cabito");
        assertTrue(controlador.nuevaPartida());
        return controlador.getPartida().getCabito();
    }

    // ---------------- Inicio y partidas ----------------

    @Test
    @DisplayName("Al iniciar se muestra la pantalla inicial")
    void iniciar() {
        controlador.iniciar();
        assertTrue(vista.inicioMostrado);
        assertNull(controlador.getPartida());
    }

    @Test
    @DisplayName("Nueva partida: usa el nombre sin espacios, muestra el juego y arranca el reloj")
    void nuevaPartida() {
        vista.nombres.add("  Tadeo  ");
        assertTrue(controlador.nuevaPartida());
        assertEquals("Tadeo", controlador.getPartida().getCabito().getNombre());
        assertSame(controlador.getPartida(), vista.partidaMostrada);
        assertTrue(controlador.isRelojActivo());
        assertTrue(vista.ultimoDialogo.contains("Tadeo"));
    }

    @Test
    @DisplayName("Si se cancela el nombre no se crea partida")
    void nuevaPartidaCancelada() {
        assertFalse(controlador.nuevaPartida());
        assertNull(controlador.getPartida());
        assertFalse(controlador.isRelojActivo());
    }

    @Test
    @DisplayName("Nombre vacío o muy largo muestra un error")
    void nombreInvalido() {
        vista.nombres.add("   ");
        assertFalse(controlador.nuevaPartida());
        vista.nombres.add("UnNombreDemasiadoLargoParaCabito");
        assertFalse(controlador.nuevaPartida());
        assertEquals(2, vista.errores.size());
        assertNull(controlador.getPartida());
    }

    @Test
    @DisplayName("Si el nombre ya tiene partida guardada se pide confirmación")
    void nombreRepetido() throws Exception {
        gestor.guardar(new Partida("Cabito"));
        vista.respuestaConfirmar = false;
        vista.nombres.add("Cabito");
        assertFalse(controlador.nuevaPartida());
        vista.respuestaConfirmar = true;
        vista.nombres.add("Cabito");
        assertTrue(controlador.nuevaPartida());
    }

    @Test
    @DisplayName("Guardar y cargar desde el controlador")
    void guardarYCargar() {
        Cabito cabito = empezar();
        cabito.getInventario().setMonedas(321);
        assertTrue(controlador.guardarPartida());
        assertTrue(Files.exists(carpeta.resolve("Cabito.json")));
        assertTrue(vista.ultimoAviso().startsWith("Partida guardada"));

        cabito.getInventario().setMonedas(0);
        vista.indiceElegido = 0;
        assertTrue(controlador.cargarPartida());
        assertEquals(List.of("Cabito"), vista.opcionesOfrecidas.get(0));
        assertEquals(321, controlador.getPartida().getCabito().getInventario().getMonedas());
    }

    @Test
    @DisplayName("Cargar sin partidas guardadas solo muestra un aviso")
    void cargarSinPartidas() {
        assertFalse(controlador.cargarPartida());
        assertTrue(vista.ultimoAviso().contains("no hay partidas"));
    }

    @Test
    @DisplayName("Un archivo dañado muestra el error sin cerrar el juego")
    void cargarDanado() throws Exception {
        Files.writeString(carpeta.resolve("Roto.json"), "{ no es json");
        vista.indiceElegido = 0;
        assertFalse(controlador.cargarPartida());
        assertTrue(vista.errores.get(0).contains("dañado"));
    }

    // ---------------- Acciones ----------------

    @Test
    @DisplayName("Jugar actualiza la vista y Cabito responde")
    void jugar() {
        Cabito cabito = empezar();
        int antes = vista.actualizaciones;
        controlador.jugar();
        assertEquals(85, cabito.getFelicidad());
        assertTrue(vista.actualizaciones > antes);
        assertTrue(vista.ultimoDialogo.startsWith("¡Qué divertido!"));
    }

    @Test
    @DisplayName("Si no puede jugar, Cabito explica por qué")
    void jugarEnfermo() {
        Cabito cabito = empezar();
        cabito.setSaciedad(5);
        cabito.actualizarEstado();
        controlador.jugar();
        assertTrue(vista.ultimoDialogo.contains(Estado.ENFERMO.getDescripcion()));
    }

    @Test
    @DisplayName("Dormir y despertar con el mismo botón")
    void dormirODespertar() {
        Cabito cabito = empezar();
        cabito.setEnergia(40);
        controlador.dormirODespertar();
        assertTrue(cabito.isDormido());
        controlador.dormirODespertar();
        assertFalse(cabito.isDormido());
    }

    @Test
    @DisplayName("Alimentar sin comida sugiere ir a la tienda")
    void alimentarSinComida() {
        empezar();
        controlador.alimentar();
        assertTrue(vista.ultimoDialogo.contains("Tienda Tadeísta"));
        assertTrue(vista.opcionesOfrecidas.isEmpty());
    }

    @Test
    @DisplayName("Con varias comidas se pregunta cuál dar")
    void alimentarEligiendo() {
        Cabito cabito = empezar();
        controlador.comprar(Catalogo.EMPANADA);
        controlador.comprar(Catalogo.TINTO);
        cabito.setSaciedad(40);
        vista.indiceElegido = 0;
        controlador.alimentar();
        assertEquals(List.of("Empanada (x1)", "Tinto de la cafetería (x1)"), vista.opcionesOfrecidas.get(0));
        assertEquals(65, cabito.getSaciedad());
        assertFalse(cabito.getInventario().tiene(Catalogo.EMPANADA));
    }

    @Test
    @DisplayName("Comprar devuelve el resultado para mostrarlo en la tienda")
    void comprar() {
        Cabito cabito = empezar();
        assertEquals(ResultadoCompra.EXITO, controlador.comprar(Catalogo.KIT_ASEO));
        assertEquals(Inventario.MONEDAS_INICIALES - 20, cabito.getInventario().getMonedas());
        assertEquals(ResultadoCompra.MONEDAS_INSUFICIENTES, controlador.comprar(Catalogo.TERMO));
    }

    @Test
    @DisplayName("usarItem devuelve el mensaje de Cabito")
    void usarItem() {
        Cabito cabito = empezar();
        cabito.getInventario().setMonedas(200);
        controlador.comprar(Catalogo.GORRA);
        controlador.comprar(Catalogo.BALON);
        assertTrue(controlador.usarItem(Catalogo.GORRA).contains("Me queda genial"));
        assertTrue(controlador.usarItem(Catalogo.GORRA).startsWith("Ya tengo puesto"));
        assertTrue(controlador.usarItem(Catalogo.BALON).contains("funcionan solos"));
    }

    // ---------------- Mundos, niveles y minijuegos ----------------

    @Test
    @DisplayName("Viajar a un mundo bloqueado muestra un aviso")
    void viajarBloqueado() {
        empezar();
        controlador.viajarA(Biblioteca.NOMBRE);
        assertTrue(vista.ultimoAviso().contains("Nivel 2 requerido"));
    }

    @Test
    @DisplayName("Explorar hasta subir de nivel avisa lo desbloqueado")
    void explorarSubeNivel() {
        Cabito cabito = empezar();
        cabito.setExperiencia(95);
        controlador.explorar();
        assertEquals(2, cabito.getNivel());
        assertTrue(vista.ultimoAviso().startsWith("¡Subiste de nivel!"));
        assertTrue(vista.ultimoAviso().contains("Biblioteca"));
    }

    @Test
    @DisplayName("Un minijuego bloqueado no se abre y explica por qué")
    void minijuegoBloqueado() {
        empezar();
        controlador.abrirMinijuego(controlador.getPartida().crearMinijuegoMemoria());
        assertNull(vista.minijuegoAbierto);
        assertTrue(vista.ultimoAviso().contains("Nivel 4 requerido"));
    }

    @Test
    @DisplayName("Un minijuego permitido se abre y al terminar entrega el premio")
    void minijuegoPermitido() {
        Cabito cabito = empezar();
        cabito.setNivel(4);
        MinijuegoMemoria juego = controlador.getPartida().crearMinijuegoMemoria();
        controlador.abrirMinijuego(juego);
        assertSame(juego, vista.minijuegoAbierto);

        // Resolver el tablero buscando las parejas
        for (int i = 0; i < 16; i++) {
            for (int j = i + 1; j < 16; j++) {
                if (!juego.getCarta(i / 4, i % 4).isEmparejada()
                        && juego.getCarta(i / 4, i % 4).esParejaDe(juego.getCarta(j / 4, j % 4))) {
                    juego.seleccionar(i / 4, i % 4);
                    juego.seleccionar(j / 4, j % 4);
                }
            }
        }
        controlador.minijuegoTerminado(juego);
        assertEquals(Inventario.MONEDAS_INICIALES + 50, cabito.getInventario().getMonedas());
        assertNotNull(vista.ultimoDialogo);
        assertTrue(vista.ultimoDialogo.contains("8 parejas"));
        assertTrue(controlador.getPartida().viajarA(Modulo20.NOMBRE));
    }

    // ---------------- Tiempo y salida ----------------

    @Test
    @DisplayName("Cada tic pasa el tiempo; si cambia el estado, Cabito lo dice")
    void tic() {
        Cabito cabito = empezar();
        cabito.setSaciedad(31);
        cabito.actualizarEstado();
        controlador.tic();
        assertEquals(28, cabito.getSaciedad());
        assertEquals(Estado.HAMBRIENTO.getDescripcion(), vista.ultimoDialogo);
    }

    @Test
    @DisplayName("Salir: Cancelar mantiene el juego abierto")
    void salirCancelar() {
        empezar();
        vista.respuestaSalir = OpcionSalir.CANCELAR;
        controlador.salir();
        assertFalse(vista.cerrada);
        assertTrue(controlador.isRelojActivo());
    }

    @Test
    @DisplayName("Salir: Guardar escribe el archivo, detiene el reloj y cierra")
    void salirGuardando() {
        empezar();
        vista.respuestaSalir = OpcionSalir.GUARDAR;
        controlador.salir();
        assertTrue(vista.cerrada);
        assertFalse(controlador.isRelojActivo());
        assertTrue(Files.exists(carpeta.resolve("Cabito.json")));
    }

    @Test
    @DisplayName("Salir sin guardar no escribe nada")
    void salirSinGuardar() {
        empezar();
        controlador.salir();
        assertTrue(vista.cerrada);
        assertFalse(Files.exists(carpeta.resolve("Cabito.json")));
    }
}
