package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import modelo.items.Catalogo;

/**
 * Dibuja los accesorios puestos encima de las imágenes de Cabito: la gorra
 * en la cabeza, el saco en el cuerpo con el texto "UTadeo", la mochila en la
 * espalda y el termo a un lado, en el piso.
 * <p>
 * Cada pose tiene sus propias {@link Anclas}: dónde va cada accesorio, de
 * qué tamaño y con qué inclinación. Las posiciones son fracciones del ancho
 * y alto de la imagen (0 = izquierda/arriba, 1 = derecha/abajo) y los
 * tamaños están en píxeles de la imagen original. Quien llama ya escaló el
 * {@link Graphics2D} igual que la imagen, así los accesorios se mueven y
 * escalan junto con Cabito.
 */
public final class AccesoriosCabito {

    /**
     * Dónde y cómo va un accesorio en una pose.
     *
     * @param x      posición horizontal como fracción del ancho de la imagen
     * @param y      posición vertical como fracción del alto de la imagen
     * @param ancho  ancho en píxeles de la imagen (para el termo, su alto)
     * @param alto   alto en píxeles (solo lo usa el saco)
     * @param angulo inclinación en radianes (positivo = sentido horario)
     */
    public record Ancla(double x, double y, double ancho, double alto, double angulo) {
    }

    /**
     * Las anclas de todos los accesorios para una pose.
     *
     * @param gorra         punto de apoyo de la gorra (arriba de la cabeza)
     * @param saco          centro del saco sobre el cuerpo
     * @param mochila       centro de la mochila sobre la espalda
     * @param mochilaDetras si la mochila va detrás de Cabito (poses de frente)
     * @param termo         base del termo, en el piso a un lado
     */
    public record Anclas(Ancla gorra, Ancla saco, Ancla mochila, boolean mochilaDetras, Ancla termo) {
    }

    private static final Color CONTORNO = new Color(6, 22, 42);
    private static final Color COSTURA = new Color(40, 72, 110);
    private static final Color BRILLO = new Color(255, 255, 255, 110);

    /** Anclas medidas sobre cada imagen de Cabito. El retrato no lleva accesorios. */
    private static final Map<Pose, Anclas> ANCLAS = new EnumMap<>(Pose.class);

    static {
        ANCLAS.put(Pose.QUIETO, new Anclas(
                new Ancla(0.33, 0.075, 95, 0, -0.05),
                new Ancla(0.50, 0.535, 142, 52, 0),
                new Ancla(0.70, 0.37, 58, 0, 0.08), false,
                new Ancla(-0.07, 0.99, 0, 70, 0)));
        ANCLAS.put(Pose.CAMINAR, new Anclas(
                new Ancla(0.74, 0.09, 78, 0, 0.12),
                new Ancla(0.47, 0.48, 128, 54, -0.12),
                new Ancla(0.40, 0.29, 56, 0, -0.15), false,
                new Ancla(-0.03, 0.99, 0, 70, 0)));
        ANCLAS.put(Pose.CORRER, new Anclas(
                new Ancla(0.74, 0.09, 78, 0, 0.18),
                new Ancla(0.47, 0.51, 128, 54, -0.15),
                new Ancla(0.40, 0.33, 54, 0, -0.2), false,
                new Ancla(-0.04, 0.99, 0, 70, 0)));
        ANCLAS.put(Pose.SALTAR, new Anclas(
                new Ancla(0.73, 0.09, 78, 0, 0.12),
                new Ancla(0.47, 0.48, 126, 52, -0.35),
                new Ancla(0.40, 0.33, 54, 0, -0.4), false,
                new Ancla(-0.03, 0.99, 0, 70, 0)));
        ANCLAS.put(Pose.SENTADO, new Anclas(
                new Ancla(0.60, 0.065, 95, 0, 0.1),
                new Ancla(0.63, 0.56, 118, 52, 0),
                new Ancla(0.88, 0.44, 62, 0, 0.1), true,
                new Ancla(1.07, 0.99, 0, 70, 0)));
        ANCLAS.put(Pose.ACOSTADO, new Anclas(
                new Ancla(0.68, 0.095, 95, 0, 0),
                new Ancla(0.31, 0.46, 100, 50, -0.1),
                new Ancla(0.30, 0.18, 50, 0, -0.1), false,
                new Ancla(1.05, 0.99, 0, 70, 0)));
        ANCLAS.put(Pose.HERIDO, new Anclas(
                new Ancla(0.57, 0.085, 95, 0, 0.35),
                new Ancla(0.43, 0.61, 124, 54, 0),
                new Ancla(0.36, 0.40, 54, 0, -0.6), false,
                new Ancla(1.05, 0.99, 0, 70, 0)));
    }

    /** No se crean objetos de esta clase: todo es estático. */
    private AccesoriosCabito() {
    }

    /**
     * Devuelve las anclas de una pose.
     *
     * @param pose pose de Cabito
     * @return las anclas, o {@code null} si en esa pose no se dibujan accesorios
     */
    public static Anclas anclas(Pose pose) {
        return ANCLAS.get(pose);
    }

