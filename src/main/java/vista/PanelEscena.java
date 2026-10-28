package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import controlador.JuegoController;
import modelo.Cabito;
import modelo.Estado;
import modelo.Partida;
import modelo.items.Catalogo;
import modelo.items.Inventario;
import modelo.mundos.Biblioteca;
import modelo.mundos.Brujula;
import modelo.mundos.Modulo20;
import modelo.mundos.Mundo;

/**
 * Centro de la ventana: el fondo del mundo actual, Cabito, su globo de
 * diálogo y los botones de acción.
 * <p>
 * Los botones se habilitan o deshabilitan según el estado de Cabito
 * (por ejemplo, dormido solo permite "Despertar").
 */
public class PanelEscena extends JPanel {

    private final GloboDialogo globo;
    private final DibujoCabito dibujo;
    private final JButton botonAlimentar;
    private final JButton botonJugar;
    private final JButton botonDormir;
    private final JButton botonBanar;
    private final JButton botonExplorar;
    private Mundo mundo;

    /**
     * Crea la escena.
     *
     * @param controlador controlador que recibe las acciones de los botones
     */
    public PanelEscena(JuegoController controlador) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        globo = new GloboDialogo();
        JPanel panelGlobo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelGlobo.setOpaque(false);
        panelGlobo.add(globo);
        add(panelGlobo, BorderLayout.NORTH);

        dibujo = new DibujoCabito();
        add(dibujo, BorderLayout.CENTER);

