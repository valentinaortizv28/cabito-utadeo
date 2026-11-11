package vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import modelo.items.Catalogo;

/**
 * Íconos de los objetos de la Tienda Tadeísta, dibujados con Java2D en
 * estilo caricatura: contorno grueso azul oscuro, colores institucionales
 * (azul y teal), un brillo blanco y una sombra en el piso.
 * <p>
 * Cada ícono se dibuja en un cuadrado de 1 x 1 (coordenadas de 0 a 1) y
 * luego se escala al tamaño pedido con {@link Graphics2D#scale}; así el mismo
 * dibujo sirve para cualquier tamaño sin perder calidad.
 * <p>
 * Si existe un PNG en {@code img/items/<clave>.png} (por ejemplo
 * {@code img/items/tinto.png}), se usa ese en lugar del dibujo.
 */
public final class IconosItems {

    /** Clave del ícono para un objeto que no está en el catálogo. */
    public static final String CLAVE_DESCONOCIDA = "desconocido";

    private static final Color CONTORNO = Estilo.AZUL_OSCURO;
    private static final Color BLANCO = Color.WHITE;
    private static final Color BRILLO = new Color(255, 255, 255, 120);
    private static final Color SOMBRA = new Color(0, 0, 0, 40);
    private static final Color CAFE = new Color(92, 52, 26);
    private static final Color DORADO = new Color(232, 168, 72);
    private static final Color DORADO_OSCURO = new Color(204, 132, 48);
    private static final Color AGUA = new Color(196, 232, 250);
    private static final Color AGUA_OSCURA = new Color(128, 200, 240);
    private static final Color ROJO = new Color(214, 48, 58);
    private static final Color VERDE = new Color(88, 176, 72);
    private static final Color GRIS = new Color(196, 204, 214);
    private static final float GROSOR = 0.035f;

    /** Nombre del objeto → clave del ícono (también es el nombre del PNG opcional). */
    private static final Map<String, String> CLAVES = Map.ofEntries(
            Map.entry(Catalogo.TINTO, "tinto"),
            Map.entry(Catalogo.EMPANADA, "empanada"),
            Map.entry(Catalogo.ALMUERZO, "almuerzo"),
            Map.entry(Catalogo.AGUA, "agua"),
            Map.entry(Catalogo.BALON, "balon"),
            Map.entry(Catalogo.CUADERNO, "cuaderno"),
            Map.entry(Catalogo.KIT_ASEO, "kit_aseo"),
            Map.entry(Catalogo.BOTIQUIN, "botiquin"),
            Map.entry(Catalogo.GORRA, "gorra"),
            Map.entry(Catalogo.SACO, "saco"),
            Map.entry(Catalogo.TERMO, "termo"),
            Map.entry(Catalogo.MOCHILA, "mochila"));

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    /** No se crean objetos de esta clase: todo es estático. */
    private IconosItems() {
    }

    /**
     * Devuelve la clave del ícono de un objeto.
     *
     * @param nombreItem nombre del objeto, por ejemplo {@link Catalogo#TINTO}
     * @return la clave (por ejemplo {@code "tinto"}) o {@link #CLAVE_DESCONOCIDA}
     */
    public static String clave(String nombreItem) {
        return CLAVES.getOrDefault(nombreItem, CLAVE_DESCONOCIDA);
    }

    /**
     * Devuelve el ícono de un objeto listo para un {@code JLabel}.
     *
     * @param nombreItem nombre del objeto
     * @param tamano     ancho y alto en píxeles
     * @return el ícono
     */
    public static ImageIcon icono(String nombreItem, int tamano) {
        return new ImageIcon(imagen(nombreItem, tamano));
    }

    /**
     * Devuelve la imagen del ícono de un objeto, con fondo transparente.
     * Cada combinación de objeto y tamaño se dibuja una sola vez.
     *
     * @param nombreItem nombre del objeto
     * @param tamano     ancho y alto en píxeles
     * @return imagen cuadrada del ícono
     */
    public static BufferedImage imagen(String nombreItem, int tamano) {
        String clave = clave(nombreItem);
        String llave = clave + "@" + tamano;
        if (!CACHE.containsKey(llave)) {
            CACHE.put(llave, crear(clave, tamano));
        }
        return CACHE.get(llave);
    }