    /**
     * Dibuja lo que va detrás de Cabito: el termo en el piso y, en las poses
     * de frente, la mochila (que así asoma por detrás del hombro).
     *
     * @param g          gráficos ya escalados a píxeles de la imagen de Cabito
     * @param pose       pose que se está dibujando
     * @param equipados  nombres de los accesorios puestos
     * @param anchoImagen ancho de la imagen de Cabito en píxeles
     * @param altoImagen  alto de la imagen de Cabito en píxeles
     */
    public static void dibujarDetras(Graphics2D g, Pose pose, Set<String> equipados,
                                     int anchoImagen, int altoImagen) {
        Anclas anclas = ANCLAS.get(pose);
        if (anclas == null) {
            return;
        }
        if (equipados.contains(Catalogo.TERMO)) {
            dibujarEn(g, anclas.termo(), anchoImagen, altoImagen, () -> dibujarTermo(g, anclas.termo().alto()));
        }
        if (equipados.contains(Catalogo.MOCHILA) && anclas.mochilaDetras()) {
            dibujarEn(g, anclas.mochila(), anchoImagen, altoImagen,
                    () -> dibujarMochila(g, anclas.mochila().ancho()));
        }
    }

    /**
     * Dibuja lo que va encima de Cabito: saco, mochila (en las poses de lado)
     * y gorra, en ese orden.
     *
     * @param g          gráficos ya escalados a píxeles de la imagen de Cabito
     * @param pose       pose que se está dibujando
     * @param equipados  nombres de los accesorios puestos
     * @param anchoImagen ancho de la imagen de Cabito en píxeles
     * @param altoImagen  alto de la imagen de Cabito en píxeles
     */
    public static void dibujarDelante(Graphics2D g, Pose pose, Set<String> equipados,
                                      int anchoImagen, int altoImagen) {
        Anclas anclas = ANCLAS.get(pose);
        if (anclas == null) {
            return;
        }
        if (equipados.contains(Catalogo.SACO)) {
            Ancla saco = anclas.saco();
            dibujarEn(g, saco, anchoImagen, altoImagen, () -> dibujarSaco(g, saco.ancho(), saco.alto()));
        }
        if (equipados.contains(Catalogo.MOCHILA) && !anclas.mochilaDetras()) {
            dibujarEn(g, anclas.mochila(), anchoImagen, altoImagen,
                    () -> dibujarMochila(g, anclas.mochila().ancho()));
        }
        if (equipados.contains(Catalogo.GORRA)) {
            dibujarEn(g, anclas.gorra(), anchoImagen, altoImagen, () -> dibujarGorra(g, anclas.gorra().ancho()));
        }
    }

    /**
     * Mueve el origen al ancla, aplica su inclinación, dibuja y deja el
     * {@link Graphics2D} como estaba.
     */
    private static void dibujarEn(Graphics2D g, Ancla ancla, int anchoImagen, int altoImagen, Runnable dibujo) {
        AffineTransform original = g.getTransform();
        g.translate(ancla.x() * anchoImagen, ancla.y() * altoImagen);
        g.rotate(ancla.angulo());
        dibujo.run();
        g.setTransform(original);
    }

    // ------------------------------------------------------------------
    // Dibujos (cada uno centrado en el origen)
    // ------------------------------------------------------------------

