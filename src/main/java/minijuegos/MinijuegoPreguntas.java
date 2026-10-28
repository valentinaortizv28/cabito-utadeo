package minijuegos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import modelo.mundos.Biblioteca;

/**
 * Minijuego de preguntas de la Biblioteca: 5 preguntas al azar, sin
 * repetir, de un banco de 15. Cada respuesta correcta vale 5 monedas.
 */
public class MinijuegoPreguntas implements Minijuego {

    /** Cantidad de preguntas por partida. */
    public static final int PREGUNTAS_POR_PARTIDA = 5;
    /** Monedas por cada respuesta correcta. */
    public static final int MONEDAS_POR_ACIERTO = 5;
    /**
     * Experiencia al terminar. El enunciado no la define, así que por ahora
     * es 0; para dar XP basta con cambiar esta constante.
     */
    public static final int XP = 0;

    private final List<Pregunta> preguntas;
    private int actual;
    private int correctas;

    /**
     * Crea un minijuego con preguntas elegidas al azar.
     */
    public MinijuegoPreguntas() {
        this(new Random());
    }

    /**
     * Crea un minijuego con un generador aleatorio dado (para pruebas).
     *
     * @param random generador usado para elegir las preguntas
     */
    public MinijuegoPreguntas(Random random) {
        this(BancoPreguntas.getPreguntas(), random);
    }

    /**
     * Crea un minijuego eligiendo de un banco dado.
     *
     * @param banco  preguntas disponibles (al menos 5)
     * @param random generador usado para elegir las preguntas
     */
    public MinijuegoPreguntas(List<Pregunta> banco, Random random) {
        this.preguntas = elegirAlAzar(banco, PREGUNTAS_POR_PARTIDA, random);
    }

    /**
     * Elige {@code cantidad} preguntas distintas con Fisher–Yates parcial:
     * para cada posición {@code i} desde el inicio, se intercambia con una
     * posición al azar entre {@code i} y el final. Al terminar, las primeras
     * {@code cantidad} posiciones son una muestra al azar sin repetir.
     *
     * @param banco    preguntas disponibles
     * @param cantidad cuántas elegir
     * @param random   generador aleatorio
     * @return lista nueva con las preguntas elegidas
     */
    private static List<Pregunta> elegirAlAzar(List<Pregunta> banco, int cantidad, Random random) {
        if (banco.size() < cantidad) {
            throw new IllegalArgumentException("El banco necesita al menos " + cantidad + " preguntas");
        }
        List<Pregunta> copia = new ArrayList<>(banco);
        for (int i = 0; i < cantidad; i++) {
            int j = i + random.nextInt(copia.size() - i);
            Collections.swap(copia, i, j);
        }
        return new ArrayList<>(copia.subList(0, cantidad));
    }

    /**
     * Responde la pregunta actual y pasa a la siguiente.
     *
     * @param indiceOpcion opción elegida (desde 0)
     * @return {@code true} si la respuesta fue correcta; {@code false} si fue
     *         incorrecta o el juego ya había terminado
     */
    public boolean responder(int indiceOpcion) {
        if (estaTerminado()) {
            return false;
        }
        boolean acierto = preguntas.get(actual).esCorrecta(indiceOpcion);
        if (acierto) {
            correctas++;
        }
        actual++;
        return acierto;
    }

    /**
     * @return la pregunta que toca responder, o {@code null} si ya terminó
     */
    public Pregunta getPreguntaActual() {
        return estaTerminado() ? null : preguntas.get(actual);
    }

    /**
     * @return número de la pregunta actual, desde 1 (para mostrar "Pregunta 2 de 5")
     */
    public int getNumeroPregunta() {
        return Math.min(actual + 1, preguntas.size());
    }

    /**
     * @return respuestas correctas hasta ahora
     */
    public int getCorrectas() {
        return correctas;
    }

    /**
     * @return las preguntas de esta partida (solo lectura)
     */
    public List<Pregunta> getPreguntas() {
        return Collections.unmodifiableList(preguntas);
    }

    // ------------------------------------------------------------------
    // Interfaz Minijuego
    // ------------------------------------------------------------------

    /**
     * @return {@code "Preguntas Tadeístas"}
     */
    @Override
    public String getNombre() {
        return "Preguntas Tadeístas";
    }

    /**
     * @return cómo se juega
     */
    @Override
    public String getInstrucciones() {
        return "Responde " + PREGUNTAS_POR_PARTIDA + " preguntas sobre la UTadeo. "
                + "Cada respuesta correcta vale " + MONEDAS_POR_ACIERTO + " monedas.";
    }

    /**
     * @return nombre de la Biblioteca
     */
    @Override
    public String getNombreMundo() {
        return Biblioteca.NOMBRE;
    }

    /**
     * @return {@code true} cuando se respondieron las 5 preguntas
     */
    @Override
    public boolean estaTerminado() {
        return actual >= preguntas.size();
    }

    /**
     * @return 5 monedas por respuesta correcta; 0 si no ha terminado
     */
    @Override
    public int calcularMonedas() {
        return estaTerminado() ? correctas * MONEDAS_POR_ACIERTO : 0;
    }

    /**
     * @return la experiencia definida en {@link #XP}; 0 si no ha terminado
     */
    @Override
    public int calcularExperiencia() {
        return estaTerminado() ? XP : 0;
    }

    /**
     * @return resumen con aciertos y monedas
     */
    @Override
    public String getResumen() {
        return "Respondiste bien " + correctas + " de " + preguntas.size() + " preguntas. Ganaste "
                + calcularMonedas() + " monedas.";
    }
}
