package controlador;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import javax.swing.Timer;

import minijuegos.Minijuego;
import modelo.Cabito;
import modelo.Estado;
import modelo.Partida;
import modelo.items.Accesorio;
import modelo.items.Catalogo;
import modelo.items.Comida;
import modelo.items.Inventario;
import modelo.items.Item;
import modelo.items.Juguete;
import modelo.items.ResultadoCompra;
import modelo.mundos.Mundo;
import modelo.mundos.ResultadoActividad;
import persistencia.GestorPartidas;
import persistencia.PersistenciaException;

/**
 * Controlador del juego (la "C" de MVC).
 * <p>
 * Recibe lo que hace el usuario desde la vista, se lo pide al modelo
 * ({@link Partida}) y le dice a la vista qué mostrar. También maneja el
 * reloj del juego: un {@link Timer} de Swing que cada 10 segundos llama a
 * {@link Partida#pasarTiempo()}.
 */
public class JuegoController {

    /** Cada cuántos milisegundos pasa un "tic" de tiempo de juego. */
    public static final int MILISEGUNDOS_POR_TIC = 10_000;
    /** Largo máximo del nombre de Cabito. */
    public static final int LARGO_MAXIMO_NOMBRE = 20;

    private final VistaJuego vista;
    private final GestorPartidas gestor;
    private final Timer reloj;
    private Partida partida;

    /**
     * Crea el controlador.
     *
     * @param vista  interfaz gráfica que se va a controlar
     * @param gestor encargado de guardar y cargar partidas
     */
    public JuegoController(VistaJuego vista, GestorPartidas gestor) {
        this.vista = vista;
        this.gestor = gestor;
        this.reloj = new Timer(MILISEGUNDOS_POR_TIC, e -> tic());
    }

    /**
     * Arranca la aplicación mostrando la pantalla inicial.
     */
    public void iniciar() {
        vista.mostrarInicio();
    }

    // ------------------------------------------------------------------
    // Partidas
    // ------------------------------------------------------------------

    /**
     * Pide un nombre y empieza una partida nueva.
     *
     * @return {@code true} si se creó la partida
     */
    public boolean nuevaPartida() {
        String nombre = vista.pedirNombre();
        if (nombre == null) {
            return false;
        }
        nombre = nombre.trim();
        if (nombre.isEmpty()) {
            vista.mostrarError("El nombre no puede estar vacío.");
            return false;
        }
        if (nombre.length() > LARGO_MAXIMO_NOMBRE) {
            vista.mostrarError("El nombre puede tener máximo " + LARGO_MAXIMO_NOMBRE + " caracteres.");
            return false;
        }
        if (gestor.existe(nombre) && !vista.confirmar("Nombre en uso",
                "Ya hay una partida guardada llamada \"" + nombre + "\".\n"
                        + "Si guardas esta partida nueva, la anterior se reemplazará. ¿Continuar?")) {
            return false;
        }
        empezar(new Partida(nombre));
        vista.mostrarDialogo("¡Hola! Soy " + nombre + ". ¡Vamos a recorrer la Tadeo!");
        return true;
    }

    /**
     * Muestra las partidas guardadas, deja elegir una y la carga.
     *
     * @return {@code true} si se cargó una partida
     */
    public boolean cargarPartida() {
        List<String> nombres;
        try {
            nombres = gestor.listarPartidas();
        } catch (PersistenciaException e) {
            vista.mostrarError(e.getMessage());
            return false;
        }
        if (nombres.isEmpty()) {
            vista.mostrarAviso("Cargar partida", "Todavía no hay partidas guardadas.");
            return false;
        }
        String elegida = vista.elegir("Cargar partida", "Elige la partida que quieres cargar:", nombres);
        if (elegida == null) {
            return false;
        }
        if (partida != null && !vista.confirmar("Cargar partida",
                "Se perderá lo que no hayas guardado de la partida actual. ¿Continuar?")) {
            return false;
        }
        try {
            empezar(gestor.cargar(elegida));
        } catch (PersistenciaException e) {
            vista.mostrarError(e.getMessage());
            return false;
        }
        vista.mostrarDialogo("¡Qué bueno verte de nuevo! Sigamos donde íbamos.");
        return true;
    }

    /**
     * Guarda la partida actual.
     *
     * @return {@code true} si se guardó
     */
    public boolean guardarPartida() {
        if (partida == null) {
            return false;
        }
        try {
            Path archivo = gestor.guardar(partida);
            vista.mostrarAviso("Partida guardada", "La partida se guardó en:\n" + archivo);
            return true;
        } catch (PersistenciaException e) {
            vista.mostrarError(e.getMessage());
            return false;
        }
    }

    /**
     * Pone una partida en juego: la muestra y arranca el reloj.
     *
     * @param nueva partida a jugar
     */
    private void empezar(Partida nueva) {
        partida = nueva;
        vista.mostrarJuego(partida);
        reloj.restart();
    }