    /** Rellena una figura y le pone contorno. */
    private static void rellenar(Graphics2D g, Shape figura, Color color, double grosor) {
        g.setColor(color);
        g.fill(figura);
        g.setColor(CONTORNO);
        g.setStroke(new BasicStroke((float) grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(figura);
    }

    /** Texto en negrita centrado en (x, y). */
    private static void texto(Graphics2D g, String texto, double x, double y, double tamano) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 1).deriveFont((float) tamano));
        Rectangle2D medida = g.getFontMetrics().getStringBounds(texto, g);
        g.setColor(Color.WHITE);
        g.drawString(texto, (float) (x - medida.getWidth() / 2), (float) (y - medida.getCenterY()));
    }

    /**
     * Gorra: copa azul apoyada en el origen (su borde de abajo queda en y = 0)
     * con visera teal hacia la derecha, hacia donde mira Cabito.
     */
    private static void dibujarGorra(Graphics2D g, double ancho) {
        double alto = ancho * 0.5;
        double grosor = ancho * 0.035;
        Path2D visera = new Path2D.Double();
        visera.moveTo(ancho * 0.1, -ancho * 0.02);
        visera.quadTo(ancho * 0.6, -ancho * 0.12, ancho * 0.8, ancho * 0.02);
        visera.quadTo(ancho * 0.55, ancho * 0.13, ancho * 0.1, ancho * 0.08);
        visera.closePath();
        rellenar(g, visera, Estilo.TEAL, grosor);
        Arc2D copa = new Arc2D.Double(-ancho / 2, -alto, ancho, alto * 2, 0, 180, Arc2D.CHORD);
        rellenar(g, copa, Estilo.AZUL_OSCURO, grosor);
        g.setColor(COSTURA);
        g.draw(new Line2D.Double(ancho * 0.08, -alto * 0.97, ancho * 0.08, 0));
        rellenar(g, new Ellipse2D.Double(-ancho * 0.05, -alto - ancho * 0.05, ancho * 0.1, ancho * 0.07),
                Estilo.TEAL, grosor * 0.6);
        texto(g, "UT", -ancho * 0.12, -alto * 0.45, ancho * 0.22);
        g.setColor(BRILLO);
        g.fill(new Ellipse2D.Double(-ancho * 0.36, -alto * 0.8, ancho * 0.16, alto * 0.25));
    }

    /** Saco: banda azul con bordes teal y el texto "UTadeo" sobre el cuerpo. */
    private static void dibujarSaco(Graphics2D g, double ancho, double alto) {
        double grosor = alto * 0.05;
        RoundRectangle2D tela = new RoundRectangle2D.Double(-ancho / 2, -alto / 2, ancho, alto, alto * 0.7, alto * 0.7);
        g.setColor(Estilo.AZUL_OSCURO);
        g.fill(tela);
        Shape recorte = g.getClip();
        g.clip(tela);
        g.setColor(Estilo.TEAL);
        g.fill(new Rectangle2D.Double(-ancho / 2, -alto / 2, ancho * 0.11, alto));
        g.fill(new Rectangle2D.Double(ancho / 2 - ancho * 0.11, -alto / 2, ancho * 0.11, alto));
        g.setColor(COSTURA);
        g.setStroke(new BasicStroke((float) (grosor * 0.6)));
        g.draw(new Line2D.Double(-ancho * 0.36, alto * 0.32, ancho * 0.36, alto * 0.32));
        g.setClip(recorte);
        g.setColor(CONTORNO);
        g.setStroke(new BasicStroke((float) grosor));
        g.draw(tela);
        texto(g, "UTadeo", 0, -alto * 0.04, alto * 0.38);
    }

    /** Mochila: cuerpo azul con bolsillo teal y asa arriba. */
    private static void dibujarMochila(Graphics2D g, double tamano) {
        double grosor = tamano * 0.05;
        g.setColor(CONTORNO);
        g.setStroke(new BasicStroke((float) (tamano * 0.07), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new RoundRectangle2D.Double(-tamano * 0.15, -tamano * 0.62, tamano * 0.3, tamano * 0.24,
                tamano * 0.12, tamano * 0.12));
        rellenar(g, new RoundRectangle2D.Double(-tamano / 2, -tamano * 0.45, tamano, tamano * 0.9,
                tamano * 0.35, tamano * 0.35), Estilo.AZUL_OSCURO, grosor);
        rellenar(g, new RoundRectangle2D.Double(-tamano * 0.32, tamano * 0.02, tamano * 0.64, tamano * 0.34,
                tamano * 0.16, tamano * 0.16), Estilo.TEAL, grosor);
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Double(-tamano * 0.06, tamano * 0.07, tamano * 0.12, tamano * 0.07,
                tamano * 0.04, tamano * 0.04));
        texto(g, "UT", 0, -tamano * 0.18, tamano * 0.24);
        g.setColor(BRILLO);
        g.fill(new Ellipse2D.Double(-tamano * 0.38, -tamano * 0.38, tamano * 0.18, tamano * 0.12));
    }

    /** Termo: de pie en el piso (su base queda en y = 0), teal con tapa azul. */
    private static void dibujarTermo(Graphics2D g, double alto) {
        double grosor = alto * 0.04;
        g.setColor(new Color(0, 0, 0, 45));
        g.fill(new Ellipse2D.Double(-alto * 0.32, -alto * 0.06, alto * 0.64, alto * 0.12));
        RoundRectangle2D cuerpo = new RoundRectangle2D.Double(-alto * 0.2, -alto * 0.82, alto * 0.4, alto * 0.82,
                alto * 0.14, alto * 0.14);
        g.setColor(Estilo.TEAL);
        g.fill(cuerpo);
        Shape recorte = g.getClip();
        g.clip(cuerpo);
        g.setColor(Estilo.AZUL_OSCURO);
        g.fill(new Rectangle2D.Double(-alto * 0.2, -alto * 0.3, alto * 0.4, alto * 0.08));
        g.setClip(recorte);
        g.setColor(CONTORNO);
        g.setStroke(new BasicStroke((float) grosor));
        g.draw(cuerpo);
        texto(g, "UT", 0, -alto * 0.55, alto * 0.16);
        rellenar(g, new RoundRectangle2D.Double(-alto * 0.23, -alto, alto * 0.46, alto * 0.22,
                alto * 0.08, alto * 0.08), Estilo.AZUL_OSCURO, grosor);
        g.setColor(BRILLO);
        g.fill(new Ellipse2D.Double(-alto * 0.15, -alto * 0.72, alto * 0.06, alto * 0.3));
    }
}
