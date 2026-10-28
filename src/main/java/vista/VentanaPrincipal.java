package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import controlador.JuegoController;
import modelo.Partida;

/**
 * Ventana principal del juego, organizada como en la imagen de referencia:
 * barra superior, escena en el centro, atributos a la derecha y mundos con
 * navegación abajo.
 */
public class VentanaPrincipal extends JFrame {

    private final BarraSuperior barraSuperior;
    private final PanelEscena escena;
    private final PanelAtributos atributos;
    private final PanelInferior inferior;

    /**
     * Crea la ventana (todavía sin mostrarla).
     *
     * @param controlador controlador del juego
     * @param vista       vista que abre los diálogos
     */
    public VentanaPrincipal(JuegoController controlador, VistaSwing vista) {
        super("Cabito: Aventura Tadeísta");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controlador.salir();
            }
        });

        barraSuperior = new BarraSuperior(controlador);
        escena = new PanelEscena(controlador);
        atributos = new PanelAtributos();
        inferior = new PanelInferior(controlador, vista);

        setLayout(new BorderLayout());
        add(barraSuperior, BorderLayout.NORTH);
        add(escena, BorderLayout.CENTER);
        add(atributos, BorderLayout.EAST);
        add(inferior, BorderLayout.SOUTH);

        setSize(1150, 780);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
    }

    /**
     * Actualiza todos los paneles con los datos de la partida.
     *
     * @param partida partida en curso
     */
    public void actualizar(Partida partida) {
        barraSuperior.actualizar(partida);
        escena.actualizar(partida);
        atributos.actualizar(partida.getCabito());
        inferior.actualizar(partida);
    }

    /**
     * Cambia la frase del globo de diálogo de Cabito.
     *
     * @param texto lo que dice Cabito
     */
    public void mostrarDialogo(String texto) {
        escena.setMensaje(texto);
    }

    /**
     * @return la escena central (para pruebas)
     */
    PanelEscena getEscena() {
        return escena;
    }

    /**
     * @return el panel de atributos (para pruebas)
     */
    PanelAtributos getAtributos() {
        return atributos;
    }

    /**
     * @return la barra superior (para pruebas)
     */
    BarraSuperior getBarraSuperior() {
        return barraSuperior;
    }
}