    /**
     * Pregunta si se quiere guardar y cierra el juego. Si el usuario cancela
     * o el guardado falla, el juego sigue abierto.
     */
    public void salir() {
        if (partida != null) {
            OpcionSalir opcion = vista.preguntarGuardarAntesDeSalir();
            if (opcion == OpcionSalir.CANCELAR) {
                return;
            }
            if (opcion == OpcionSalir.GUARDAR && !guardarPartida()) {
                return;
            }
        }
        reloj.stop();
        vista.cerrar();
    }

    // ------------------------------------------------------------------
    // Paso del tiempo
    // ------------------------------------------------------------------

    /**
     * Avanza el tiempo del juego 10 segundos. Lo llama el reloj; si el
     * estado de Cabito cambia, Cabito lo cuenta en su globo.
     */
    public void tic() {
        if (partida == null) {
            return;
        }
        Estado antes = partida.getCabito().getEstado();
        partida.pasarTiempo();
        Estado ahora = partida.getCabito().getEstado();
        if (ahora != antes) {
            vista.mostrarDialogo(ahora.getDescripcion());
        }
        vista.actualizar(partida);
    }

    // ------------------------------------------------------------------
    // Acciones básicas
    // ------------------------------------------------------------------

    /** Juega con Cabito. */
    public void jugar() {
        ejecutar(() -> partida.jugar(), "¡Qué divertido! ¿Jugamos otra vez?",
                "Ahora no puedo jugar. " + descripcionEstado());
    }

    /** Baña a Cabito. */
    public void banar() {
        ejecutar(() -> partida.banar(), "¡Quedé limpiecito! Aunque el agua no me encanta...",
                "Ahora no me puedes bañar. " + descripcionEstado());
    }

    /** Manda a dormir a Cabito o lo despierta, según cómo esté. */
    public void dormirODespertar() {
        if (partida == null) {
            return;
        }
        if (partida.getCabito().isDormido()) {
            ejecutar(() -> partida.despertar(), "¡Buenos días! Ya estoy despierto.", "");
        } else {
            ejecutar(() -> partida.dormir(), "Zzz... buenas noches.",
                    "¡No tengo sueño! Tengo la energía llena.");
        }
    }

    /**
     * Le da de comer a Cabito. Si hay varias comidas en el inventario,
     * pregunta cuál.
     */
    public void alimentar() {
        if (partida == null) {
            return;
        }
        Inventario inventario = partida.getCabito().getInventario();
        List<Comida> comidas = inventario.getComidas();
        if (comidas.isEmpty()) {
            vista.mostrarDialogo("No tengo comida... ¿Me compras algo en la Tienda Tadeísta?");
            return;
        }
        String nombreComida;
        if (comidas.size() == 1) {
            nombreComida = comidas.get(0).getNombre();
        } else {
            Map<String, String> opciones = new LinkedHashMap<>();
            for (Comida comida : comidas) {
                opciones.put(comida.getNombre() + " (x" + inventario.getCantidad(comida.getNombre()) + ")",
                        comida.getNombre());
            }
            String elegida = vista.elegir("Alimentar", "¿Qué le das de comer?", new ArrayList<>(opciones.keySet()));
            if (elegida == null) {
                return;
            }
            nombreComida = opciones.get(elegida);
        }
        final String comida = nombreComida;
        ejecutar(() -> partida.alimentar(comida), "¡Ñam! Qué rico: " + comida + ".",
                "Ahora no puedo comer. " + descripcionEstado());
    }

    /**
     * Usa un objeto del inventario (comer, usar o equipar).
     *
     * @param nombre nombre del objeto
     * @return el mensaje que dijo Cabito, para mostrarlo también en el inventario
     */
    public String usarItem(String nombre) {
        if (partida == null) {
            return "";
        }
        Item item = Catalogo.buscar(nombre);
        Inventario inventario = partida.getCabito().getInventario();
        String mensaje;
        if (item instanceof Juguete) {
            mensaje = "Los juguetes funcionan solos: dan su bonus al jugar o estudiar.";
            vista.mostrarDialogo(mensaje);
            return mensaje;
        }
        if (item instanceof Accesorio && inventario.estaEquipado(nombre)) {
            mensaje = "Ya tengo puesto: " + nombre + ".";
            vista.mostrarDialogo(mensaje);
            return mensaje;
        }
        String exito = item instanceof Accesorio ? "¡Me queda genial: " + nombre + "!" : "¡Listo! Usé: " + nombre + ".";
        String fallo = "Ahora no puedo usar eso. " + descripcionEstado();
        boolean usado = ejecutar(() -> partida.usarItem(nombre), exito, fallo);
        return usado ? exito : fallo;
    }

