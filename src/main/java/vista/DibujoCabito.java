package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.Timer;

import modelo.Estado;

/**
 * Muestra a Cabito con la imagen de la {@link Pose} que corresponde a su
 * estado, más dos animaciones sencillas:
 * <ul>
 *   <li>Una pose temporal (por ejemplo, correr un momento después de jugar).</li>
 *   <li>Una caminata: Cabito entra caminando desde la izquierda al cambiar de mundo.</li>
 * </ul>
 * También se puede fijar una pose mientras dura algo (por ejemplo, correr
 * durante un minijuego).
 * <p>
 * Las imágenes se escalan con interpolación bilineal para que se vean
 * suaves. Todas las poses usan la misma escala (calculada con un tamaño de
 * referencia), así Cabito no cambia de tamaño al cambiar de pose; y siempre
 * se dibuja con las patas sobre el piso, al centro y abajo del componente.
 */
public class DibujoCabito extends JComponent {

    /** Milisegundos que dura una pose temporal. */
    public static final int DURACION_POSE_TEMPORAL = 1500;
    /** Milisegundos que dura la caminata al cambiar de mundo. */
    public static final int DURACION_CAMINATA = 1400;

    /** Tamaño de referencia (en píxeles de las imágenes) que debe caber en el componente. */
    private static final double ANCHO_REFERENCIA = 380;
    private static final double ALTO_REFERENCIA = 320;
    /** Escala máxima, para no agrandar tanto las imágenes que se vean borrosas. */
    private static final double ESCALA_MAXIMA = 1.6;
    private static final int MARGEN_INFERIOR = 12;
    private static final int MILIS_POR_CUADRO = 30;
    /** Cuántos pasos (rebotes) da Cabito durante la caminata. */
    private static final int PASOS = 7;
    private static final double ALTURA_PASO = 6;

    private Estado estado = Estado.NORMAL;
    private Pose poseFija;
    private Pose poseTemporal;
    /** 0 = Cabito en su sitio; 1 = Cabito justo afuera, a la izquierda. */
    private double desplazamiento;
    /** Fase del rebote de la caminata, en radianes. */
    private double fasePaso;
    private long inicioCaminata;
    private final Timer temporizadorPose;
    private final Timer temporizadorCaminata;

    /**
     * Crea el dibujo con Cabito en estado normal.
     */
    public DibujoCabito() {
        setPreferredSize(new Dimension(320, 280));
        setOpaque(false);
        temporizadorPose = new Timer(DURACION_POSE_TEMPORAL, e -> terminarPoseTemporal());
        temporizadorPose.setRepeats(false);
        temporizadorCaminata = new Timer(MILIS_POR_CUADRO, e -> avanzarCaminata());
    }

    // ------------------------------------------------------------------
    // Qué pose se muestra
    // ------------------------------------------------------------------

    /**
     * Cambia el estado de Cabito (y con él la pose normal).
     *
     * @param estado estado actual de Cabito
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
        repaint();
    }

    /**
     * @return el estado que se está mostrando
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Devuelve la pose que se dibuja ahora. Tiene prioridad la pose fija,
     * luego la temporal y, si no hay ninguna, la del estado.
     *
     * @return la pose actual
     */
    public Pose getPose() {
        if (poseFija != null) {
            return poseFija;
        }
        if (poseTemporal != null) {
            return poseTemporal;
        }
        return Pose.para(estado);
    }

    /**
     * Fija una pose hasta que se llame a {@link #soltarPose()}.
     *
     * @param pose pose a mostrar
     */
    public void fijarPose(Pose pose) {
        poseFija = pose;
        repaint();
    }

    /**
     * Quita la pose fija y vuelve a la pose del estado.
     */
    public void soltarPose() {
        poseFija = null;
        repaint();
    }

    /**
     * Muestra una pose durante {@link #DURACION_POSE_TEMPORAL} milisegundos.
     *
     * @param pose pose a mostrar un momento
     */
    public void mostrarPoseTemporal(Pose pose) {
        temporizadorCaminata.stop();
        desplazamiento = 0;
        poseTemporal = pose;
        temporizadorPose.restart();
        repaint();
    }

    /**
     * Hace que Cabito entre caminando desde la izquierda hasta el centro.
     */
    public void animarCaminata() {
        temporizadorPose.stop();
        poseTemporal = Pose.CAMINAR;
        desplazamiento = 1;
        fasePaso = 0;
        inicioCaminata = System.currentTimeMillis();
        temporizadorCaminata.restart();
        repaint();
    }

