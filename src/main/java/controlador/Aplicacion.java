package controlador;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import persistencia.GestorPartidas;
import vista.VistaSwing;

/**
 * Punto de entrada del juego: la clase que se ejecuta.
 * <p>
 * Crea la vista, el gestor de partidas y el controlador, los conecta y
 * muestra la pantalla inicial. Todo se hace dentro del hilo de Swing
 * ({@link SwingUtilities#invokeLater}), como recomienda Java.
 */
public final class Aplicacion {

    /** No se crean objetos de esta clase. */
    private Aplicacion() {
    }

    /**
     * Arranca Cabito: Aventura Tadeísta.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            usarAspectoMultiplataforma();
            VistaSwing vista = new VistaSwing();
            JuegoController controlador = new JuegoController(vista, new GestorPartidas());
            vista.setControlador(controlador);
            controlador.iniciar();
        });
    }

    /**
     * Usa el aspecto "Metal" de Java en todos los sistemas operativos. Así
     * los colores de botones y barras se ven igual en Windows, macOS y Linux
     * (el aspecto nativo de macOS, por ejemplo, ignora los colores de fondo).
     */
    private static void usarAspectoMultiplataforma() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            // Si no se puede, se usa el aspecto por defecto; el juego funciona igual.
        }
    }
}