    /**
     * Compra un objeto en la Tienda Tadeísta.
     *
     * @param nombre nombre del objeto
     * @return resultado de la compra, para mostrarlo en la tienda
     */
    public ResultadoCompra comprar(String nombre) {
        if (partida == null) {
            return ResultadoCompra.NO_EXISTE;
        }
        ResultadoCompra resultado = partida.comprar(nombre);
        if (resultado == ResultadoCompra.EXITO) {
            vista.mostrarDialogo("¡Gracias! Ya tengo: " + nombre + ".");
        }
        vista.actualizar(partida);
        return resultado;
    }

    // ------------------------------------------------------------------
    // Mundos
    // ------------------------------------------------------------------

    /**
     * Viaja a otro mundo, o avisa por qué no se puede.
     *
     * @param nombreMundo nombre del mundo
     */
    public void viajarA(String nombreMundo) {
        if (partida == null) {
            return;
        }
        Mundo mundo = partida.buscarMundo(nombreMundo);
        if (mundo == null) {
            return;
        }
        if (partida.viajarA(nombreMundo)) {
            vista.mostrarDialogo("¡Llegamos a " + mundo.getNombre() + "! " + mundo.getDescripcion() + ".");
        } else {
            vista.mostrarAviso("Mundo bloqueado", mundo.getNombre() + ": " + mundo.getMensajeBloqueado() + ".");
        }
        vista.actualizar(partida);
    }

    /**
     * Hace la actividad del mundo actual (botón "Explorar").
     */
    public void explorar() {
        if (partida == null) {
            return;
        }
        int nivelAntes = partida.getCabito().getNivel();
        ResultadoActividad resultado = partida.realizarActividad();
        vista.mostrarDialogo(resultado.getMensaje());
        terminarAccion(nivelAntes);
    }

    // ------------------------------------------------------------------
    // Minijuegos
    // ------------------------------------------------------------------

    /**
     * Abre un minijuego si se puede jugar; si no, explica por qué.
     *
     * @param minijuego minijuego a abrir
     */
    public void abrirMinijuego(Minijuego minijuego) {
        if (partida == null) {
            return;
        }
        if (!partida.puedeJugar(minijuego)) {
            vista.mostrarAviso(minijuego.getNombre(), partida.getMotivoNoPuedeJugar(minijuego));
            return;
        }
        vista.abrirMinijuego(minijuego);
    }

    /**
     * Recibe un minijuego terminado y entrega su premio.
     *
     * @param minijuego minijuego que terminó
     */
    public void minijuegoTerminado(Minijuego minijuego) {
        if (partida == null) {
            return;
        }
        int nivelAntes = partida.getCabito().getNivel();
        if (partida.entregarPremio(minijuego)) {
            vista.mostrarDialogo(minijuego.getResumen());
        }
        terminarAccion(nivelAntes);
    }

    // ------------------------------------------------------------------
    // Ayudantes
    // ------------------------------------------------------------------

    /**
     * Ejecuta una acción del modelo, muestra el mensaje según si funcionó y
     * actualiza la vista.
     *
     * @param accion acción que devuelve si se pudo hacer
     * @param exito  mensaje si funcionó
     * @param fallo  mensaje si no funcionó
     * @return lo que devolvió la acción
     */
    private boolean ejecutar(BooleanSupplier accion, String exito, String fallo) {
        if (partida == null) {
            return false;
        }
        int nivelAntes = partida.getCabito().getNivel();
        boolean resultado = accion.getAsBoolean();
        vista.mostrarDialogo(resultado ? exito : fallo);
        terminarAccion(nivelAntes);
        return resultado;
    }

    /**
     * Actualiza la vista y, si Cabito subió de nivel, avisa qué se desbloqueó.
     *
     * @param nivelAntes nivel antes de la acción
     */
    private void terminarAccion(int nivelAntes) {
        vista.actualizar(partida);
        Cabito cabito = partida.getCabito();
        int nivel = cabito.getNivel();
        if (nivel > nivelAntes) {
            String texto = "¡" + cabito.getNombre() + " subió al nivel " + nivel + "!";
            List<String> desbloqueos = partida.desbloqueosEntre(nivelAntes, nivel);
            if (!desbloqueos.isEmpty()) {
                texto += "\nDesbloqueaste: " + String.join(", ", desbloqueos) + ".";
            }
            vista.mostrarAviso("¡Subiste de nivel!", texto);
        }
    }

    /**
     * @return la descripción del estado actual de Cabito, para los mensajes de "no puedo"
     */
    private String descripcionEstado() {
        return partida == null ? "" : partida.getCabito().getEstado().getDescripcion();
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    /**
     * @return la partida en curso, o {@code null} si todavía no se ha empezado
     */
    public Partida getPartida() {
        return partida;
    }

    /**
     * @return {@code true} si el reloj del juego está corriendo
     */
    public boolean isRelojActivo() {
        return reloj.isRunning();
    }
}
