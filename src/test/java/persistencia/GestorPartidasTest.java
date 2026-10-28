package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import modelo.Cabito;
import modelo.Estado;
import modelo.Partida;
import modelo.RandomFijo;
import modelo.items.Bonus;
import modelo.items.Catalogo;
import modelo.items.Inventario;
import modelo.mundos.Biblioteca;
import modelo.mundos.Brujula;
import modelo.mundos.Modulo20;
import modelo.mundos.Mundo;

/**
 * Pruebas de {@link GestorPartidas}. Cada prueba usa una carpeta temporal
 * que JUnit crea y borra sola ({@code @TempDir}), así no se ensucia la
 * carpeta {@code partidas/} real.
 */
class GestorPartidasTest {

    @TempDir
    Path carpeta;

    private GestorPartidas gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorPartidas(carpeta);
    }

    /** Arma una partida avanzada con todo tipo de datos distintos a los iniciales. */
    private Partida partidaAvanzada() {
        Partida partida = new Partida("Tadeo", new RandomFijo());
        Cabito cabito = partida.getCabito();
        Inventario inventario = cabito.getInventario();

        cabito.setNivel(4);
        cabito.setExperiencia(123);
        inventario.setMonedas(500);
        partida.comprar(Catalogo.EMPANADA);
        partida.comprar(Catalogo.EMPANADA);
        partida.comprar(Catalogo.EMPANADA);
        partida.comprar(Catalogo.KIT_ASEO);
        partida.comprar(Catalogo.BALON);
        partida.comprar(Catalogo.GORRA);
        partida.comprar(Catalogo.TERMO);
        partida.comprar(Catalogo.SACO);
        partida.usarItem(Catalogo.GORRA);
        partida.usarItem(Catalogo.SACO);

        partida.realizarActividad();             // La Brújula
        partida.viajarA(Biblioteca.NOMBRE);
        partida.realizarActividad();
        partida.realizarActividad();
        partida.viajarA(Modulo20.NOMBRE);

        cabito.setSaciedad(41);
        cabito.setEnergia(37);
        cabito.setFelicidad(88);
        cabito.setHigiene(64);
        cabito.setInteligencia(72);
        cabito.actualizarEstado();
        return partida;
    }

    private List<String> nombres(List<Mundo> mundos) {
        List<String> nombres = new ArrayList<>();
        for (Mundo mundo : mundos) {
            nombres.add(mundo.getNombre());
        }
        return nombres;
    }

    // ---------------- Ida y vuelta ----------------

    @Test
    @DisplayName("Una partida guardada y cargada queda idéntica")
    void idaYVuelta() throws Exception {
        Partida original = partidaAvanzada();
        Cabito a = original.getCabito();
        Inventario invA = a.getInventario();

        gestor.guardar(original);
        Partida cargada = gestor.cargar("Tadeo");
        Cabito b = cargada.getCabito();
        Inventario invB = b.getInventario();

        // Atributos y estado
        assertEquals(a.getNombre(), b.getNombre());
        assertEquals(a.getSaciedad(), b.getSaciedad());
        assertEquals(a.getEnergia(), b.getEnergia());
        assertEquals(a.getFelicidad(), b.getFelicidad());
        assertEquals(a.getHigiene(), b.getHigiene());
        assertEquals(a.getInteligencia(), b.getInteligencia());
        assertEquals(a.getEstado(), b.getEstado());
        assertEquals(a.isDormido(), b.isDormido());
        // Nivel, experiencia y monedas
        assertEquals(4, b.getNivel());
        assertEquals(a.getExperiencia(), b.getExperiencia());
        assertEquals(invA.getMonedas(), invB.getMonedas());
        // Inventario y accesorios equipados
        assertEquals(invA.getCantidades(), invB.getCantidades());
        assertEquals(3, invB.getCantidad(Catalogo.EMPANADA));
        assertEquals(invA.getEquipados(), invB.getEquipados());
        assertTrue(invB.estaEquipado(Catalogo.GORRA));
        assertTrue(invB.estaEquipado(Catalogo.SACO));
        assertFalse(invB.estaEquipado(Catalogo.TERMO));
        assertTrue(invB.tieneBonus(Bonus.FELICIDAD_LENTA));
        // Mundos desbloqueados y progreso
        assertEquals(nombres(original.getMundosDesbloqueados()), nombres(cargada.getMundosDesbloqueados()));
        assertEquals(List.of(Brujula.NOMBRE, Biblioteca.NOMBRE, Modulo20.NOMBRE),
                nombres(cargada.getMundosDesbloqueados()));
        assertEquals(Modulo20.NOMBRE, cargada.getMundoActual().getNombre());
        assertEquals(original.getActividadesPorMundo(), cargada.getActividadesPorMundo());
        assertEquals(2, cargada.getActividadesRealizadas(Biblioteca.NOMBRE));
        // Historial
        assertEquals(original.getHistorial().getAcciones(), cargada.getHistorial().getAcciones());
        assertEquals(original.getHistorial().getLogros(), cargada.getHistorial().getLogros());
    }

    @Test
    @DisplayName("Guardar y cargar dos veces seguidas no cambia nada")
    void dobleIdaYVuelta() throws Exception {
        gestor.guardar(partidaAvanzada());
        String primerJson = sinFecha(Files.readString(carpeta.resolve("Tadeo.json")));
        gestor.guardar(gestor.cargar("Tadeo"));
        String segundoJson = sinFecha(Files.readString(carpeta.resolve("Tadeo.json")));
        assertEquals(primerJson, segundoJson);
    }

    private String sinFecha(String json) {
        return json.replaceAll("\"fechaGuardado\": \"[^\"]*\"", "");
    }

    @Test
    @DisplayName("Una partida nueva también se guarda y se carga igual")
    void partidaNueva() throws Exception {
        Partida original = new Partida("Cabito");
        gestor.guardar(original);
        Partida cargada = gestor.cargar("Cabito");
        assertEquals(1, cargada.getCabito().getNivel());
        assertEquals(Inventario.MONEDAS_INICIALES, cargada.getCabito().getInventario().getMonedas());
        assertTrue(cargada.getCabito().getInventario().getItems().isEmpty());
        assertEquals(List.of(Brujula.NOMBRE), nombres(cargada.getMundosDesbloqueados()));
        assertEquals(Estado.NORMAL, cargada.getCabito().getEstado());
    }

    @Test
    @DisplayName("Cargar no vuelve a aplicar el efecto de los accesorios equipados")
    void cargarNoReaplicaEfectos() throws Exception {
        Partida original = partidaAvanzada(); // tiene el Saco equipado (+10 al equiparlo)
        gestor.guardar(original);
        assertEquals(88, gestor.cargar("Tadeo").getCabito().getFelicidad());
    }

    @Test
    @DisplayName("Si Cabito estaba dormido, sigue dormido al cargar")
    void dormidoSeConserva() throws Exception {
        Partida original = new Partida("Dormilón");
        original.getCabito().setEnergia(40);
        original.dormir();
        gestor.guardar(original);
        assertTrue(Files.exists(carpeta.resolve("Dormilon.json")));
        Cabito cargado = gestor.cargar("Dormilón").getCabito();
        assertEquals("Dormilón", cargado.getNombre());
        assertTrue(cargado.isDormido());
        assertEquals(Estado.DORMIDO, cargado.getEstado());
    }

    // ---------------- Archivos ----------------

    @Test
    @DisplayName("El archivo se llama como Cabito, es JSON legible y la carpeta se crea sola")
    void archivoJson() throws Exception {
        GestorPartidas enSubcarpeta = new GestorPartidas(carpeta.resolve("partidas"));
        Path archivo = enSubcarpeta.guardar(partidaAvanzada());
        assertEquals(carpeta.resolve("partidas").resolve("Tadeo.json"), archivo);
        String json = Files.readString(archivo, StandardCharsets.UTF_8);
        assertTrue(json.contains("\"nombre\": \"Tadeo\""));
        assertTrue(json.contains("\"Gorra UTadeo\""));
        assertTrue(json.contains("\"mundoActual\": \"Módulo 20\""));
    }

    @Test
    @DisplayName("Listar devuelve las partidas en orden alfabético")
    void listar() throws Exception {
        gestor.guardar(new Partida("Zoe"));
        gestor.guardar(new Partida("Ana"));
        gestor.guardar(new Partida("Mateo"));
        Files.writeString(carpeta.resolve("notas.txt"), "no es una partida");
        assertEquals(List.of("Ana", "Mateo", "Zoe"), gestor.listarPartidas());
    }

    @Test
    @DisplayName("Si la carpeta no existe, la lista está vacía")
    void listarSinCarpeta() throws Exception {
        assertTrue(new GestorPartidas(carpeta.resolve("no-existe")).listarPartidas().isEmpty());
    }

    @Test
    @DisplayName("Guardar con el mismo nombre reemplaza la partida anterior")
    void sobrescribir() throws Exception {
        Partida partida = new Partida("Cabito");
        gestor.guardar(partida);
        partida.getCabito().getInventario().setMonedas(999);
        gestor.guardar(partida);
        assertEquals(1, gestor.listarPartidas().size());
        assertEquals(999, gestor.cargar("Cabito").getCabito().getInventario().getMonedas());
    }

    @Test
    @DisplayName("existe() indica si ya hay una partida con ese nombre")
    void existe() throws Exception {
        assertFalse(gestor.existe("Cabito"));
        gestor.guardar(new Partida("Cabito"));
        assertTrue(gestor.existe("Cabito"));
    }

    @Test
    @DisplayName("Los caracteres no válidos en el nombre se reemplazan en el archivo")
    void nombreSeguro() throws Exception {
        assertEquals("Cabi_to_.json", GestorPartidas.nombreArchivo("Cabi/to?"));
        assertEquals("Pena Tadeista.json", GestorPartidas.nombreArchivo("Peña Tadeísta"));
        assertEquals("partida.json", GestorPartidas.nombreArchivo("   "));
        gestor.guardar(new Partida("Cabi/to?"));
        assertEquals("Cabi/to?", gestor.cargar("Cabi/to?").getCabito().getNombre());
    }

    // ---------------- Errores ----------------

    @Test
    @DisplayName("Cargar una partida que no existe da un mensaje claro")
    void cargarInexistente() {
        PersistenciaException e = assertThrows(PersistenciaException.class, () -> gestor.cargar("Nadie"));
        assertEquals("No existe una partida guardada con el nombre \"Nadie\".", e.getMessage());
    }

    @Test
    @DisplayName("Un archivo dañado da un mensaje claro en vez de un error técnico")
    void archivoDanado() throws Exception {
        Files.writeString(carpeta.resolve("Roto.json"), "{ esto no es json ");
        PersistenciaException e = assertThrows(PersistenciaException.class, () -> gestor.cargar("Roto"));
        assertTrue(e.getMessage().contains("dañado"));
    }

    @Test
    @DisplayName("Un archivo vacío o sin nombre se reporta como incompleto")
    void archivoIncompleto() throws Exception {
        Files.writeString(carpeta.resolve("Vacio.json"), "");
        assertThrows(PersistenciaException.class, () -> gestor.cargar("Vacio"));
        Files.writeString(carpeta.resolve("SinNombre.json"), "{ \"nivel\": 3 }");
        PersistenciaException e = assertThrows(PersistenciaException.class, () -> gestor.cargar("SinNombre"));
        assertTrue(e.getMessage().contains("incompleto"));
    }

    @Test
    @DisplayName("Una versión de archivo más nueva no se carga")
    void versionNueva() throws Exception {
        Files.writeString(carpeta.resolve("Futuro.json"), "{ \"version\": 99, \"nombre\": \"Futuro\" }");
        assertThrows(PersistenciaException.class, () -> gestor.cargar("Futuro"));
    }

    @Test
    @DisplayName("Si no se puede escribir en la carpeta, el error es amigable")
    void noSePuedeEscribir() throws Exception {
        Path archivoEnLugarDeCarpeta = carpeta.resolve("ocupado");
        Files.writeString(archivoEnLugarDeCarpeta, "soy un archivo");
        GestorPartidas malo = new GestorPartidas(archivoEnLugarDeCarpeta);
        PersistenciaException e = assertThrows(PersistenciaException.class, () -> malo.guardar(new Partida("X")));
        assertTrue(e.getMessage().startsWith("No se pudo guardar la partida"));
    }

    @Test
    @DisplayName("Un archivo editado a mano con datos inválidos se corrige al cargar")
    void datosInvalidosSeCorrigen() throws Exception {
        String json = "{"
                + "\"nombre\": \"Tramposo\", \"saciedad\": 500, \"energia\": -20, \"felicidad\": 50,"
                + "\"higiene\": 50, \"inteligencia\": 50, \"nivel\": 1, \"experiencia\": -5, \"monedas\": -10,"
                + "\"inventario\": {\"Empanada\": 2, \"Pizza\": 4, \"Gorra UTadeo\": 0, \"Termo Tadeísta\": 1},"
                + "\"accesoriosEquipados\": [\"Gorra UTadeo\", \"Empanada\", \"Termo Tadeísta\"],"
                + "\"mundoActual\": \"Módulo 20\""
                + "}";
        Files.writeString(carpeta.resolve("Tramposo.json"), json);
        Partida partida = gestor.cargar("Tramposo");
        Cabito cabito = partida.getCabito();
        Inventario inventario = cabito.getInventario();

        assertEquals(100, cabito.getSaciedad());
        assertEquals(0, cabito.getEnergia());
        assertEquals(0, cabito.getExperiencia());
        assertEquals(0, inventario.getMonedas());
        assertEquals(2, inventario.getCantidad(Catalogo.EMPANADA));
        assertFalse(inventario.tiene("Pizza"));
        assertFalse(inventario.tiene(Catalogo.GORRA));
        assertFalse(inventario.estaEquipado(Catalogo.GORRA));    // no lo tiene
        assertFalse(inventario.estaEquipado(Catalogo.EMPANADA)); // no es accesorio
        assertTrue(inventario.estaEquipado(Catalogo.TERMO));
        // Módulo 20 requiere nivel 4: queda en La Brújula
        assertEquals(Brujula.NOMBRE, partida.getMundoActual().getNombre());
        assertTrue(partida.getHistorial().getAcciones().isEmpty());
    }
}