        botonAlimentar = Estilo.crearBoton("Alimentar", controlador::alimentar);
        botonJugar = Estilo.crearBoton("Jugar", controlador::jugar);
        botonDormir = Estilo.crearBoton("Dormir", controlador::dormirODespertar);
        botonBanar = Estilo.crearBoton("Bañar", controlador::banar);
        botonExplorar = Estilo.crearBoton("Explorar", controlador::explorar);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        acciones.setOpaque(false);
        acciones.add(botonAlimentar);
        acciones.add(botonJugar);
        acciones.add(botonDormir);
        acciones.add(botonBanar);
        acciones.add(botonExplorar);
        add(acciones, BorderLayout.SOUTH);
    }

    /**
     * Actualiza el dibujo, el fondo y los botones con los datos de la partida.
     *
     * @param partida partida en curso
     */
    public void actualizar(Partida partida) {
        Cabito cabito = partida.getCabito();
        Estado estado = cabito.getEstado();
        Inventario inventario = cabito.getInventario();
        mundo = partida.getMundoActual();

        dibujo.setEstado(estado);
        dibujo.setAccesorios(inventario.estaEquipado(Catalogo.GORRA), inventario.estaEquipado(Catalogo.SACO),
                inventario.estaEquipado(Catalogo.MOCHILA));

        boolean despierto = !estado.soloPermiteDespertar();
        configurar(botonAlimentar, despierto, "Cabito está dormido");
        configurar(botonJugar, estado.permiteJugar(), "No puede jugar: " + estado.getDescripcion());
        configurar(botonBanar, despierto, "Cabito está dormido");
        botonDormir.setText(cabito.isDormido() ? "Despertar" : "Dormir");
        botonExplorar.setText(mundo.getNombreActividad());
        configurar(botonExplorar, estado.permiteActividades(), "No puede salir: " + estado.getDescripcion());
        repaint();
    }

    /**
     * Habilita o deshabilita un botón y explica el motivo en su tooltip.
     */
    private void configurar(JButton boton, boolean habilitado, String motivo) {
        boton.setEnabled(habilitado);
        boton.setBackground(habilitado ? Estilo.TEAL : Estilo.BLOQUEADO);
        boton.setToolTipText(habilitado ? null : motivo);
    }

    /**
     * Cambia el texto del globo de diálogo.
     *
     * @param mensaje lo que dice Cabito
     */
    public void setMensaje(String mensaje) {
        globo.setMensaje(mensaje);
    }

    /**
     * @return el dibujo de Cabito (para pruebas)
     */
    DibujoCabito getDibujo() {
        return dibujo;
    }

    /**
     * @return texto actual del globo (para pruebas)
     */
    String getMensaje() {
        return globo.getText();
    }

    /**
     * @return botón Jugar (para pruebas)
     */
    JButton getBotonJugar() {
        return botonJugar;
    }

    /**
     * @return botón Dormir/Despertar (para pruebas)
     */
    JButton getBotonDormir() {
        return botonDormir;
    }

    /**
     * Pinta el fondo del mundo: una imagen PNG si existe o, si no, un
     * dibujo sencillo distinto para cada mundo.
     *
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mundo == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        BufferedImage fondo = Imagenes.cargar(mundo.getNombreFondo());
        if (fondo != null) {
            g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        } else {
            dibujarFondo(g2);
        }
        g2.setColor(new Color(11, 37, 69, 170));
        g2.fillRoundRect(12, 12, 170, 30, 14, 14);
        g2.setColor(Color.WHITE);
        g2.setFont(Estilo.FUENTE_NEGRITA);
        g2.drawString(mundo.getNombre(), 24, 33);
        g2.dispose();
    }

    /**
     * Dibuja un fondo sencillo según el mundo actual.
     */
    private void dibujarFondo(Graphics2D g2) {
        int ancho = getWidth();
        int alto = getHeight();
        int piso = (int) (alto * 0.72);
        switch (mundo.getNombre()) {
            case Biblioteca.NOMBRE:
                g2.setPaint(new GradientPaint(0, 0, new Color(246, 232, 208), 0, piso, new Color(226, 204, 168)));
                g2.fillRect(0, 0, ancho, piso);
                dibujarEstantes(g2, ancho, piso);
                g2.setColor(new Color(150, 105, 70));
                break;
            case Modulo20.NOMBRE:
                g2.setPaint(new GradientPaint(0, 0, new Color(222, 228, 238), 0, piso, new Color(190, 202, 218)));
                g2.fillRect(0, 0, ancho, piso);
                dibujarEdificio(g2, ancho, piso);
                g2.setColor(new Color(150, 158, 170));
                break;
            case Brujula.NOMBRE:
            default:
                g2.setPaint(new GradientPaint(0, 0, new Color(205, 236, 246), 0, piso, new Color(170, 222, 214)));
                g2.fillRect(0, 0, ancho, piso);
                dibujarBrujula(g2, ancho, piso);
                g2.setColor(new Color(124, 184, 120));
                break;
        }
        g2.fillRect(0, piso, ancho, alto - piso);
    }

    /** Estantes con libros de colores para la Biblioteca. */
    private void dibujarEstantes(Graphics2D g2, int ancho, int piso) {
        Color[] colores = {Estilo.TEAL, Estilo.AZUL_OSCURO, new Color(190, 70, 60), new Color(220, 170, 50)};
        for (int fila = 0; fila < 3; fila++) {
            int y = 60 + fila * (piso - 60) / 3;
            g2.setColor(new Color(150, 105, 70));
            g2.fillRect(0, y + 46, ancho, 8);
            for (int x = 10, i = 0; x < ancho - 20; x += 22, i++) {
                g2.setColor(colores[(i + fila) % colores.length]);
                g2.fillRect(x, y + 6 + (i % 3) * 4, 16, 40 - (i % 3) * 4);
            }
        }
    }

    /** Fachada con ventanas para el Módulo 20. */
    private void dibujarEdificio(Graphics2D g2, int ancho, int piso) {
        g2.setColor(new Color(160, 170, 186));
        g2.fillRect(ancho / 10, piso / 5, ancho * 8 / 10, piso * 4 / 5);
        g2.setColor(new Color(205, 230, 240));
        for (int y = piso / 5 + 20; y < piso - 40; y += 50) {
            for (int x = ancho / 10 + 20; x < ancho * 9 / 10 - 40; x += 60) {
                g2.fillRect(x, y, 36, 30);
            }
        }
    }

    /** Rosa de los vientos para La Brújula. */
    private void dibujarBrujula(Graphics2D g2, int ancho, int piso) {
        int cx = ancho / 2;
        int cy = piso / 2;
        int r = Math.min(ancho, piso) / 3;
        g2.setColor(new Color(255, 255, 255, 110));
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.setColor(new Color(18, 163, 154, 90));
        g2.fillPolygon(new Polygon(new int[] {cx, cx + r / 6, cx, cx - r / 6},
                new int[] {cy - r, cy, cy + r, cy}, 4));
        g2.fillPolygon(new Polygon(new int[] {cx - r, cx, cx + r, cx},
                new int[] {cy, cy - r / 6, cy, cy + r / 6}, 4));
    }
}
