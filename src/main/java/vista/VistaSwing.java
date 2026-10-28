package vista;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controlador.JuegoController;
import controlador.OpcionSalir;
import controlador.VistaJuego;
import minijuegos.Minijuego;
import minijuegos.MinijuegoMemoria;
import minijuegos.MinijuegoPreguntas;
import modelo.Partida;

/**
 * Implementación de {@link VistaJuego} con Swing.
 * <p>
 * Maneja las dos ventanas (pantalla inicial y ventana principal), los
 * mensajes emergentes ({@link JOptionPane}) y abre los diálogos de tienda,
 * inventario, mundos, minijuegos y logros.
 */
public class VistaSwing implements VistaJuego {

    private JuegoController controlador;
    private PantallaInicio pantallaInicio;
    private VentanaPrincipal ventanaPrincipal;

    /**
     * Conecta la vista con su controlador. Se llama una vez al arrancar,
     * antes de mostrar cualquier ventana.
     *
     * @param controlador controlador del juego
     */
    public void setControlador(JuegoController controlador) {
        this.controlador = controlador;
    }

    // ------------------------------------------------------------------
    // VistaJuego
    // ------------------------------------------------------------------

    @Override
    public void mostrarInicio() {
        pantallaInicio = new PantallaInicio(controlador);
        pantallaInicio.setVisible(true);
    }

    @Override
    public void mostrarJuego(Partida partida) {
        if (pantallaInicio != null) {
            pantallaInicio.dispose();
            pantallaInicio = null;
        }
        if (ventanaPrincipal == null) {
            ventanaPrincipal = new VentanaPrincipal(controlador, this);
        }
        ventanaPrincipal.actualizar(partida);
        ventanaPrincipal.mostrarDialogo(partida.getCabito().getEstado().getDescripcion());
        ventanaPrincipal.setVisible(true);
    }

    @Override
    public void actualizar(Partida partida) {
        if (ventanaPrincipal != null) {
            ventanaPrincipal.actualizar(partida);
        }
    }

    @Override
    public void mostrarDialogo(String texto) {
        if (ventanaPrincipal != null) {
            ventanaPrincipal.mostrarDialogo(texto);
        }
    }

    @Override
    public void mostrarAviso(String titulo, String texto) {
        JOptionPane.showMessageDialog(ventanaActiva(), texto, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(ventanaActiva(), texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public String pedirNombre() {
        return JOptionPane.showInputDialog(ventanaActiva(), "¿Cómo se llamará tu Cabito?", "Nueva partida",
                JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public String elegir(String titulo, String mensaje, List<String> opciones) {
        Object elegida = JOptionPane.showInputDialog(ventanaActiva(), mensaje, titulo,
                JOptionPane.QUESTION_MESSAGE, null, opciones.toArray(), opciones.get(0));
        return elegida == null ? null : elegida.toString();
    }

    @Override
    public boolean confirmar(String titulo, String mensaje) {
        return JOptionPane.showConfirmDialog(ventanaActiva(), mensaje, titulo, JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    @Override
    public OpcionSalir preguntarGuardarAntesDeSalir() {
        String[] opciones = {"Guardar y salir", "Salir sin guardar", "Cancelar"};
        int respuesta = JOptionPane.showOptionDialog(ventanaActiva(), "¿Quieres guardar la partida antes de salir?",
                "Salir", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (respuesta == 0) {
            return OpcionSalir.GUARDAR;
        } else if (respuesta == 1) {
            return OpcionSalir.NO_GUARDAR;
        } else {
            return OpcionSalir.CANCELAR;
        }
    }

    @Override
    public void abrirMinijuego(Minijuego minijuego) {
        JPanel panel;
        if (minijuego instanceof MinijuegoMemoria) {
            panel = new PanelMemoria((MinijuegoMemoria) minijuego, controlador::minijuegoTerminado);
        } else if (minijuego instanceof MinijuegoPreguntas) {
            panel = new PanelPreguntas((MinijuegoPreguntas) minijuego, controlador::minijuegoTerminado);
        } else {
            return;
        }
        JDialog dialogo = new JDialog(ventanaActiva(), minijuego.getNombre(), JDialog.ModalityType.APPLICATION_MODAL);
        dialogo.setContentPane(panel);
        dialogo.setSize(680, 640);
        dialogo.setLocationRelativeTo(ventanaActiva());
        dialogo.setVisible(true);
    }

    @Override
    public void cerrar() {
        if (ventanaPrincipal != null) {
            ventanaPrincipal.dispose();
        }
        if (pantallaInicio != null) {
            pantallaInicio.dispose();
        }
        System.exit(0);
    }

    // ------------------------------------------------------------------
    // Navegación (botones del panel inferior)
    // ------------------------------------------------------------------

    /** Abre la lista de mundos. */
    public void abrirMundos() {
        new DialogoMundos(ventanaActiva(), controlador).setVisible(true);
    }

    /** Abre la Tienda Tadeísta. */
    public void abrirTienda() {
        new DialogoTienda(ventanaActiva(), controlador).setVisible(true);
    }

    /** Abre el inventario. */
    public void abrirInventario() {
        new DialogoInventario(ventanaActiva(), controlador).setVisible(true);
    }

    /** Abre la lista de minijuegos. */
    public void abrirMinijuegos() {
        new DialogoMinijuegos(ventanaActiva(), controlador).setVisible(true);
    }

    /** Abre los logros y el historial. */
    public void abrirLogros() {
        new DialogoLogros(ventanaActiva(), controlador.getPartida()).setVisible(true);
    }

    /**
     * Devuelve la ventana que tiene el foco (por ejemplo, un minijuego
     * abierto) para que los mensajes aparezcan encima de ella.
     *
     * @return la ventana activa, o la principal / inicial si no hay ninguna
     */
    private Window ventanaActiva() {
        Window activa = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        if (activa != null) {
            return activa;
        }
        return ventanaPrincipal != null ? ventanaPrincipal : pantallaInicio;
    }
}
