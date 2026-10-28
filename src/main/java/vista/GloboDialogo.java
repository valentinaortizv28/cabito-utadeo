package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Globo de diálogo (como en los cómics) donde Cabito dice frases.
 */
public class GloboDialogo extends JLabel {

    private static final int ANCHO_TEXTO = 330;
    private static final int ALTO_COLA = 12;

    /**
     * Crea un globo vacío.
     */
    public GloboDialogo() {
        setFont(Estilo.FUENTE);
        setForeground(Estilo.AZUL_OSCURO);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(BorderFactory.createEmptyBorder(12, 18, 12 + ALTO_COLA, 18));
        setOpaque(false);
        setMensaje(" ");
    }

    /**
     * Cambia el texto del globo.
     *
     * @param mensaje lo que dice Cabito (texto plano)
     */
    public void setMensaje(String mensaje) {
        setText(Estilo.textoAjustado(mensaje, ANCHO_TEXTO));
    }

    /**
     * Pinta el globo blanco con borde y su "cola" hacia abajo, y encima el texto.
     *
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int ancho = getWidth() - 3;
        int alto = getHeight() - ALTO_COLA - 2;
        Polygon cola = new Polygon(
                new int[] {ancho / 2 - 12, ancho / 2 + 12, ancho / 2},
                new int[] {alto - 1, alto - 1, alto + ALTO_COLA}, 3);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(1, 1, ancho, alto, 26, 26);
        g2.fillPolygon(cola);
        g2.setColor(Estilo.TEAL);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, ancho, alto, 26, 26);
        g2.drawLine(ancho / 2 - 12, alto, ancho / 2, alto + ALTO_COLA);
        g2.drawLine(ancho / 2 + 12, alto, ancho / 2, alto + ALTO_COLA);
        g2.setColor(Color.WHITE);
        g2.drawLine(ancho / 2 - 10, alto, ancho / 2 + 10, alto);
        g2.dispose();
        super.paintComponent(g);
    }
}
