package vista;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import minijuegos.Minijuego;
import minijuegos.MinijuegoMemoria;
import minijuegos.MinijuegoPreguntas;

/**
 * Ventana de prueba para jugar los dos minijuegos sin el resto del juego.
 * Sirve para revisar los paneles mientras se construye la interfaz completa
 * (etapa 6); después se puede borrar.
 */
public final class DemoMinijuegos {

    /** No se crean objetos de esta clase. */
    private DemoMinijuegos() {
    }

    /**
     * Abre una ventana con una pestaña por minijuego.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame ventana = new JFrame("Cabito: minijuegos (demo)");
            JTabbedPane pestanas = new JTabbedPane();
            pestanas.setFont(Estilo.FUENTE);
            pestanas.addTab("Memoria", new PanelMemoria(new MinijuegoMemoria(),
                    juego -> mostrarResultado(ventana, juego)));
            pestanas.addTab("Preguntas", new PanelPreguntas(new MinijuegoPreguntas(),
                    juego -> mostrarResultado(ventana, juego)));
            ventana.add(pestanas);
            ventana.setMinimumSize(new Dimension(640, 600));
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }

    /**
     * Muestra el resumen de un minijuego terminado.
     *
     * @param ventana ventana padre del mensaje
     * @param juego   minijuego que terminó
     */
    private static void mostrarResultado(JFrame ventana, Minijuego juego) {
        JOptionPane.showMessageDialog(ventana, juego.getResumen(), juego.getNombre(),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
