package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import minijuegos.Minijuego;
import minijuegos.MinijuegoMemoria;
import minijuegos.MinijuegoPreguntas;
import modelo.items.Catalogo;
import modelo.items.Item;
import modelo.items.ResultadoCompra;
import modelo.items.Tienda;
import modelo.mundos.Biblioteca;
import modelo.mundos.Brujula;
import modelo.mundos.Modulo20;
import modelo.mundos.Mundo;
import modelo.mundos.ResultadoActividad;

/**
 * Una partida completa: Cabito, la tienda, los mundos, el progreso y el
 * historial.
 * <p>
 * Es la puerta de entrada al modelo: el controlador le pide las acciones a
 * la partida y ella las ejecuta, las anota en el historial y revisa si
 * Cabito subió de nivel para registrar lo que se desbloqueó.
 */
public class Partida {

    private final Cabito cabito;
    private final Tienda tienda;
    private final List<Mundo> mundos;
    private final Map<String, Integer> actividadesPorMundo;
    private final Historial historial;
    private final Random random;
    private String mundoActual;

    /**
     * Crea una partida nueva.
     *
     * @param nombreCabito nombre que el jugador le da a Cabito
     */
    public Partida(String nombreCabito) {
        this(nombreCabito, new Random());
    }

    /**
     * Crea una partida nueva con un generador aleatorio dado (para pruebas).
     *
     * @param nombreCabito nombre que el jugador le da a Cabito
     * @param random       generador de números aleatorios para los mundos
     */
    public Partida(String nombreCabito, Random random) {
        this.cabito = new Cabito(nombreCabito);
        this.tienda = new Tienda();
        this.mundos = List.of(new Brujula(), new Biblioteca(), new Modulo20());
        this.actividadesPorMundo = new HashMap<>();
        this.historial = new Historial();
        this.random = random;
        this.mundoActual = Brujula.NOMBRE;
        historial.registrar("¡Comienza la aventura de " + nombreCabito + " en la Tadeo!");
    }

    // ------------------------------------------------------------------
    // Acciones básicas
    // ------------------------------------------------------------------

    /**
     * Juega con Cabito y lo anota en el historial.
     *
     * @return {@code true} si pudo jugar
     */
    public boolean jugar() {
        int nivelAntes = cabito.getNivel();
        if (!cabito.jugar()) {
            return false;
        }
        historial.registrar("Jugaste con " + cabito.getNombre() + ".");
        revisarSubidaDeNivel(nivelAntes);
        return true;
    }

    /**
     * Baña a Cabito y lo anota en el historial.
     *
     * @return {@code true} si se pudo bañar
     */
    public boolean banar() {
        if (!cabito.banar()) {
            return false;
        }
        historial.registrar("Bañaste a " + cabito.getNombre() + ".");
        return true;
    }

    /**
     * Manda a dormir a Cabito.
     *
     * @return {@code false} si ya estaba dormido o tiene la energía llena
     */
    public boolean dormir() {
        if (cabito.isDormido()) {
            return false;
        }
        cabito.dormir();
        if (!cabito.isDormido()) {
            return false;
        }
        historial.registrar(cabito.getNombre() + " se fue a dormir.");
        return true;
    }

    /**
     * Despierta a Cabito.
     *
     * @return {@code false} si no estaba dormido
     */
    public boolean despertar() {
        if (!cabito.isDormido()) {
            return false;
        }
        cabito.despertar();
        historial.registrar("Despertaste a " + cabito.getNombre() + ".");
        return true;
    }

    /**
     * Le da a Cabito una comida del inventario.
     *
     * @param nombreComida nombre de la comida
     * @return {@code true} si comió
     */
    public boolean alimentar(String nombreComida) {
        if (!cabito.alimentar(nombreComida)) {
            return false;
        }
        historial.registrar(cabito.getNombre() + " comió: " + nombreComida + ".");
        return true;
    }

    /**
     * Usa un objeto del inventario (consumir o equipar).
     *
     * @param nombre nombre del objeto
     * @return {@code true} si se usó
     */
    public boolean usarItem(String nombre) {
        if (!cabito.usarItem(nombre)) {
            return false;
        }
        historial.registrar("Usaste: " + nombre + ".");
        return true;
    }