    /**
     * @return {@code true} mientras hay una pose temporal o una caminata en curso
     */
    public boolean isAnimando() {
        return poseTemporal != null;
    }

    /**
     * Termina la pose temporal y vuelve a la pose del estado.
     */
    private void terminarPoseTemporal() {
        poseTemporal = null;
        repaint();
    }

    /**
     * Avanza un cuadro de la caminata. El desplazamiento se frena al final
     * ("ease-out") para que Cabito llegue suave a su sitio.
     */
    private void avanzarCaminata() {
        double avance = (System.currentTimeMillis() - inicioCaminata) / (double) DURACION_CAMINATA;
        if (avance >= 1) {
            temporizadorCaminata.stop();
            desplazamiento = 0;
            fasePaso = 0;
            terminarPoseTemporal();
            return;
        }
        double suavizado = 1 - (1 - avance) * (1 - avance);
        desplazamiento = 1 - suavizado;
        fasePaso = avance * PASOS * Math.PI;
        repaint();
    }

    // ------------------------------------------------------------------
    // Dibujo
    // ------------------------------------------------------------------

    /**
     * Dibuja la imagen de la pose actual, escalada con interpolación bilineal,
     * con una sombra en el piso.
     *
     * @param g contexto gráfico de Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Pose pose = getPose();
        BufferedImage imagen = Imagenes.cargar(pose.getRuta());
        if (imagen == null) {
            dibujarAvisoSinImagen(g2, pose);
        } else {
            dibujarImagen(g2, imagen, pose);
        }
        g2.dispose();
    }

    /**
     * Dibuja la imagen apoyada en el piso, centrada y desplazada si está caminando.
     */
    private void dibujarImagen(Graphics2D g2, BufferedImage imagen, Pose pose) {
        double escala;
        if (pose == Pose.RETRATO) {
            // El retrato ocupa todo el espacio disponible.
            escala = Math.min(getWidth() / (double) imagen.getWidth(),
                    (getHeight() - MARGEN_INFERIOR) / (double) imagen.getHeight());
        } else {
            escala = Math.min(getWidth() / ANCHO_REFERENCIA, (getHeight() - MARGEN_INFERIOR) / ALTO_REFERENCIA);
            escala = Math.min(escala, ESCALA_MAXIMA);
        }
        int ancho = (int) Math.round(imagen.getWidth() * escala);
        int alto = (int) Math.round(imagen.getHeight() * escala);
        int piso = getHeight() - MARGEN_INFERIOR;
        int x = (getWidth() - ancho) / 2 - (int) Math.round(desplazamiento * (getWidth() + ancho) / 2.0);
        int rebote = (int) Math.round(Math.abs(Math.sin(fasePaso)) * ALTURA_PASO * escala);
        int y = piso - alto - rebote;

        // Sombra en el piso
        g2.setColor(new Color(0, 0, 0, 45));
        int anchoSombra = (int) (ancho * 0.8);
        int altoSombra = Math.max(6, (int) (16 * escala));
        g2.fillOval(x + (ancho - anchoSombra) / 2, piso - altoSombra / 2, anchoSombra, altoSombra);

        g2.drawImage(imagen, x, y, ancho, alto, null);

        if (estado == Estado.DORMIDO && pose == Pose.ACOSTADO) {
            dibujarZetas(g2, x + ancho * 0.72, y + alto * 0.12, escala);
        }
    }

    /**
     * Dibuja "Z z z" sobre la cabeza cuando Cabito duerme.
     */
    private void dibujarZetas(Graphics2D g2, double x, double y, double escala) {
        g2.setColor(Estilo.AZUL_OSCURO);
        String[] letras = {"Z", "z", "z"};
        for (int i = 0; i < letras.length; i++) {
            int tamano = (int) Math.max(10, (30 - i * 6) * escala);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, tamano));
            g2.drawString(letras[i], (float) (x + i * 26 * escala), (float) (y - i * 26 * escala));
        }
    }

    /**
     * Si falta la imagen de una pose, muestra un aviso en vez de dejar el espacio vacío.
     */
    private void dibujarAvisoSinImagen(Graphics2D g2, Pose pose) {
        String texto = "Falta la imagen img/" + pose.getRuta();
        g2.setColor(Estilo.AZUL_OSCURO);
        g2.setFont(Estilo.FUENTE);
        FontMetrics metricas = g2.getFontMetrics();
        g2.drawString(texto, (getWidth() - metricas.stringWidth(texto)) / 2, getHeight() / 2);
    }
}
