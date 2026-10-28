package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Historial de acciones y logros de la partida.
 * <p>
 * Usa dos {@link LinkedList}:
 * <ul>
 *   <li>{@code acciones}: todo lo que pasa. Funciona como una cola con tope:
 *       lo nuevo entra al final ({@code addLast}) y, si se pasa de
 *       {@link #MAXIMO_ACCIONES}, sale lo más viejo ({@code removeFirst}).
 *       Ambas operaciones son O(1) en una lista enlazada.</li>
 *   <li>{@code logros}: subidas de nivel y desbloqueos. Son pocos y no se
 *       borran nunca.</li>
 * </ul>
 */
public class Historial {

    /** Cantidad máxima de acciones que se guardan. */
    public static final int MAXIMO_ACCIONES = 100;

    private final LinkedList<String> acciones;
    private final LinkedList<String> logros;

    /**
     * Crea un historial vacío.
     */
    public Historial() {
        acciones = new LinkedList<>();
        logros = new LinkedList<>();
    }

    /**
     * Agrega una acción al final. Si se supera el máximo, se borra la más
     * antigua.
     *
     * @param texto descripción de la acción
     */
    public void registrar(String texto) {
        acciones.addLast(texto);
        if (acciones.size() > MAXIMO_ACCIONES) {
            acciones.removeFirst();
        }
    }

    /**
     * Agrega un logro. También queda en las acciones para verlo en orden.
     *
     * @param texto descripción del logro
     */
    public void registrarLogro(String texto) {
        logros.addLast(texto);
        registrar("¡Logro! " + texto);
    }

    /**
     * Devuelve todas las acciones guardadas, de la más antigua a la más nueva.
     *
     * @return lista de solo lectura
     */
    public List<String> getAcciones() {
        return Collections.unmodifiableList(acciones);
    }

    /**
     * Devuelve todos los logros, del más antiguo al más nuevo.
     *
     * @return lista de solo lectura
     */
    public List<String> getLogros() {
        return Collections.unmodifiableList(logros);
    }

    /**
     * Devuelve las últimas acciones, empezando por la más reciente (útil
     * para mostrarlas en pantalla).
     *
     * @param cantidad cuántas acciones devolver como máximo
     * @return lista nueva con las acciones más recientes primero
     */
    public List<String> getUltimasAcciones(int cantidad) {
        List<String> ultimas = new ArrayList<>();
        Iterator<String> iterador = acciones.descendingIterator();
        while (iterador.hasNext() && ultimas.size() < cantidad) {
            ultimas.add(iterador.next());
        }
        return ultimas;
    }
}
