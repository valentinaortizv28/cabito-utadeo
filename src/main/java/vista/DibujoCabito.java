package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import modelo.Estado;

/**
 * Dibuja a Cabito según su estado.
 * <p>
 * Primero busca una imagen PNG para el estado (por ejemplo
 * {@code img/cabito_feliz.png}); si no existe, dibuja una figura con
 * Graphics2D: cambian los ojos, la boca y algunos detalles (Zzz, lágrima,
 * manchas...). También dibuja los accesorios equipados (gorra, saco, mochila).
 * <p>
 * Todas las medidas son proporcionales al tamaño del componente ({@code t}),
 * así la figura se ve bien en cualquier tamaño de ventana.
 */
public class DibujoCabito extends JComponent {

    private static final Color CUERPO = Estilo.TEAL;
    private static final Color CUERPO_ENFERMO = new Color(150, 190, 130);
    private static final Color PANZA = new Color(222, 246, 241);
    private static final Color MEJILLAS = new Color(255, 165, 180);
    private static final Color BOCA = new Color(120, 30, 45);
    private static final Color AGUA = new Color(90, 170, 240);
    private static final Color MANCHA = new Color(125, 95, 60, 200);

    private Estado estado = Estado.NORMAL;
    private boolean conGorra;
    private boolean conSaco;
    private boolean conMochila;

    /**
     * Crea el dibujo con Cabito en estado normal.
     */
    public DibujoCabito() {
        setPreferredSize(new Dimension(280, 280));
        setOpaque(false);
    }

    /**
     * Cambia el estado que se dibuja.
     *
     * @param estado estado actual de Cabito
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
        repaint();
    }

    /**
     * Indica qué accesorios se dibujan.
     *
     * @param gorra   si lleva la Gorra UTadeo
     * @param saco    si lleva el Saco UTadeo
     * @param mochila si lleva la Mochila UTadeo
     */
    public void setAccesorios(boolean gorra, boolean saco, boolean mochila) {
        this.conGorra = gorra;
        this.conSaco = saco;
        this.conMochila = mochila;
        repaint();
    }

    /**
     * @return el estado que se está dibujando
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Dibuja la imagen del estado o, si no hay, la figura.
     *
     * @param g contexto gráfico de Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        BufferedImage imagen = Imagenes.cargar(estado.getNombreImagen());
        if (imagen != null) {
            dibujarImagen(g2, imagen);
        } else {
            dibujarFigura(g2);
        }
        g2.dispose();
    }

    /**
     * Dibuja una imagen centrada y escalada sin deformarla.
     */
    private void dibujarImagen(Graphics2D g2, BufferedImage imagen) {
        double escala = Math.min(getWidth() / (double) imagen.getWidth(),
                getHeight() / (double) imagen.getHeight());
        int ancho = (int) (imagen.getWidth() * escala);
        int alto = (int) (imagen.getHeight() * escala);
        g2.drawImage(imagen, (getWidth() - ancho) / 2, (getHeight() - alto) / 2, ancho, alto, null);
    }

