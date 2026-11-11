package vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import modelo.items.Item;

/**
 * Casilla del inventario: el ícono del objeto, su nombre, la cantidad en
 * una etiqueta redonda en la esquina superior derecha (por ejemplo "x3") y
 * un botón para usarlo.
 * <p>
 * El fondo redondeado y la etiqueta de cantidad se pintan a mano: el fondo
 * en {@link #paintComponent} (debajo de todo) y la etiqueta en
 * {@link #paintChildren} (encima del ícono).
 */
public class CasillaItem extends JPanel {

    /** Tamaño del ícono en píxeles. */
    public static final int TAMANO_ICONO = 72;

    private static final int ANCHO = 150;
    private static final int ALTO = 196;
    private static final int RADIO = 18;
    private static final int ANCHO_TEXTO = 110;

    private final String textoCantidad;
    private final boolean equipado;
    private final JButton boton;

    /**
     * Crea la casilla.
     *
     * @param item        objeto que se muestra
     * @param cantidad    unidades que tiene el jugador
     * @param equipado    si es un accesorio que está puesto
     * @param textoBoton  texto del botón (por ejemplo "Comer" o "Equipar")
     * @param habilitado  si el botón se puede pulsar
     * @param accion      lo que hace el botón
     */
    public CasillaItem(Item item, int cantidad, boolean equipado, String textoBoton, boolean habilitado,
                       Runnable accion) {
        this.textoCantidad = "x" + cantidad;
        this.equipado = equipado;
        setLayout(new BorderLayout(0, 4));
        setOpaque(false);
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBorder(BorderFactory.createEmptyBorder(14, 8, 10, 8));
        setToolTipText(item.getTipo() + ": " + item.getDescripcion());

        JLabel icono = new JLabel(IconosItems.icono(item.getNombre(), TAMANO_ICONO));
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        add(icono, BorderLayout.NORTH);

        JLabel nombre = new JLabel("<html><div style='text-align:center;width:" + ANCHO_TEXTO + "px'>"
                + Estilo.escaparHtml(item.getNombre()) + "</div></html>", SwingConstants.CENTER);
        nombre.setFont(Estilo.FUENTE_PEQUENA.deriveFont(Font.BOLD));
        nombre.setForeground(Estilo.AZUL_OSCURO);
        add(nombre, BorderLayout.CENTER);

        boton = Estilo.crearBoton(textoBoton, accion);
        boton.setFont(Estilo.FUENTE_PEQUENA.deriveFont(Font.BOLD));
        boton.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        boton.setEnabled(habilitado);
        if (!habilitado) {
            boton.setBackground(Estilo.BLOQUEADO);
        }
        add(boton, BorderLayout.SOUTH);
    }

    /**
     * Pinta el fondo blanco redondeado. Si el accesorio está equipado, el
     * borde es teal y más grueso.
     *
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, RADIO, RADIO);
        g2.setColor(equipado ? Estilo.TEAL : Estilo.TEAL_CLARO);
        g2.setStroke(new BasicStroke(equipado ? 3f : 2f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, RADIO, RADIO);
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Pinta los componentes (ícono, nombre, botón) y encima la etiqueta de
     * cantidad en la esquina superior derecha.
     *
     * @param g contexto gráfico
     */
    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(Estilo.FUENTE_NEGRITA);
        FontMetrics metricas = g2.getFontMetrics();
        int ancho = Math.max(30, metricas.stringWidth(textoCantidad) + 14);
        int alto = 26;
        int x = getWidth() - ancho - 8;
        int y = 8;
        g2.setColor(Estilo.AZUL_OSCURO);
        g2.fillRoundRect(x, y, ancho, alto, alto, alto);
        g2.setColor(Color.WHITE);
        g2.drawString(textoCantidad, x + (ancho - metricas.stringWidth(textoCantidad)) / 2,
                y + (alto + metricas.getAscent() - metricas.getDescent()) / 2);
        g2.dispose();
    }

    /**
     * @return el texto de la cantidad, por ejemplo {@code "x3"}
     */
    public String getTextoCantidad() {
        return textoCantidad;
    }

    /**
     * @return el botón de la casilla (para pruebas)
     */
    JButton getBoton() {
        return boton;
    }
}
