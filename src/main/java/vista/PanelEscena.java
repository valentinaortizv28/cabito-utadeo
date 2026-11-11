package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

import controlador.Animacion;
import controlador.JuegoController;
import modelo.Cabito;
import modelo.Estado;
import modelo.Partida;
import modelo.items.Catalogo;
import modelo.items.Tienda;
import modelo.mundos.Biblioteca;
import modelo.mundos.Brujula;
import modelo.mundos.Modulo20;
import modelo.mundos.Mundo;

/**
 * Centro de la ventana: el fondo del mundo actual, Cabito (con la pose de su
 * estado y sus animaciones), su globo de diálogo y los botones de acción.
 * <p>
 * Los botones se habilitan o deshabilitan según el estado de Cabito
 * (por ejemplo, dormido solo permite "Despertar").
 * <p>
 * Cuando Cabito está en la Tienda Tadeísta, la escena cambia de modo: se
 * dibuja el local, a la derecha aparece el mostrador con los productos
 * ({@link PanelTienda}) y los botones de acción se cambian por "Volver".
 */
public class PanelEscena extends JPanel {

    private final GloboDialogo globo;
    private final DibujoCabito dibujo;
    private final JButton botonAlimentar;
    private final JButton botonJugar;
    private final JButton botonDormir;
    private final JButton botonBanar;
    private final JButton botonExplorar;
    private final JButton botonVolver;
    private final JPanel acciones;
    private final PanelTienda panelTienda;
    private Mundo mundo;
    private boolean enTienda;

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
        botonVolver = Estilo.crearBoton("Volver", controlador::salirDeTienda);
        botonVolver.setVisible(false);
        acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        acciones.setOpaque(false);
        acciones.add(botonAlimentar);
        acciones.add(botonJugar);
        acciones.add(botonDormir);
        acciones.add(botonBanar);
        acciones.add(botonExplorar);
        acciones.add(botonVolver);
        add(acciones, BorderLayout.SOUTH);

