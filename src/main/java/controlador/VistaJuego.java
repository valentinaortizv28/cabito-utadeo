package controlador;

import java.util.List;

import minijuegos.Minijuego;
import modelo.Partida;

/**
 * Lo que el controlador necesita de la interfaz gráfica.
 * <p>
 * El controlador solo conoce esta interfaz, no las ventanas de Swing. Así
 * se puede cambiar la vista sin tocar el controlador, y en las pruebas se
 * usa una vista falsa que no abre ventanas.
 */
public interface VistaJuego {

    /** Muestra la pantalla inicial (Nueva partida / Cargar partida). */
    void mostrarInicio();

    /**
     * Cierra la pantalla inicial (si estaba abierta) y muestra la ventana
     * principal con la partida.
     *
     * @param partida partida que se va a jugar
     */
    void mostrarJuego(Partida partida);

    /**
     * Vuelve a dibujar todo con los datos actuales de la partida.
     *
     * @param partida partida en curso
     */
    void actualizar(Partida partida);

    /**
     * Muestra una frase en el globo de diálogo de Cabito.
     *
     * @param texto lo que dice Cabito
     */
    void mostrarDialogo(String texto);

    /**
     * Muestra un mensaje informativo en una ventana emergente.
     *
     * @param titulo título de la ventana
     * @param texto  mensaje
     */
    void mostrarAviso(String titulo, String texto);

    /**
     * Muestra un mensaje de error en una ventana emergente.
     *
     * @param texto mensaje para el usuario
     */
    void mostrarError(String texto);

    /**
     * Pide el nombre para una partida nueva.
     *
     * @return el texto escrito, o {@code null} si el usuario canceló
     */
    String pedirNombre();

    /**
     * Pide al usuario que elija una opción de una lista.
     *
     * @param titulo   título de la ventana
     * @param mensaje  pregunta
     * @param opciones opciones disponibles
     * @return la opción elegida, o {@code null} si canceló
     */
    String elegir(String titulo, String mensaje, List<String> opciones);

    /**
     * Pide confirmación con botones Sí / No.
     *
     * @param titulo  título de la ventana
     * @param mensaje pregunta
     * @return {@code true} si el usuario respondió que sí
     */
    boolean confirmar(String titulo, String mensaje);

    /**
     * Pregunta si se quiere guardar antes de salir.
     *
     * @return la opción elegida
     */
    OpcionSalir preguntarGuardarAntesDeSalir();

    /**
     * Abre la ventana de un minijuego. Al terminarlo, la vista debe llamar a
     * {@link JuegoController#minijuegoTerminado(Minijuego)}.
     *
     * @param minijuego minijuego a jugar
     */
    void abrirMinijuego(Minijuego minijuego);

    /** Cierra todas las ventanas y termina el programa. */
    void cerrar();
}
