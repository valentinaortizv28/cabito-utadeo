package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import controlador.JuegoController;
import modelo.Partida;
import modelo.mundos.Mundo;

/**
 * Panel inferior: una tarjeta por mundo (los bloqueados muestran
 * "Nivel X requerido") y los botones de navegación: Mundos, Tienda
 * Tadeísta, Inventario, Minijuegos y Logros.
 */
public class PanelInferior extends JPanel {

    private final JuegoController controlador;
    private final JPanel panelTarjetas;

    /**
     * Crea el panel.
     *
     * @param controlador controlador que recibe los viajes entre mundos
     * @param vista       vista que abre los diálogos de navegación
     */
    public PanelInferior(JuegoController controlador, VistaSwing vista) {
        this.controlador = controlador;
        setLayout(new BorderLayout(0, 10));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Estilo.TEAL_CLARO),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        panelTarjetas = new JPanel(new GridLayout(1, 0, 12, 0));
        panelTarjetas.setOpaque(false);
        add(panelTarjetas, BorderLayout.CENTER);

        JPanel navegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navegacion.setOpaque(false);
        navegacion.add(Estilo.crearBoton("Mundos", vista::abrirMundos));
        navegacion.add(Estilo.crearBoton("Tienda Tadeísta", vista::abrirTienda));
        navegacion.add(Estilo.crearBoton("Inventario", vista::abrirInventario));
        navegacion.add(Estilo.crearBoton("Minijuegos", vista::abrirMinijuegos));
        navegacion.add(Estilo.crearBoton("Logros", vista::abrirLogros));
        add(navegacion, BorderLayout.SOUTH);
    }

    /**
     * Vuelve a crear las tarjetas de los mundos según el nivel y el mundo actual.
     *
     * @param partida partida en curso
     */
    public void actualizar(Partida partida) {
        panelTarjetas.removeAll();
        for (Mundo mundo : partida.getMundos()) {
            panelTarjetas.add(crearTarjeta(partida, mundo));
        }
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    /**
     * Crea la tarjeta de un mundo. Al hacer clic se viaja a ese mundo.
     *
     * @param partida partida en curso
     * @param mundo   mundo de la tarjeta
     * @return botón con forma de tarjeta
     */
    private JButton crearTarjeta(Partida partida, Mundo mundo) {
        boolean desbloqueado = partida.estaDesbloqueado(mundo);
        boolean actual = mundo == partida.getMundoActual();
        String detalle;
        if (actual) {
            detalle = "Estás aquí";
        } else if (desbloqueado) {
            detalle = "Actividad: " + mundo.getNombreActividad();
        } else {
            detalle = mundo.getMensajeBloqueado();
        }
        JButton tarjeta = new JButton("<html><center><b>" + mundo.getNombre() + "</b><br>"
                + mundo.getDescripcion() + "<br><i>" + detalle + "</i></center></html>");
        tarjeta.setFont(Estilo.FUENTE_PEQUENA);
        tarjeta.setFocusPainted(false);
        tarjeta.setOpaque(true);
        tarjeta.setBackground(desbloqueado ? (actual ? Estilo.TEAL : Estilo.TEAL_CLARO) : Estilo.BLOQUEADO);
        tarjeta.setForeground(actual ? Color.WHITE : Estilo.AZUL_OSCURO);
        tarjeta.setBorder(BorderFactory.createLineBorder(actual ? Estilo.AZUL_OSCURO : Color.WHITE, actual ? 3 : 1));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.addActionListener(e -> controlador.viajarA(mundo.getNombre()));
        return tarjeta;
    }
}