    /**
     * Dibuja a Cabito con figuras geométricas.
     */
    private void dibujarFigura(Graphics2D g2) {
        double t = Math.min(getWidth(), getHeight()) * 0.9;
        double cx = getWidth() / 2.0;
        double cy = getHeight() / 2.0 + t * 0.06;
        Color colorCuerpo = estado == Estado.ENFERMO ? CUERPO_ENFERMO : CUERPO;

        // Sombra en el piso
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fill(new Ellipse2D.Double(cx - 0.3 * t, cy + 0.32 * t, 0.6 * t, 0.08 * t));

        // Mochila (va detrás del cuerpo)
        if (conMochila) {
            g2.setColor(new Color(120, 72, 40));
            g2.fill(new RoundRectangle2D.Double(cx + 0.2 * t, cy - 0.08 * t, 0.2 * t, 0.32 * t, 0.06 * t, 0.06 * t));
        }

        // Orejas y cuerpo
        g2.setColor(colorCuerpo);
        g2.fill(new Ellipse2D.Double(cx - 0.3 * t, cy - 0.43 * t, 0.16 * t, 0.2 * t));
        g2.fill(new Ellipse2D.Double(cx + 0.14 * t, cy - 0.43 * t, 0.16 * t, 0.2 * t));
        Shape cuerpo = new Ellipse2D.Double(cx - 0.32 * t, cy - 0.36 * t, 0.64 * t, 0.7 * t);
        g2.fill(cuerpo);

        // Panza
        g2.setColor(PANZA);
        g2.fill(new Ellipse2D.Double(cx - 0.18 * t, cy + 0.02 * t, 0.36 * t, 0.28 * t));

        // Saco: se pinta la parte de abajo del cuerpo, recortada a su forma
        if (conSaco) {
            Shape recorteAnterior = g2.getClip();
            g2.clip(cuerpo);
            g2.setColor(Estilo.AZUL_OSCURO);
            g2.fill(new Rectangle2D.Double(cx - 0.4 * t, cy + 0.1 * t, 0.8 * t, 0.4 * t));
            g2.setColor(Color.WHITE);
            textoCentrado(g2, "UTadeo", cx, cy + 0.24 * t, (float) (0.07 * t));
            g2.setClip(recorteAnterior);
        }

        // Manchas de suciedad
        if (estado == Estado.SUCIO) {
            g2.setColor(MANCHA);
            g2.fill(new Ellipse2D.Double(cx - 0.25 * t, cy + 0.05 * t, 0.08 * t, 0.06 * t));
            g2.fill(new Ellipse2D.Double(cx + 0.15 * t, cy - 0.2 * t, 0.07 * t, 0.05 * t));
            g2.fill(new Ellipse2D.Double(cx + 0.05 * t, cy + 0.2 * t, 0.09 * t, 0.06 * t));
        }

        // Contorno
        g2.setColor(colorCuerpo.darker());
        g2.setStroke(new BasicStroke((float) (t * 0.012)));
        g2.draw(cuerpo);

        dibujarOjos(g2, cx, cy, t, colorCuerpo);
        dibujarBoca(g2, cx, cy, t);
        dibujarDetalles(g2, cx, cy, t);
        if (conGorra) {
            dibujarGorra(g2, cx, cy, t);
        }
    }

