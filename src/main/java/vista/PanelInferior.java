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
import modelo.items.Tienda;
import modelo.mundos.Mundo;

/**
 * Panel inferior: una tarjeta por lugar (los tres mundos, donde los
 * bloqueados muestran "Nivel X requerido", y la Tienda Tadeísta) y los
 * botones de navegación: Mundos, Tienda Tadeísta, Inventario, Minijuegos y Logros.
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
        navegacion.add(Estilo.crearBoton(Tienda.NOMBRE, controlador::irATienda));
        navegacion.add(Estilo.crearBoton("Inventario", vista::abrirInventario));
        navegacion.add(Estilo.crearBoton("Minijuegos", vista::abrirMinijuegos));
        navegacion.add(Estilo.crearBoton("Logros", vista::abrirLogros));
        add(navegacion, BorderLayout.SOUTH);
    }

    /**
     * Vuelve a crear las tarjetas de los lugares (los tres mundos y la
     * Tienda Tadeísta) según el nivel y dónde está Cabito.
     *
     * @param partida partida en curso
     */
    public void actualizar(Partida partida) {
        panelTarjetas.removeAll();
        boolean enTienda = partida.isEnTienda();
        for (Mundo mundo : partida.getMundos()) {
            boolean desbloqueado = partida.estaDesbloqueado(mundo);
            boolean esDeDondeViene = mundo == partida.getMundoActual();
            String detalle;
            if (esDeDondeViene) {
                detalle = enTienda ? "Volver aquí" : "Estás aquí";
            } else if (desbloqueado) {
                detalle = "Actividad: " + mundo.getNombreActividad();
            } else {
                detalle = mundo.getMensajeBloqueado();
            }
            panelTarjetas.add(crearTarjeta(mundo.getNombre(), mundo.getDescripcion(), detalle, desbloqueado,
                    esDeDondeViene && !enTienda, () -> controlador.viajarA(mundo.getNombre())));
        }
        panelTarjetas.add(crearTarjeta(Tienda.NOMBRE, Tienda.DESCRIPCION,
                enTienda ? "Estás aquí" : "Sin nivel requerido", true, enTienda, controlador::irATienda));
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    /**
     * @return cantidad de tarjetas de lugares (para pruebas)
     */
    int getCantidadTarjetas() {
        return panelTarjetas.getComponentCount();
    }

    /**
     * Crea la tarjeta de un lugar. Al hacer clic, Cabito va a ese lugar.
     *
     * @param nombre       nombre del lugar
     * @param descripcion  descripción corta
     * @param detalle      texto en cursiva (por ejemplo "Estás aquí" o "Nivel 2 requerido")
     * @param desbloqueado si se puede ir (si no, se pinta gris)
     * @param actual       si Cabito está ahí (se resalta)
     * @param accion       lo que pasa al hacer clic
     * @return botón con forma de tarjeta
     */
    private JButton crearTarjeta(String nombre, String descripcion, String detalle, boolean desbloqueado,
                                 boolean actual, Runnable accion) {
        JButton tarjeta = new JButton("<html><center><b>" + Estilo.escaparHtml(nombre) + "</b><br>"
                + descripcion + "<br><i>" + detalle + "</i></center></html>");
        tarjeta.setFont(Estilo.FUENTE_PEQUENA);
        tarjeta.setFocusPainted(false);
        tarjeta.setOpaque(true);
        tarjeta.setBackground(desbloqueado ? (actual ? Estilo.TEAL : Estilo.TEAL_CLARO) : Estilo.BLOQUEADO);
        tarjeta.setForeground(actual ? Color.WHITE : Estilo.AZUL_OSCURO);
        tarjeta.setBorder(BorderFactory.createLineBorder(actual ? Estilo.AZUL_OSCURO : Color.WHITE, actual ? 3 : 1));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.addActionListener(e -> accion.run());
        return tarjeta;
    }
}