        panelTienda = new PanelTienda(controlador);
        panelTienda.setVisible(false);
        add(panelTienda, BorderLayout.EAST);
    }

    /**
     * Actualiza el dibujo, el fondo y los botones con los datos de la partida.
     *
     * @param partida partida en curso
     */
    public void actualizar(Partida partida) {
        Cabito cabito = partida.getCabito();
        Estado estado = cabito.getEstado();
        mundo = partida.getMundoActual();

        dibujo.setEstado(estado);

        boolean despierto = !estado.soloPermiteDespertar();
        configurar(botonAlimentar, despierto, "Cabito está dormido");
        configurar(botonJugar, estado.permiteJugar(), "No puede jugar: " + estado.getDescripcion());
        configurar(botonBanar, despierto, "Cabito está dormido");
        botonDormir.setText(cabito.isDormido() ? "Despertar" : "Dormir");
        botonExplorar.setText(mundo.getNombreActividad());
        configurar(botonExplorar, estado.permiteActividades(), "No puede salir: " + estado.getDescripcion());
        actualizarModoTienda(partida);
        repaint();
    }

    /**
     * Cambia entre el modo mundo y el modo tienda según dónde esté Cabito.
     *
     * @param partida partida en curso
     */
    private void actualizarModoTienda(Partida partida) {
        boolean ahoraEnTienda = partida.isEnTienda();
        if (ahoraEnTienda != enTienda) {
            enTienda = ahoraEnTienda;
            panelTienda.setVisible(enTienda);
            panelTienda.setContenidoVisible(true);
            for (Component boton : acciones.getComponents()) {
                boton.setVisible(!enTienda);
            }
            botonVolver.setVisible(enTienda);
            revalidate();
        }
        if (enTienda) {
            botonVolver.setText("Volver a " + mundo.getNombre());
            panelTienda.actualizar(partida);
        }
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
     * Muestra una animación de Cabito: correr un momento después de jugar, o
     * entrar caminando al llegar a otro mundo.
     *
     * @param animacion animación a mostrar
     */
    public void animar(Animacion animacion) {
        switch (animacion) {
            case JUGAR:
                dibujo.mostrarPoseTemporal(Pose.CORRER);
                break;
            case VIAJAR:
                dibujo.animarCaminata();
                break;
            case ENTRAR_TIENDA:
                // Cabito camina hasta el mostrador y solo entonces aparecen los productos.
                panelTienda.setContenidoVisible(false);
                dibujo.animarCaminata(() -> panelTienda.setContenidoVisible(true));
                break;
            default:
                break;
        }
    }

    /**
     * Pone a Cabito corriendo mientras hay un minijuego abierto.
     *
     * @param activo {@code true} al abrir el minijuego, {@code false} al cerrarlo
     */
    public void setPoseMinijuego(boolean activo) {
        if (activo) {
            dibujo.fijarPose(Pose.CORRER);
        } else {
            dibujo.soltarPose();
        }
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
     * @return botón para volver de la tienda (para pruebas)
     */
    JButton getBotonVolver() {
        return botonVolver;
    }

    /**
     * @return el mostrador de la tienda (para pruebas)
     */
    PanelTienda getPanelTienda() {
        return panelTienda;
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
        BufferedImage fondo = Imagenes.cargar(enTienda ? "fondo_tienda.png" : mundo.getNombreFondo());
        if (fondo != null) {
            g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        } else if (enTienda) {
            dibujarTienda(g2);
        } else {
            dibujarFondo(g2);
        }
        String lugar = enTienda ? Tienda.NOMBRE : mundo.getNombre();
        g2.setFont(Estilo.FUENTE_NEGRITA);
        int anchoEtiqueta = g2.getFontMetrics().stringWidth(lugar) + 24;
        g2.setColor(new Color(11, 37, 69, 170));
        g2.fillRoundRect(12, 12, Math.max(170, anchoEtiqueta), 30, 14, 14);
        g2.setColor(Color.WHITE);
        g2.drawString(lugar, 24, 33);
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

    /**
     * Dibuja el local de la Tienda Tadeísta: toldo a rayas, papel tapiz,
     * estantes de madera con merch UTadeo, un letrero y piso de madera.
     */
    private void dibujarTienda(Graphics2D g2) {
        int ancho = getWidth();
        int alto = getHeight();
        int piso = (int) (alto * 0.74);

        // Papel tapiz a rayas
        for (int x = 0; x < ancho; x += 36) {
            g2.setColor((x / 36) % 2 == 0 ? new Color(226, 244, 241) : new Color(240, 250, 248));
            g2.fillRect(x, 0, 36, piso);
        }

        // Estantes con productos (los mismos íconos de la tienda)
        String[][] estantes = {
            {Catalogo.GORRA, Catalogo.TERMO, Catalogo.SACO, Catalogo.MOCHILA},
            {Catalogo.BALON, Catalogo.CUADERNO, Catalogo.AGUA, Catalogo.KIT_ASEO},
        };
        int tamano = Math.max(36, Math.min(56, alto / 10));
        for (int fila = 0; fila < estantes.length; fila++) {
            int y = (int) (piso * (0.46 + fila * 0.28));
            int x = 30;
            int i = 0;
            while (x + tamano < ancho - 20) {
                BufferedImage icono = IconosItems.imagen(estantes[fila][i % estantes[fila].length], tamano);
                g2.drawImage(icono, x, y - tamano + 6, null);
                x += tamano + 22;
                i++;
            }
            g2.setColor(new Color(170, 112, 64));
            g2.fillRect(0, y, ancho, 12);
            g2.setColor(new Color(128, 80, 42));
            g2.fillRect(0, y + 12, ancho, 4);
        }

        // Letrero "Merch UTadeo"
        int letreroX = 30;
        int letreroY = (int) (piso * 0.1) + 30;
        g2.setColor(Estilo.AZUL_OSCURO);
        g2.fillRoundRect(letreroX, letreroY, 210, 46, 16, 16);
        g2.setColor(Estilo.TEAL);
        g2.fillRect(letreroX + 16, letreroY + 36, 178, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(Estilo.FUENTE_TITULO);
        g2.drawString("Merch UTadeo", letreroX + 22, letreroY + 30);

        // Toldo a rayas con borde ondulado
        int alturaToldo = 26;
        for (int x = 0, i = 0; x < ancho; x += 40, i++) {
            g2.setColor(i % 2 == 0 ? Estilo.TEAL : Color.WHITE);
            g2.fillRect(x, 0, 40, alturaToldo);
            g2.fillOval(x, alturaToldo - 12, 40, 24);
        }

        // Piso de madera
        g2.setColor(new Color(204, 160, 112));
        g2.fillRect(0, piso, ancho, alto - piso);
        g2.setColor(new Color(178, 132, 86));
        for (int y = piso + 22; y < alto; y += 22) {
            g2.drawLine(0, y, ancho, y);
        }
        g2.setColor(new Color(128, 80, 42));
        g2.fillRect(0, piso - 4, ancho, 4);
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
