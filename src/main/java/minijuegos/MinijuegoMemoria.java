package minijuegos;

import java.util.Random;

import modelo.mundos.Modulo20;

/**
 * Juego de memoria del Módulo 20: un tablero {@code Carta[][]} de 4x4 con
 * 8 parejas de íconos tadeístas, barajado con el algoritmo de Fisher–Yates.
 * <p>
 * Cada intento consiste en voltear dos cartas. Si son pareja quedan boca
 * arriba; si no, se vuelven a ocultar. Entre menos intentos, más monedas.
 */
public class MinijuegoMemoria implements Minijuego {

    /** Filas del tablero. */
    public static final int FILAS = 4;
    /** Columnas del tablero. */
    public static final int COLUMNAS = 4;
    /** Íconos de las 8 parejas. */
    public static final String[] ICONOS = {
        "Cabito", "Tinto", "Libro", "Balón", "Gorra", "Brújula", "Birrete", "Lápiz"
    };

    private static final int INTENTOS_EXCELENTE = 12;
    private static final int INTENTOS_BUENO = 18;
    private static final int MONEDAS_EXCELENTE = 50;
    private static final int MONEDAS_BUENO = 30;
    private static final int MONEDAS_BASE = 15;
    private static final int XP = 40;

    private final Carta[][] tablero;
    private final Random random;
    private int intentos;
    private int parejasEncontradas;
    private Carta primera;
    private Carta segunda;

    /**
     * Crea un juego de memoria con un tablero barajado al azar.
     */
    public MinijuegoMemoria() {
        this(new Random());
    }

    /**
     * Crea un juego de memoria con un generador aleatorio dado (para pruebas).
     *
     * @param random generador usado para barajar
     */
    public MinijuegoMemoria(Random random) {
        this.random = random;
        this.tablero = new Carta[FILAS][COLUMNAS];
        llenarTablero();
        barajar();
    }

    /**
     * Pone las 16 cartas en orden: cada ícono en dos casillas seguidas,
     * recorriendo el tablero fila por fila.
     */
    private void llenarTablero() {
        for (int i = 0; i < FILAS * COLUMNAS; i++) {
            tablero[i / COLUMNAS][i % COLUMNAS] = new Carta(ICONOS[i / 2]);
        }
    }

    /**
     * Baraja el tablero con Fisher–Yates.
     * <p>
     * Se imagina el tablero como una lista de 16 casillas: la casilla
     * {@code i} es la fila {@code i / 4}, columna {@code i % 4}. Se recorre
     * desde la última casilla hacia la primera y cada una se intercambia con
     * una casilla al azar entre la 0 y ella misma. Así cada orden posible
     * tiene la misma probabilidad y solo se hacen 15 intercambios (O(n)).
     */
    private void barajar() {
        for (int i = FILAS * COLUMNAS - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Carta temporal = tablero[i / COLUMNAS][i % COLUMNAS];
            tablero[i / COLUMNAS][i % COLUMNAS] = tablero[j / COLUMNAS][j % COLUMNAS];
            tablero[j / COLUMNAS][j % COLUMNAS] = temporal;
        }
    }

    /**
     * Voltea la carta de una casilla.
     * <p>
     * Si quedaron dos cartas boca arriba que no eran pareja, se ocultan
     * antes de voltear la nueva (por si la vista no lo hizo).
     *
     * @param fila    fila de la carta (0–3)
     * @param columna columna de la carta (0–3)
     * @return qué pasó con la jugada
     */
    public ResultadoJugada seleccionar(int fila, int columna) {
        if (estaTerminado() || fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) {
            return ResultadoJugada.INVALIDA;
        }
        if (hayCartasPorOcultar()) {
            ocultarNoEmparejadas();
        }
        Carta carta = tablero[fila][columna];
        if (carta.isVisible()) {
            return ResultadoJugada.INVALIDA;
        }
        carta.voltear();

        if (primera == null) {
            primera = carta;
            return ResultadoJugada.PRIMERA_CARTA;
        }

        intentos++;
        if (primera.esParejaDe(carta)) {
            primera.emparejar();
            carta.emparejar();
            parejasEncontradas++;
            primera = null;
            return ResultadoJugada.PAREJA;
        }
        segunda = carta;
        return ResultadoJugada.NO_PAREJA;
    }

    /**
     * @return {@code true} si hay dos cartas boca arriba que no son pareja
     */
    public boolean hayCartasPorOcultar() {
        return segunda != null;
    }

    /**
     * Oculta las dos cartas del último intento fallido. La vista lo llama
     * después de dejarlas ver un momento.
     */
    public void ocultarNoEmparejadas() {
        if (segunda != null) {
            primera.ocultar();
            segunda.ocultar();
            primera = null;
            segunda = null;
        }
    }

    /**
     * Devuelve la carta de una casilla (para dibujarla).
     *
     * @param fila    fila (0–3)
     * @param columna columna (0–3)
     * @return la carta
     */
    public Carta getCarta(int fila, int columna) {
        return tablero[fila][columna];
    }

    /**
     * @return intentos hechos (cada par de cartas volteadas cuenta uno)
     */
    public int getIntentos() {
        return intentos;
    }

    /**
     * @return parejas encontradas hasta ahora (0–8)
     */
    public int getParejasEncontradas() {
        return parejasEncontradas;
    }

    // ------------------------------------------------------------------
    // Interfaz Minijuego
    // ------------------------------------------------------------------

    /**
     * @return {@code "Memoria Tadeísta"}
     */
    @Override
    public String getNombre() {
        return "Memoria Tadeísta";
    }

    /**
     * @return cómo se juega
     */
    @Override
    public String getInstrucciones() {
        return "Voltea dos cartas por turno y encuentra las 8 parejas. "
                + "Con 12 intentos o menos ganas 50 monedas; con 18 o menos, 30; si no, 15.";
    }

    /**
     * @return nombre del Módulo 20
     */
    @Override
    public String getNombreMundo() {
        return Modulo20.NOMBRE;
    }

    /**
     * @return {@code true} cuando se encontraron las 8 parejas
     */
    @Override
    public boolean estaTerminado() {
        return parejasEncontradas == ICONOS.length;
    }

    /**
     * Monedas según los intentos: ≤12 → 50, ≤18 → 30, más → 15.
     *
     * @return monedas ganadas; 0 si no ha terminado
     */
    @Override
    public int calcularMonedas() {
        if (!estaTerminado()) {
            return 0;
        }
        if (intentos <= INTENTOS_EXCELENTE) {
            return MONEDAS_EXCELENTE;
        } else if (intentos <= INTENTOS_BUENO) {
            return MONEDAS_BUENO;
        } else {
            return MONEDAS_BASE;
        }
    }

    /**
     * @return siempre 40 XP al terminar; 0 si no ha terminado
     */
    @Override
    public int calcularExperiencia() {
        return estaTerminado() ? XP : 0;
    }

    /**
     * @return resumen con intentos, monedas y experiencia
     */
    @Override
    public String getResumen() {
        return "¡Encontraste las " + ICONOS.length + " parejas en " + intentos + " intentos! Ganaste "
                + calcularMonedas() + " monedas y " + calcularExperiencia() + " XP.";
    }
}