    /**
     * Dibuja los ojos según el estado.
     */
    private void dibujarOjos(Graphics2D g2, double cx, double cy, double t, Color colorCuerpo) {
        double radio = 0.07 * t;
        double ojoY = cy - 0.14 * t;
        double[] ojosX = {cx - 0.12 * t, cx + 0.12 * t};
        g2.setStroke(new BasicStroke((float) (t * 0.018), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (double ojoX : ojosX) {
            double x = ojoX - radio;
            double y = ojoY - radio;
            switch (estado) {
                case DORMIDO:
                    g2.setColor(Estilo.AZUL_OSCURO);
                    g2.draw(new Arc2D.Double(x, y - radio * 0.4, 2 * radio, 2 * radio, 180, 180, Arc2D.OPEN));
                    break;
                case FELIZ:
                    g2.setColor(Estilo.AZUL_OSCURO);
                    g2.draw(new Arc2D.Double(x, y + radio * 0.4, 2 * radio, 2 * radio, 0, 180, Arc2D.OPEN));
                    break;
                case ENFERMO:
                    g2.setColor(Estilo.AZUL_OSCURO);
                    g2.draw(new Line2D.Double(x + radio * 0.4, y + radio * 0.4, x + radio * 1.6, y + radio * 1.6));
                    g2.draw(new Line2D.Double(x + radio * 1.6, y + radio * 0.4, x + radio * 0.4, y + radio * 1.6));
                    break;
                default:
                    g2.setColor(Color.WHITE);
                    g2.fill(new Ellipse2D.Double(x, y, 2 * radio, 2 * radio));
                    g2.setColor(Estilo.AZUL_OSCURO);
                    double pupila = radio * 1.1;
                    double bajar = estado == Estado.CANSADO || estado == Estado.TRISTE ? radio * 0.35 : 0;
                    g2.fill(new Ellipse2D.Double(ojoX - pupila / 2, ojoY - pupila / 2 + bajar, pupila, pupila));
                    g2.setColor(Color.WHITE);
                    g2.fill(new Ellipse2D.Double(ojoX - pupila * 0.1, ojoY - pupila * 0.35 + bajar,
                            pupila * 0.3, pupila * 0.3));
                    if (estado == Estado.CANSADO) {
                        // Párpado caído: media elipse del color del cuerpo
                        g2.setColor(colorCuerpo);
                        g2.fill(new Arc2D.Double(x - 1, y - 1, 2 * radio + 2, 2 * radio + 2, 0, 180, Arc2D.CHORD));
                        g2.setColor(colorCuerpo.darker());
                        g2.draw(new Line2D.Double(x, ojoY, x + 2 * radio, ojoY));
                    }
                    break;
            }
        }

        if (estado == Estado.TRISTE) {
            // Cejas inclinadas y una lágrima
            g2.setColor(Estilo.AZUL_OSCURO);
            g2.draw(new Line2D.Double(cx - 0.2 * t, cy - 0.23 * t, cx - 0.07 * t, cy - 0.27 * t));
            g2.draw(new Line2D.Double(cx + 0.2 * t, cy - 0.23 * t, cx + 0.07 * t, cy - 0.27 * t));
            dibujarGota(g2, cx - 0.17 * t, cy - 0.04 * t, 0.05 * t);
        }
    }

    /**
     * Dibuja la boca según el estado.
     */
    private void dibujarBoca(Graphics2D g2, double cx, double cy, double t) {
        double bocaY = cy - 0.03 * t;
        g2.setColor(BOCA);
        g2.setStroke(new BasicStroke((float) (t * 0.016), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        switch (estado) {
            case FELIZ:
                g2.fill(new Arc2D.Double(cx - 0.1 * t, bocaY - 0.07 * t, 0.2 * t, 0.16 * t, 180, 180, Arc2D.CHORD));
                break;
            case TRISTE:
                g2.draw(new Arc2D.Double(cx - 0.07 * t, bocaY + 0.01 * t, 0.14 * t, 0.08 * t, 20, 140, Arc2D.OPEN));
                break;
            case HAMBRIENTO:
                g2.fill(new Ellipse2D.Double(cx - 0.05 * t, bocaY - 0.02 * t, 0.1 * t, 0.08 * t));
                dibujarGota(g2, cx + 0.04 * t, bocaY + 0.07 * t, 0.035 * t);
                break;
            case CANSADO:
                g2.draw(new Line2D.Double(cx - 0.05 * t, bocaY + 0.02 * t, cx + 0.05 * t, bocaY + 0.02 * t));
                break;
            case ENFERMO:
                Path2D ondas = new Path2D.Double();
                ondas.moveTo(cx - 0.07 * t, bocaY + 0.02 * t);
                ondas.quadTo(cx - 0.035 * t, bocaY - 0.01 * t, cx, bocaY + 0.02 * t);
                ondas.quadTo(cx + 0.035 * t, bocaY + 0.05 * t, cx + 0.07 * t, bocaY + 0.02 * t);
                g2.draw(ondas);
                break;
            case DORMIDO:
                g2.draw(new Ellipse2D.Double(cx - 0.02 * t, bocaY, 0.04 * t, 0.035 * t));
                break;
            default:
                g2.draw(new Arc2D.Double(cx - 0.07 * t, bocaY - 0.04 * t, 0.14 * t, 0.08 * t, 200, 140, Arc2D.OPEN));
                break;
        }
    }

    /**
     * Dibuja detalles extra: mejillas, Zzz, sudor, moscas, brillos.
     */
    private void dibujarDetalles(Graphics2D g2, double cx, double cy, double t) {
        if (estado == Estado.FELIZ || estado == Estado.NORMAL) {
            g2.setColor(MEJILLAS);
            g2.fill(new Ellipse2D.Double(cx - 0.25 * t, cy - 0.07 * t, 0.08 * t, 0.05 * t));
            g2.fill(new Ellipse2D.Double(cx + 0.17 * t, cy - 0.07 * t, 0.08 * t, 0.05 * t));
        }
        switch (estado) {
            case DORMIDO:
                g2.setColor(Estilo.AZUL_OSCURO);
                textoCentrado(g2, "Z", cx + 0.3 * t, cy - 0.3 * t, (float) (0.08 * t));
                textoCentrado(g2, "z", cx + 0.38 * t, cy - 0.4 * t, (float) (0.065 * t));
                textoCentrado(g2, "z", cx + 0.44 * t, cy - 0.48 * t, (float) (0.05 * t));
                break;
            case ENFERMO:
                dibujarGota(g2, cx + 0.22 * t, cy - 0.27 * t, 0.05 * t);
                break;
            case SUCIO:
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke((float) (t * 0.006)));
                double[][] moscas = {{0.25, -0.42}, {0.36, -0.36}, {-0.33, -0.4}};
                for (double[] mosca : moscas) {
                    double mx = cx + mosca[0] * t;
                    double my = cy + mosca[1] * t;
                    g2.fill(new Ellipse2D.Double(mx, my, 0.02 * t, 0.02 * t));
                    g2.draw(new Arc2D.Double(mx - 0.02 * t, my - 0.02 * t, 0.03 * t, 0.03 * t, 0, 180, Arc2D.OPEN));
                }
                break;
            case FELIZ:
                g2.setColor(new Color(255, 200, 40));
                dibujarBrillo(g2, cx - 0.38 * t, cy - 0.3 * t, 0.05 * t);
                dibujarBrillo(g2, cx + 0.38 * t, cy - 0.18 * t, 0.04 * t);
                break;
            default:
                break;
        }
    }

    /**
     * Dibuja la gorra UTadeo sobre la cabeza.
     */
    private void dibujarGorra(Graphics2D g2, double cx, double cy, double t) {
        g2.setColor(Estilo.AZUL_OSCURO);
        g2.fill(new Arc2D.Double(cx - 0.24 * t, cy - 0.5 * t, 0.48 * t, 0.32 * t, 0, 180, Arc2D.CHORD));
        g2.fill(new RoundRectangle2D.Double(cx - 0.02 * t, cy - 0.37 * t, 0.36 * t, 0.05 * t, 0.04 * t, 0.04 * t));
        g2.setColor(Color.WHITE);
        textoCentrado(g2, "UT", cx, cy - 0.39 * t, (float) (0.07 * t));
    }

    /**
     * Dibuja una gota de agua (lágrima, sudor o baba).
     */
    private void dibujarGota(Graphics2D g2, double x, double y, double tamano) {
        Path2D gota = new Path2D.Double();
        gota.moveTo(x, y - tamano);
        gota.quadTo(x + tamano * 0.7, y, x, y + tamano * 0.5);
        gota.quadTo(x - tamano * 0.7, y, x, y - tamano);
        g2.setColor(AGUA);
        g2.fill(gota);
    }

    /**
     * Dibuja un brillo en forma de estrella de cuatro puntas.
     */
    private void dibujarBrillo(Graphics2D g2, double x, double y, double r) {
        Path2D brillo = new Path2D.Double();
        brillo.moveTo(x, y - r);
        brillo.quadTo(x, y, x + r, y);
        brillo.quadTo(x, y, x, y + r);
        brillo.quadTo(x, y, x - r, y);
        brillo.quadTo(x, y, x, y - r);
        g2.fill(brillo);
    }

    /**
     * Escribe un texto en negrita centrado en un punto.
     */
    private void textoCentrado(Graphics2D g2, String texto, double x, double y, float tamano) {
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, Math.round(tamano))));
        FontMetrics metricas = g2.getFontMetrics();
        float px = (float) (x - metricas.stringWidth(texto) / 2.0);
        float py = (float) (y + (metricas.getAscent() - metricas.getDescent()) / 2.0);
        g2.drawString(texto, px, py);
    }
}