    /**
     * Compra un objeto en la Tienda Tadeísta.
     *
     * @param nombre nombre del objeto
     * @return resultado de la compra
     */
    public ResultadoCompra comprar(String nombre) {
        ResultadoCompra resultado = tienda.comprar(nombre, cabito);
        if (resultado == ResultadoCompra.EXITO) {
            Item item = Catalogo.buscar(nombre);
            historial.registrar("Compraste " + nombre + " por " + item.getPrecio() + " monedas.");
        }
        return resultado;
    }

    /**
     * Avanza el reloj del juego 10 segundos. Si el estado de Cabito cambia,
     * se anota en el historial.
     */
    public void pasarTiempo() {
        Estado antes = cabito.getEstado();
        cabito.pasarTiempo();
        Estado ahora = cabito.getEstado();
        if (ahora != antes) {
            historial.registrar(cabito.getNombre() + " ahora está " + ahora.name().toLowerCase()
                    + ": " + ahora.getDescripcion());
        }
    }

    // ------------------------------------------------------------------
    // Mundos
    // ------------------------------------------------------------------

    /**
     * Viaja a otro mundo si está desbloqueado.
     *
     * @param nombreMundo nombre del mundo
     * @return {@code false} si no existe o el nivel no alcanza
     */
    public boolean viajarA(String nombreMundo) {
        Mundo mundo = buscarMundo(nombreMundo);
        if (mundo == null || !estaDesbloqueado(mundo)) {
            return false;
        }
        mundoActual = mundo.getNombre();
        historial.registrar("Viajaste a " + mundo.getNombre() + ".");
        return true;
    }

    /**
     * Hace la actividad del mundo actual. Si se hizo, cuenta para el
     * progreso del mundo, se anota en el historial y se revisa si Cabito
     * subió de nivel.
     *
     * @return resultado de la actividad
     */
    public ResultadoActividad realizarActividad() {
        Mundo mundo = getMundoActual();
        int nivelAntes = cabito.getNivel();
        ResultadoActividad resultado = mundo.realizarActividad(cabito, random);
        if (resultado.isRealizada()) {
            actividadesPorMundo.merge(mundo.getNombre(), 1, Integer::sum);
            historial.registrar(mundo.getNombre() + ": " + resultado.getMensaje());
            revisarSubidaDeNivel(nivelAntes);
        }
        return resultado;
    }

    /**
     * Busca un mundo por su nombre.
     *
     * @param nombre nombre exacto del mundo
     * @return el mundo, o {@code null} si no existe
     */
    public Mundo buscarMundo(String nombre) {
        for (Mundo mundo : mundos) {
            if (mundo.getNombre().equals(nombre)) {
                return mundo;
            }
        }
        return null;
    }

    /**
     * Indica si Cabito tiene el nivel para entrar a un mundo.
     *
     * @param mundo mundo a revisar
     * @return {@code true} si está desbloqueado
     */
    public boolean estaDesbloqueado(Mundo mundo) {
        return mundo.estaDesbloqueado(cabito.getNivel());
    }

    /**
     * Devuelve cuántas veces se ha hecho la actividad de un mundo.
     *
     * @param nombreMundo nombre del mundo
     * @return veces realizadas (0 si nunca)
     */
    public int getActividadesRealizadas(String nombreMundo) {
        return actividadesPorMundo.getOrDefault(nombreMundo, 0);
    }

    // ------------------------------------------------------------------
    // Minijuegos
    // ------------------------------------------------------------------

    /**
     * Indica si Cabito puede jugar un minijuego: su mundo debe estar
     * desbloqueado y el estado debe permitirlo (no dormido, enfermo ni cansado).
     *
     * @param minijuego minijuego a revisar
     * @return {@code true} si se puede jugar
     */
    public boolean puedeJugar(Minijuego minijuego) {
        Mundo mundo = buscarMundo(minijuego.getNombreMundo());
        return mundo != null && estaDesbloqueado(mundo) && cabito.getEstado().permiteMinijuegos();
    }

