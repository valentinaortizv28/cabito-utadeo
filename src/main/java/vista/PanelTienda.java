package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import controlador.JuegoController;
import modelo.Cabito;
import modelo.Partida;
import modelo.items.Inventario;
import modelo.items.Item;
import modelo.items.ResultadoCompra;
import modelo.items.Tienda;

/**
 * Mostrador de la Tienda Tadeísta dentro de la escena: los productos con
 * su ícono, precio y botón Comprar, las monedas disponibles y un mensaje
 * con el resultado de la última compra.
 * <p>
 * Las filas solo se vuelven a crear cuando cambia algo que las afecta
 * (monedas, nivel u objetos comprados), así el reloj del juego no reinicia
 * la barra de desplazamiento cada 10 segundos.
 */
public class PanelTienda extends JPanel {

    /** Ancho del mostrador en píxeles. */
    public static final int ANCHO = 540;

    private static final int TAMANO_ICONO = 40;
    private static final int ANCHO_TEXTO = 135;
    private static final int RADIO = 22;

    private final JuegoController controlador;
    private final JPanel contenido;
    private final JLabel encabezado;
    private final JPanel filas;
    private final JLabel mensaje;
    private Partida partida;
    private String firma = "";

    /**
     * Crea el mostrador (sin productos hasta el primer {@link #actualizar}).
     *
     * @param controlador controlador que hace las compras
     */
    public PanelTienda(JuegoController controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(ANCHO, 0));

        contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        encabezado = new JLabel(" ");
        encabezado.setFont(Estilo.FUENTE_TITULO);
        encabezado.setForeground(Estilo.AZUL_OSCURO);
        contenido.add(encabezado, BorderLayout.NORTH);

        filas = new JPanel(new GridLayout(0, 1, 0, 6));
        filas.setOpaque(false);
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(filas, BorderLayout.NORTH);
        JScrollPane desplazamiento = new JScrollPane(arriba);
        desplazamiento.setBorder(null);
        desplazamiento.setOpaque(false);
        desplazamiento.getViewport().setOpaque(false);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        desplazamiento.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contenido.add(desplazamiento, BorderLayout.CENTER);

        mensaje = new JLabel(" ");
        mensaje.setFont(Estilo.FUENTE_NEGRITA);
        contenido.add(mensaje, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);
    }

    /**
     * Muestra u oculta los productos. Mientras Cabito camina hacia la tienda
     * el mostrador está vacío (pero sigue ocupando su espacio).
     *
     * @param visible {@code true} para mostrar los productos
     */
    public void setContenidoVisible(boolean visible) {
        contenido.setVisible(visible);
        repaint();
    }

    /**
     * @return {@code true} si los productos se están mostrando
     */
    public boolean isContenidoVisible() {
        return contenido.isVisible();
    }

    /**
     * Actualiza las monedas y, si cambió algo, vuelve a crear las filas.
     *
     * @param nuevaPartida partida en curso
     */
    public void actualizar(Partida nuevaPartida) {
        this.partida = nuevaPartida;
        Cabito cabito = partida.getCabito();
        Inventario inventario = cabito.getInventario();
        encabezado.setText(Tienda.NOMBRE + "  —  " + inventario.getMonedas() + " monedas");
        String nuevaFirma = inventario.getMonedas() + "|" + cabito.getNivel() + "|" + inventario.getCantidades();
        if (!nuevaFirma.equals(firma)) {
            firma = nuevaFirma;
            llenarFilas();
        }
    }

    /**
     * Crea una fila por cada producto: ícono, nombre, precio y botón.
     */
    private void llenarFilas() {
        filas.removeAll();
        Cabito cabito = partida.getCabito();
        Inventario inventario = cabito.getInventario();
        for (Item item : partida.getTienda().getItems()) {
            boolean desbloqueado = partida.getTienda().estaDesbloqueado(item, cabito.getNivel());
            boolean yaLoTiene = !item.esConsumible() && inventario.tiene(item.getNombre());
            String textoBoton;
            if (!desbloqueado) {
                textoBoton = "Nivel " + item.getNivelRequerido();
            } else if (yaLoTiene) {
                textoBoton = "Comprado";
            } else {
                textoBoton = "Comprar";
            }
            JLabel precio = new JLabel(item.getPrecio() + " monedas");
            precio.setFont(Estilo.FUENTE_NEGRITA);
            precio.setForeground(Estilo.AZUL_OSCURO);
            JButton boton = DialogoLista.botonFila(textoBoton, desbloqueado && !yaLoTiene, () -> comprar(item));
            String html = "<b>" + Estilo.escaparHtml(item.getNombre()) + "</b><br><font size='-1'>"
                    + Estilo.escaparHtml(item.getDescripcion()) + "</font>";
            JPanel fila = Estilo.crearFila(IconosItems.icono(item.getNombre(), TAMANO_ICONO), html, ANCHO_TEXTO,
                    precio, boton);
            fila.setToolTipText(item.getTipo() + ": " + item.getDescripcion());
            filas.add(fila);
        }
        filas.revalidate();
        filas.repaint();
    }

    /**
     * Compra un producto y muestra el resultado: verde si se compró, y un
     * mensaje amable en ámbar si no (por ejemplo, si faltan monedas).
     *
     * @param item producto
     */
    private void comprar(Item item) {
        ResultadoCompra resultado = controlador.comprar(item.getNombre());
        boolean exito = resultado == ResultadoCompra.EXITO;
        String texto = exito ? "¡Compraste " + item.getNombre() + "!"
                : controlador.mensajeCompra(resultado, item.getNombre());
        mensaje.setText(Estilo.textoAjustado(texto, ANCHO - 120));
        mensaje.setForeground(exito ? Estilo.CORRECTO : Estilo.ADVERTENCIA.darker());
    }

    /**
     * Pinta el mostrador: un panel blanco semitransparente con borde teal,
     * solo cuando los productos están visibles.
     *
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!contenido.isVisible()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 225));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIO, RADIO);
        g2.setColor(Estilo.TEAL);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIO, RADIO);
        g2.dispose();
    }

    /**
     * @return cantidad de productos mostrados (para pruebas)
     */
    int getCantidadProductos() {
        return filas.getComponentCount();
    }

    /**
     * @return texto del último mensaje (para pruebas)
     */
    String getMensaje() {
        return mensaje.getText();
    }

    /**
     * Pulsa el botón de un producto (para pruebas).
     *
     * @param indice posición del producto en la lista
     */
    void comprarProducto(int indice) {
        comprar(partida.getTienda().getItems().get(indice));
    }
}