    /**
     * Crea la imagen: usa el PNG si existe; si no, dibuja el ícono.
     */
    private static BufferedImage crear(String clave, int tamano) {
        BufferedImage imagen = new BufferedImage(tamano, tamano, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        BufferedImage png = Imagenes.cargar("items/" + clave + ".png");
        if (png != null) {
            g.drawImage(png, 0, 0, tamano, tamano, null);
        } else {
            g.scale(tamano, tamano);
            g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            dibujar(g, clave);
        }
        g.dispose();
        return imagen;
    }

    /**
     * Dibuja el ícono de una clave en el cuadrado de 0 a 1.
     */
    private static void dibujar(Graphics2D g, String clave) {
        sombra(g);
        switch (clave) {
            case "tinto":
                dibujarTinto(g);
                break;
            case "empanada":
                dibujarEmpanada(g);
                break;
            case "almuerzo":
                dibujarAlmuerzo(g);
                break;
            case "agua":
                dibujarAgua(g);
                break;
            case "balon":
                dibujarBalon(g);
                break;
            case "cuaderno":
                dibujarCuaderno(g);
                break;
            case "kit_aseo":
                dibujarKitAseo(g);
                break;
            case "botiquin":
                dibujarBotiquin(g);
                break;
            case "gorra":
                dibujarGorra(g);
                break;
            case "saco":
                dibujarSaco(g);
                break;
            case "termo":
                dibujarTermo(g);
                break;
            case "mochila":
                dibujarMochila(g);
                break;
            default:
                dibujarDesconocido(g);
                break;
        }
    }

    // ------------------------------------------------------------------
    // Ayudantes de dibujo
    // ------------------------------------------------------------------

    /** Rellena una figura y le dibuja el contorno caricaturesco. */
    private static void rellenar(Graphics2D g, Shape figura, Color color) {
        g.setColor(color);
        g.fill(figura);
        g.setColor(CONTORNO);
        g.draw(figura);
    }

    /** Sombra ovalada en el piso. */
    private static void sombra(Graphics2D g) {
        g.setColor(SOMBRA);
        g.fill(new Ellipse2D.Double(0.18, 0.86, 0.64, 0.09));
    }

    /** Brillo blanco semitransparente (da volumen). */
    private static void brillo(Graphics2D g, double x, double y, double ancho, double alto) {
        g.setColor(BRILLO);
        g.fill(new Ellipse2D.Double(x, y, ancho, alto));
    }

    /** Escribe un texto en negrita centrado en (x, y). */
    private static void texto(Graphics2D g, String texto, double x, double y, float tamano, Color color) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 1).deriveFont(tamano));
        Rectangle2D medida = g.getFontMetrics().getStringBounds(texto, g);
        g.setColor(color);
        g.drawString(texto, (float) (x - medida.getWidth() / 2), (float) (y - medida.getCenterY()));
    }

    /** Rectángulo redondeado (abreviatura). */
    private static RoundRectangle2D rr(double x, double y, double ancho, double alto, double radio) {
        return new RoundRectangle2D.Double(x, y, ancho, alto, radio, radio);
    }

    // ------------------------------------------------------------------
    // Comida
    // ------------------------------------------------------------------

    /** Taza de tinto con platillo y vapor. */
    private static void dibujarTinto(Graphics2D g) {
        rellenar(g, new Ellipse2D.Double(0.12, 0.74, 0.76, 0.14), Estilo.TEAL);
        Area asa = new Area(new Ellipse2D.Double(0.6, 0.44, 0.22, 0.22));
        asa.subtract(new Area(new Ellipse2D.Double(0.65, 0.49, 0.12, 0.12)));
        rellenar(g, asa, BLANCO);

        Path2D taza = new Path2D.Double();
        taza.moveTo(0.2, 0.38);
        taza.lineTo(0.72, 0.38);
        taza.lineTo(0.66, 0.74);
        taza.quadTo(0.46, 0.84, 0.26, 0.74);
        taza.closePath();
        g.setColor(BLANCO);
        g.fill(taza);
        Shape recorte = g.getClip();
        g.clip(taza);
        g.setColor(Estilo.TEAL);
        g.fill(new Rectangle2D.Double(0.15, 0.5, 0.7, 0.1));
        g.setClip(recorte);
        g.setColor(CONTORNO);
        g.draw(taza);
        rellenar(g, new Ellipse2D.Double(0.22, 0.34, 0.48, 0.09), CAFE);

        g.setColor(GRIS);
        g.setStroke(new BasicStroke(0.03f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (double x : new double[] {0.36, 0.5}) {
            Path2D vapor = new Path2D.Double();
            vapor.moveTo(x, 0.3);
            vapor.curveTo(x - 0.06, 0.24, x + 0.06, 0.18, x, 0.1);
            g.draw(vapor);
        }
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        brillo(g, 0.26, 0.44, 0.06, 0.18);
    }

    /** Empanada en media luna con su repulgue. */
    private static void dibujarEmpanada(Graphics2D g) {
        Path2D media = new Path2D.Double();
        media.moveTo(0.1, 0.72);
        media.curveTo(0.1, 0.16, 0.9, 0.16, 0.9, 0.72);
        media.quadTo(0.5, 0.8, 0.1, 0.72);
        media.closePath();
        rellenar(g, media, DORADO);
        // Repulgue: bolitas a lo largo de la curva (puntos de una curva de Bézier cúbica)
        g.setStroke(new BasicStroke(0.022f));
        for (int i = 0; i <= 16; i++) {
            double t = i / 16.0;
            double u = 1 - t;
            double x = u * u * u * 0.1 + 3 * u * u * t * 0.1 + 3 * u * t * t * 0.9 + t * t * t * 0.9;
            double y = u * u * u * 0.72 + 3 * u * u * t * 0.16 + 3 * u * t * t * 0.16 + t * t * t * 0.72;
            rellenar(g, new Ellipse2D.Double(x - 0.035, y - 0.035, 0.07, 0.07), DORADO_OSCURO);
        }
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(DORADO_OSCURO);
        g.fill(new Ellipse2D.Double(0.4, 0.55, 0.04, 0.03));
        g.fill(new Ellipse2D.Double(0.6, 0.6, 0.04, 0.03));
        g.fill(new Ellipse2D.Double(0.5, 0.48, 0.03, 0.03));
        brillo(g, 0.28, 0.44, 0.16, 0.08);
    }

    /** Plato con arroz, carne, maduro y ensalada. */
    private static void dibujarAlmuerzo(Graphics2D g) {
        rellenar(g, new Ellipse2D.Double(0.06, 0.3, 0.88, 0.58), Estilo.TEAL_CLARO);
        rellenar(g, new Ellipse2D.Double(0.15, 0.36, 0.7, 0.46), BLANCO);
        g.setStroke(new BasicStroke(0.022f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Arroz
        rellenar(g, new Ellipse2D.Double(0.2, 0.42, 0.3, 0.2), new Color(250, 243, 222));
        // Carne asada con marcas de parrilla
        RoundRectangle2D carne = rr(0.48, 0.42, 0.28, 0.17, 0.1);
        rellenar(g, carne, new Color(152, 86, 46));
        g.setColor(new Color(96, 52, 26));
        g.draw(new Line2D.Double(0.54, 0.46, 0.6, 0.56));
        g.draw(new Line2D.Double(0.63, 0.45, 0.69, 0.55));
        // Maduro (tajada ovalada inclinada)
        AffineTransform original = g.getTransform();
        g.rotate(-0.25, 0.56, 0.68);
        rellenar(g, new Ellipse2D.Double(0.42, 0.63, 0.3, 0.1), new Color(236, 160, 40));
        g.setTransform(original);
        // Ensalada
        rellenar(g, new Ellipse2D.Double(0.22, 0.6, 0.14, 0.1), VERDE);
        rellenar(g, new Ellipse2D.Double(0.31, 0.64, 0.07, 0.06), ROJO);
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        brillo(g, 0.2, 0.4, 0.12, 0.05);
    }

    /** Botella de agua con etiqueta UTadeo. */
    private static void dibujarAgua(Graphics2D g) {
        RoundRectangle2D cuerpo = rr(0.3, 0.26, 0.4, 0.62, 0.16);
        rellenar(g, rr(0.4, 0.15, 0.2, 0.15, 0.06), AGUA);
        g.setColor(AGUA);
        g.fill(cuerpo);
        Shape recorte = g.getClip();
        g.clip(cuerpo);
        g.setColor(AGUA_OSCURA);
        g.fill(new Rectangle2D.Double(0.28, 0.42, 0.44, 0.5));
        g.setColor(Estilo.AZUL_OSCURO);
        g.fill(new Rectangle2D.Double(0.28, 0.5, 0.44, 0.2));
        g.setClip(recorte);
        g.setColor(CONTORNO);
        g.draw(cuerpo);
        texto(g, "UT", 0.5, 0.6, 0.13f, BLANCO);
        rellenar(g, rr(0.37, 0.05, 0.26, 0.12, 0.05), Estilo.TEAL);
        brillo(g, 0.34, 0.28, 0.06, 0.2);
    }

    // ------------------------------------------------------------------
    // Juguetes
    // ------------------------------------------------------------------

    /** Balón con parches azul y teal. */
    private static void dibujarBalon(Graphics2D g) {
        Ellipse2D balon = new Ellipse2D.Double(0.13, 0.1, 0.74, 0.74);
        g.setColor(BLANCO);
        g.fill(balon);
        Shape recorte = g.getClip();
        g.clip(balon);
        double cx = 0.5;
        double cy = 0.47;
        g.setStroke(new BasicStroke(0.02f));
        for (int i = 0; i < 5; i++) {
            double angulo = -Math.PI / 2 + i * 2 * Math.PI / 5;
            double px = cx + Math.cos(angulo) * 0.36;
            double py = cy + Math.sin(angulo) * 0.36;
            g.setColor(CONTORNO);
            g.draw(new Line2D.Double(cx + Math.cos(angulo) * 0.11, cy + Math.sin(angulo) * 0.11, px, py));
            rellenar(g, pentagono(px, py, 0.11, angulo + Math.PI), Estilo.TEAL);
        }
        rellenar(g, pentagono(cx, cy, 0.12, -Math.PI / 2), Estilo.AZUL_OSCURO);
        g.setClip(recorte);
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(CONTORNO);
        g.draw(balon);
        brillo(g, 0.24, 0.2, 0.16, 0.1);
    }

    /** Pentágono regular centrado en (cx, cy). */
    private static Path2D pentagono(double cx, double cy, double radio, double anguloInicial) {
        Path2D figura = new Path2D.Double();
        for (int i = 0; i < 5; i++) {
            double angulo = anguloInicial + i * 2 * Math.PI / 5;
            double x = cx + Math.cos(angulo) * radio;
            double y = cy + Math.sin(angulo) * radio;
            if (i == 0) {
                figura.moveTo(x, y);
            } else {
                figura.lineTo(x, y);
            }
        }
        figura.closePath();
        return figura;
    }

    /** Cuaderno de argollas con portada UTadeo. */
    private static void dibujarCuaderno(Graphics2D g) {
        rellenar(g, rr(0.24, 0.14, 0.58, 0.72, 0.06), BLANCO);
        rellenar(g, rr(0.18, 0.1, 0.58, 0.72, 0.06), Estilo.AZUL_OSCURO);
        g.setColor(Estilo.TEAL);
        g.fill(new Rectangle2D.Double(0.18, 0.66, 0.58, 0.06));
        rellenar(g, rr(0.33, 0.26, 0.34, 0.2, 0.06), BLANCO);
        texto(g, "UT", 0.5, 0.36, 0.13f, Estilo.TEAL);
        g.setColor(GRIS);
        g.setStroke(new BasicStroke(0.025f));
        for (double y = 0.16; y < 0.78; y += 0.09) {
            g.draw(new Ellipse2D.Double(0.12, y, 0.11, 0.05));
        }
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        brillo(g, 0.56, 0.14, 0.14, 0.06);
    }

    // ------------------------------------------------------------------
    // Cuidado
    // ------------------------------------------------------------------

    /** Jabón teal con burbujas y un cepillo de dientes. */
    private static void dibujarKitAseo(Graphics2D g) {
        AffineTransform original = g.getTransform();
        g.rotate(0.45, 0.72, 0.5);
        rellenar(g, rr(0.68, 0.24, 0.09, 0.58, 0.05), Estilo.AZUL_OSCURO);
        rellenar(g, rr(0.665, 0.1, 0.12, 0.17, 0.04), BLANCO);
        g.setColor(Estilo.TEAL);
        for (double y = 0.13; y < 0.25; y += 0.035) {
            g.draw(new Line2D.Double(0.69, y, 0.76, y));
        }
        g.setTransform(original);

        rellenar(g, rr(0.1, 0.5, 0.52, 0.32, 0.14), Estilo.TEAL);
        texto(g, "UT", 0.36, 0.66, 0.14f, new Color(255, 255, 255, 170));
        brillo(g, 0.16, 0.54, 0.16, 0.06);

        g.setStroke(new BasicStroke(0.022f));
        double[][] burbujas = {{0.16, 0.3, 0.13}, {0.32, 0.2, 0.09}, {0.1, 0.16, 0.07}, {0.36, 0.36, 0.06}};
        for (double[] b : burbujas) {
            Ellipse2D burbuja = new Ellipse2D.Double(b[0], b[1], b[2], b[2]);
            g.setColor(new Color(220, 244, 252, 200));
            g.fill(burbuja);
            g.setColor(AGUA_OSCURA);
            g.draw(burbuja);
            g.setColor(BLANCO);
            g.fill(new Ellipse2D.Double(b[0] + b[2] * 0.2, b[1] + b[2] * 0.2, b[2] * 0.25, b[2] * 0.25));
        }
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    /** Botiquín blanco con cruz roja y franja teal. */
    private static void dibujarBotiquin(Graphics2D g) {
        g.setColor(CONTORNO);
        g.setStroke(new BasicStroke(0.055f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(rr(0.37, 0.12, 0.26, 0.18, 0.08));
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        RoundRectangle2D caja = rr(0.1, 0.24, 0.8, 0.6, 0.14);
        g.setColor(BLANCO);
        g.fill(caja);
        Shape recorte = g.getClip();
        g.clip(caja);
        g.setColor(Estilo.TEAL);
        g.fill(new Rectangle2D.Double(0.08, 0.24, 0.84, 0.13));
        g.setClip(recorte);
        g.setColor(CONTORNO);
        g.draw(caja);
        g.draw(new Line2D.Double(0.1, 0.37, 0.9, 0.37));

        Area cruz = new Area(rr(0.42, 0.44, 0.16, 0.34, 0.04));
        cruz.add(new Area(rr(0.33, 0.53, 0.34, 0.16, 0.04)));
        rellenar(g, cruz, ROJO);
        brillo(g, 0.16, 0.42, 0.1, 0.16);
    }

    // ------------------------------------------------------------------
    // Accesorios
    // ------------------------------------------------------------------

    /** Gorra azul con visera teal y letras UT. */
    private static void dibujarGorra(Graphics2D g) {
        Arc2D copa = new Arc2D.Double(0.08, 0.2, 0.68, 0.7, 0, 180, Arc2D.CHORD);
        rellenar(g, copa, Estilo.AZUL_OSCURO);
        g.setColor(new Color(40, 72, 110));
        g.draw(new Line2D.Double(0.42, 0.22, 0.42, 0.55));
        rellenar(g, new Ellipse2D.Double(0.38, 0.17, 0.08, 0.06), Estilo.TEAL);
        texto(g, "UT", 0.3, 0.44, 0.14f, BLANCO);

        Path2D visera = new Path2D.Double();
        visera.moveTo(0.38, 0.55);
        visera.quadTo(0.8, 0.46, 0.94, 0.62);
        visera.quadTo(0.76, 0.74, 0.38, 0.66);
        visera.closePath();
        rellenar(g, visera, Estilo.TEAL);
        g.setColor(CONTORNO);
        g.draw(new Line2D.Double(0.08, 0.55, 0.76, 0.55));
        brillo(g, 0.16, 0.28, 0.14, 0.08);
    }

    /** Saco azul con cuello, puños y pretina teal. */
    private static void dibujarSaco(Graphics2D g) {
        Path2D mangaIzquierda = new Path2D.Double();
        mangaIzquierda.moveTo(0.3, 0.2);
        mangaIzquierda.lineTo(0.12, 0.38);
        mangaIzquierda.lineTo(0.08, 0.72);
        mangaIzquierda.lineTo(0.22, 0.74);
        mangaIzquierda.lineTo(0.28, 0.46);
        mangaIzquierda.closePath();
        Path2D mangaDerecha = new Path2D.Double(mangaIzquierda,
                new AffineTransform(-1, 0, 0, 1, 1, 0));
        rellenar(g, mangaIzquierda, Estilo.AZUL_OSCURO);
        rellenar(g, mangaDerecha, Estilo.AZUL_OSCURO);
        rellenar(g, rr(0.075, 0.68, 0.16, 0.08, 0.03), Estilo.TEAL);
        rellenar(g, rr(0.765, 0.68, 0.16, 0.08, 0.03), Estilo.TEAL);

        rellenar(g, rr(0.26, 0.18, 0.48, 0.64, 0.1), Estilo.AZUL_OSCURO);
        rellenar(g, rr(0.26, 0.74, 0.48, 0.09, 0.04), Estilo.TEAL);
        rellenar(g, new Arc2D.Double(0.38, 0.11, 0.24, 0.16, 180, 180, Arc2D.CHORD), Estilo.TEAL);
        texto(g, "UTadeo", 0.5, 0.48, 0.1f, BLANCO);
        brillo(g, 0.3, 0.24, 0.08, 0.14);
    }

    /** Termo teal con tapa azul. */
    private static void dibujarTermo(Graphics2D g) {
        RoundRectangle2D cuerpo = rr(0.31, 0.2, 0.38, 0.68, 0.14);
        g.setColor(Estilo.TEAL);
        g.fill(cuerpo);
        Shape recorte = g.getClip();
        g.clip(cuerpo);
        g.setColor(Estilo.AZUL_OSCURO);
        g.fill(new Rectangle2D.Double(0.3, 0.64, 0.4, 0.08));
        g.setClip(recorte);
        g.setColor(CONTORNO);
        g.draw(cuerpo);
        texto(g, "UT", 0.5, 0.45, 0.14f, BLANCO);
        rellenar(g, rr(0.29, 0.07, 0.42, 0.17, 0.07), Estilo.AZUL_OSCURO);
        g.setColor(new Color(40, 72, 110));
        g.draw(new Line2D.Double(0.33, 0.16, 0.67, 0.16));
        brillo(g, 0.36, 0.26, 0.06, 0.3);
    }

    /** Mochila azul con bolsillo teal. */
    private static void dibujarMochila(Graphics2D g) {
        g.setColor(CONTORNO);
        g.setStroke(new BasicStroke(0.05f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(rr(0.4, 0.06, 0.2, 0.16, 0.08));
        g.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        rellenar(g, rr(0.16, 0.14, 0.68, 0.72, 0.24), Estilo.AZUL_OSCURO);
        g.setColor(new Color(60, 96, 140));
        g.draw(new Arc2D.Double(0.22, 0.2, 0.56, 0.4, 20, 140, Arc2D.OPEN));
        rellenar(g, rr(0.26, 0.5, 0.48, 0.3, 0.12), Estilo.TEAL);
        g.setColor(CONTORNO);
        g.draw(new Line2D.Double(0.28, 0.58, 0.72, 0.58));
        rellenar(g, rr(0.46, 0.55, 0.08, 0.06, 0.03), BLANCO);
        texto(g, "UT", 0.5, 0.36, 0.13f, BLANCO);
        brillo(g, 0.22, 0.2, 0.12, 0.08);
    }

    /** Signo de pregunta para un objeto sin ícono. */
    private static void dibujarDesconocido(Graphics2D g) {
        rellenar(g, new Ellipse2D.Double(0.16, 0.12, 0.68, 0.68), Estilo.TEAL_CLARO);
        texto(g, "?", 0.5, 0.46, 0.4f, Estilo.AZUL_OSCURO);
    }
}