    /**
     * Explica por qué no se puede jugar un minijuego.
     *
     * @param minijuego minijuego a revisar
     * @return el motivo, o un texto vacío si sí se puede jugar
     */
    public String getMotivoNoPuedeJugar(Minijuego minijuego) {
        Mundo mundo = buscarMundo(minijuego.getNombreMundo());
        if (mundo != null && !estaDesbloqueado(mundo)) {
            return minijuego.getNombre() + ": " + mundo.getMensajeBloqueado() + ".";
        }
        if (!cabito.getEstado().permiteMinijuegos()) {
            return cabito.getNombre() + " no puede jugar ahora: " + cabito.getEstado().getDescripcion();
        }
        return "";
    }

    /**
     * Crea un juego de memoria nuevo usando el generador aleatorio de la partida.
     *
     * @return el minijuego listo para jugar
     */
    public MinijuegoMemoria crearMinijuegoMemoria() {
        return new MinijuegoMemoria(random);
    }

    /**
     * Crea un juego de preguntas nuevo usando el generador aleatorio de la partida.
     *
     * @return el minijuego listo para jugar
     */
    public MinijuegoPreguntas crearMinijuegoPreguntas() {
        return new MinijuegoPreguntas(random);
    }

    /**
     * Entrega el premio de un minijuego terminado: suma las monedas y la
     * experiencia, lo anota en el historial y revisa si Cabito subió de nivel.
     * Se debe llamar una sola vez por partida de minijuego.
     *
     * @param minijuego minijuego que se jugó
     * @return {@code false} si el minijuego no había terminado
     */
    public boolean entregarPremio(Minijuego minijuego) {
        if (!minijuego.estaTerminado()) {
            return false;
        }
        int nivelAntes = cabito.getNivel();
        cabito.getInventario().agregarMonedas(minijuego.calcularMonedas());
        cabito.ganarExperiencia(minijuego.calcularExperiencia());
        historial.registrar(minijuego.getNombre() + ": " + minijuego.getResumen());
        revisarSubidaDeNivel(nivelAntes);
        return true;
    }

    // ------------------------------------------------------------------
    // Niveles y desbloqueos
    // ------------------------------------------------------------------

    /**
     * Si Cabito subió de nivel desde {@code nivelAntes}, registra el logro
     * y todo lo que se desbloqueó.
     *
     * @param nivelAntes nivel antes de la acción
     */
    private void revisarSubidaDeNivel(int nivelAntes) {
        int nivelAhora = cabito.getNivel();
        if (nivelAhora <= nivelAntes) {
            return;
        }
        historial.registrarLogro("¡" + cabito.getNombre() + " subió al nivel " + nivelAhora + "!");
        for (String desbloqueo : desbloqueosEntre(nivelAntes, nivelAhora)) {
            historial.registrarLogro("Desbloqueaste " + desbloqueo);
        }
    }

    /**
     * Lista lo que se desbloquea al pasar de un nivel a otro: mundos y
     * objetos de la tienda cuyo nivel requerido está en el rango
     * ({@code desde}, {@code hasta}].
     *
     * @param desde nivel anterior (no incluido)
     * @param hasta nivel nuevo (incluido)
     * @return textos como "el mundo Biblioteca" o "el accesorio Mochila UTadeo"
     */
    public List<String> desbloqueosEntre(int desde, int hasta) {
        List<String> desbloqueos = new ArrayList<>();
        for (Mundo mundo : mundos) {
            if (mundo.getNivelRequerido() > desde && mundo.getNivelRequerido() <= hasta) {
                desbloqueos.add("el mundo " + mundo.getNombre());
            }
        }
        for (Item item : Catalogo.getItems()) {
            if (item.getNivelRequerido() > desde && item.getNivelRequerido() <= hasta) {
                desbloqueos.add("el " + item.getTipo().toLowerCase() + " " + item.getNombre());
            }
        }
        return desbloqueos;
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    /**
     * @return la mascota de la partida
     */
    public Cabito getCabito() {
        return cabito;
    }

    /**
     * @return la Tienda Tadeísta
     */
    public Tienda getTienda() {
        return tienda;
    }

    /**
     * @return los tres mundos, en orden de desbloqueo
     */
    public List<Mundo> getMundos() {
        return mundos;
    }

    /**
     * @return el mundo donde está Cabito
     */
    public Mundo getMundoActual() {
        return buscarMundo(mundoActual);
    }

    /**
     * @return historial de acciones y logros
     */
    public Historial getHistorial() {
        return historial;
    }
}
