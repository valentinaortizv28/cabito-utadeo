package minijuegos;

/**
 * Lo que pasa al seleccionar una carta en el juego de memoria.
 */
public enum ResultadoJugada {

    /** La carta no se puede seleccionar (ya está boca arriba, fuera del tablero o el juego terminó). */
    INVALIDA,
    /** Se volteó la primera carta del intento. */
    PRIMERA_CARTA,
    /** La segunda carta forma pareja con la primera. */
    PAREJA,
    /** La segunda carta no forma pareja; las dos se ocultarán. */
    NO_PAREJA
}
